/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.server;

import com.cloudfoundry.dashboard.shared.DataPoint;
import com.cloudfoundry.dashboard.shared.FetchChartData;
import com.cloudfoundry.dashboard.shared.Metric;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;

@Component
public class FetchChartDataHandler implements
    ActionHandler<FetchChartData, FetchChartData.Result> {

  private static final Logger logger = LoggerFactory.getLogger(FetchChartDataHandler.class);

  private static final Pattern INTERVAL_PATTERN = Pattern.compile("(\\d+)([a-z])-([a-z]+)");

  private static final Map<String, Long> UNIT_LOOKUP = ImmutableMap.<String, Long>builder()
      .put("s", 1000L)
      .put("m", 60L * 1000)
      .put("h", 60L * 60 * 1000)
      .put("d", 24L * 60 * 60 * 1000)
      .put("w", 7L * 24 * 60 * 60 * 1000)
      .put("y", 365L * 7 * 24 * 60 * 60 * 1000)
      .build();

  private static final Map<String, Aggregator> AGGREGATORS = ImmutableMap.<String, Aggregator>builder()
      .put("max", new Aggregators.MaxAggregator())
      .put("min", new Aggregators.MinAggregator())
      .put("avg", new Aggregators.AvgAggregator())
      .put("sum", new Aggregators.SumAggregator())
      .build();

  @Resource(name = "tsdb")
  private URI tsdbUri;

  @Override
  public FetchChartData.Result execute(FetchChartData action, ExecutionContext context) throws ActionException {
    long start = System.currentTimeMillis();
    // set the connection timeout value to 30 seconds (30000 milliseconds)
    final HttpParams httpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
    HttpClient httpClient = new DefaultHttpClient(httpParams);
    try {
      URI uri = buildUri(action);
      HttpGet httpGet = new HttpGet(uri);

      long maxTimestampDelta = calculateMaxTimestampDelta(action);

      logger.debug("Fetching: {}", uri.toString());

      HttpResponse response = httpClient.execute(httpGet);

      long duration = System.currentTimeMillis() - start;
      logger.debug("Received response in: {}ms", duration);

      HttpEntity entity = response.getEntity();

      if (entity != null) {
        Map<Metric, List<DataPoint>> points = Maps.newHashMap();

        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        try {
          String line;
          while ((line = reader.readLine()) != null) {
            processLine(action, points, line, maxTimestampDelta);
          }
        } finally {
          reader.close();
        }

        normalize(action.getQuery(), points);

        return new FetchChartData.Result(points);
      }
      throw new ActionException("Invalid TSDB response: " + response.getStatusLine().getStatusCode());
    } catch (URISyntaxException e) {
      throw new ActionException("Error generating backend URI.", e);
    } catch (ClientProtocolException e) {
      throw new ActionException("Error making TSDB request.", e);
    } catch (IOException e) {
      throw new ActionException("Error making TSDB request.", e);
    }
  }

  protected URI buildUri(FetchChartData action) throws URISyntaxException {
    List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
    queryParams.add(new BasicNameValuePair("ascii", null));
    queryParams.add(new BasicNameValuePair("start", formatDate(action.getStart())));
    if (action.getEnd() != -1) {
      queryParams.add(new BasicNameValuePair("end", formatDate(action.getEnd())));
    }
    queryParams.add(new BasicNameValuePair("m", action.getQuery()));

    return URIUtils.createURI("http", tsdbUri.getHost(), tsdbUri.getPort(), "/q",
        URLEncodedUtils.format(queryParams, "UTF-8"), null);
  }

  protected String formatDate(long timestamp) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss", Locale.ENGLISH);
    formatter.setTimeZone(TimeZone.getTimeZone("U"));
    return formatter.format(timestamp);
  }

  protected long calculateMaxTimestampDelta(FetchChartData action) {
    long end;
    if (action.getEnd() != -1) {
      end = action.getEnd();
    } else {
      end = new Date().getTime();
    }

    long delta = Math.abs(end - action.getStart());
    return delta / 5;
  }

  /**
   * Removes the last data point in each set and replaces it with the previous value.
   * This is necessary for some values that are still incomplete.
   *
   * @param query associated query.
   * @param data data to normalize.
   */
  protected void normalize(String query, Map<Metric, List<DataPoint>> data) {
    for (Map.Entry<Metric, List<DataPoint>> entry : data.entrySet()) {
      List<DataPoint> points = entry.getValue();
      if (points.size() > 1) {
        Collections.sort(points, new Comparator<DataPoint>() {

          @Override
          public int compare(DataPoint o1, DataPoint o2) {
            long result = o1.getTimestamp() - o2.getTimestamp();
            if (result < 0) {
              return -1;
            } else if (result > 0) {
              return 1;
            } else {
              return 0;
            }
          }

        });

        //AGG:[interval-DOWNSAMPLE:][rate:]metric[{tag1=value1[,tag2=value2...]}]
        String[] parts = query.split(":");
        if (parts.length > 2 && !parts[1].equals("rate")) {
          Matcher matcher = INTERVAL_PATTERN.matcher(parts[1]);
          if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);
            String aggregatorName = matcher.group(3);

            Long unitMultiplier = UNIT_LOOKUP.get(unit);
            Aggregator aggregator = AGGREGATORS.get(aggregatorName);
            if (unitMultiplier != null && aggregator != null) {
              value *= unitMultiplier;
              List<DataPoint> downsampledPoints = downsample(value, aggregator, points);
              stripUnstableData(value, downsampledPoints);
              entry.setValue(downsampledPoints);
            }
          }
        }
      }
    }
  }

  protected void stripUnstableData(long duration, List<DataPoint> points) {
    int lastIndex = points.size() - 1;
    DataPoint last = points.get(lastIndex);
    if (new Date().getTime() - last.getTimestamp() < duration) {
      points.remove(lastIndex);
    }
  }

  protected List<DataPoint> downsample(long duration, Aggregator aggregator, List<DataPoint> points) {
    List<DataPoint> result = Lists.newArrayList();

    int offset = 0;
    long maxSampleTimestamp = 0;

    for (int i = 0; i < points.size(); i++) {
      DataPoint dataPoint = points.get(i);
      if (dataPoint.getTimestamp() > maxSampleTimestamp) {
        if (i > offset) {
          result.add(aggregator.aggregate(points, offset, i));
        }

        offset = i;
        long timestampDelta = dataPoint.getTimestamp() - maxSampleTimestamp;
        maxSampleTimestamp += Math.ceil((double)timestampDelta / duration) * duration;
      }
    }

    if (points.size() > offset) {
      result.add(aggregator.aggregate(points, offset, points.size()));
    }

    return result;
  }

  private void processLine(FetchChartData action, Map<Metric, List<DataPoint>> points, String line,
                           long maxTimestampDelta) {
    String[] parts = line.split(" ", 4);

    long timestamp = Long.parseLong(parts[1]) * 1000;
    if (action.getStart() - timestamp > maxTimestampDelta ||
        (action.getEnd() != -1 && timestamp - action.getEnd() > maxTimestampDelta)) {
      return;
    }

    String key = parts[0];
    String value = parts[2];

    Map<String, String> tags;
    if (parts.length < 4) {
      tags = Collections.emptyMap();
    } else {
      tags = parseTags(parts[3]);
    }

    Metric metric = new Metric(key, tags);

    List<DataPoint> dataPoints = points.get(metric);
    if (dataPoints == null) {
      dataPoints = Lists.newArrayList();
      points.put(metric, dataPoints);
    }

    dataPoints.add(new DataPoint(timestamp, Double.parseDouble(value)));
  }

  protected HashMap<String, String> parseTags(String tags) {
    HashMap<String, String> result = new HashMap<String, String>();
    String[] splitTags = tags.split(" ");
    for (String tag : splitTags) {
      String[] parts = tag.split("=", 2);
      result.put(parts[0], parts[1]);
    }
    return result;
  }

  @Override
  public Class<FetchChartData> getActionType() {
    return FetchChartData.class;
  }

  @Override
  public void undo(FetchChartData action, FetchChartData.Result result, ExecutionContext context)
      throws ActionException {
    // Nothing to undo
  }

}
