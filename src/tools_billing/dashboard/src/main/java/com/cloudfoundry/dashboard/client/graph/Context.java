/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.i18n.client.TimeZone;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

import java.util.Date;
import java.util.Map;

/**
 * Context - graph context used for managing state across presenters and URL history.
 *
 * @author Vadim Spivak
 */
public class Context {

  private static final Map<Range, Long> RANGE_INTERVAL = ImmutableMap.<Range, Long>builder()
      .put(Range.RANGE_30M, 30L * 60 * 1000)
      .put(Range.RANGE_4H, 4L * 60 * 60 * 1000)
      .put(Range.RANGE_1D, 24L * 60 * 60 * 1000)
      .put(Range.RANGE_1W, 7L * 24 * 60 * 60 * 1000)
      .put(Range.RANGE_4W, 4L * 7 * 24 * 60 * 60 * 1000)
      .build();

  private final static BiMap<String, Integer> UPDATE_PERIOD_LOOKUP = ImmutableBiMap.<String, Integer>builder()
      .put("off", -1)
      .put("15s", 15 * 1000)
      .put("30s", 30 * 1000)
      .put("1m", 60 * 1000)
      .put("5m", 5 * 60 * 1000)
      .build();

  private long start = -1;

  private long end = -1;

  private long lastUpdated = -1;

  private int updatePeriod = -1;

  private Range range;

  private TimeZone timeZone;

  // Only makes sense for zoom presenter
  private String graph;

  // Only makes sense for zoom presenter
  private String[] hiddenLabels;

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public String getGraph() {
    return graph;
  }

  public void setGraph(String graph) {
    this.graph = graph;
  }

  public String[] getHiddenLabels() {
    return hiddenLabels;
  }

  public void setHiddenLabels(String[] hiddenLabels) {
    this.hiddenLabels = hiddenLabels;
  }

  public long getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(long lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Range getRange() {
    return range;
  }

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  public int getUpdatePeriod() {
    return updatePeriod;
  }

  public void setUpdatePeriod(int updatePeriod) {
    this.updatePeriod = updatePeriod;
  }

  public String getUpdatePeriodParamValue() {
    return UPDATE_PERIOD_LOOKUP.inverse().get(updatePeriod);
  }

  public boolean isInitialized() {
    return start != -1;
  }

  public void navigateBy(long amount) {
    long maxStartValue = new Date().getTime() - RANGE_INTERVAL.get(this.range);

    if (amount == 0) {
      start = new Date().getTime() - RANGE_INTERVAL.get(range);
      end = -1;
    } else {
      start = Math.min(start + amount, maxStartValue);
      end = start + RANGE_INTERVAL.get(range);
    }
  }

  public void setFromPlaceRequest(PlaceRequest request) {
    String range = request.getParameter("range", "30m");
    String start = request.getParameter("start", null);
    String timeZone = request.getParameter("tz", "local");
    String updatePeriod = request.getParameter("update", "off");
    String hidden = request.getParameter("hidden", null);

    graph = request.getParameter("graph", null);

    this.range = Range.fromParamValue(range);

    long maxStartValue = new Date().getTime() - RANGE_INTERVAL.get(this.range);

    if (start != null) {
      this.start = Math.min(Long.parseLong(start), maxStartValue);
      this.end = this.start + RANGE_INTERVAL.get(this.range);
    } else {
      this.start = maxStartValue;
      this.end = -1;
    }

    setTimeZoneFromParamValue(timeZone);
    setUpdatePeriodFromParamValue(updatePeriod);
    setHiddenLabelsFromParamValue(hidden);
  }

  private void setHiddenLabelsFromParamValue(String hidden) {
    if (hidden != null && !hidden.isEmpty()) {
      hiddenLabels = hidden.split(",");
    } else {
      hiddenLabels = null;
    }
  }

  @SuppressWarnings({"deprecation"})
  public void setTimeZoneFromParamValue(String value) {
    if (value.equals("local")) {
      timeZone = TimeZone.createTimeZone(new Date().getTimezoneOffset());
    } else {
      timeZone = TimeZone.createTimeZone(0);
    }
  }

  public void setUpdatePeriodFromParamValue(String value) {
    Integer updatePeriod = UPDATE_PERIOD_LOOKUP.get(value);
    if (updatePeriod != null) {
      this.updatePeriod = updatePeriod;
    } else {
      this.updatePeriod = -1;
    }
  }

  public void setRange(Range range) {
    setRange(range, false);
  }

  public void setRange(Range range, boolean updateTime) {
    this.range = range;
    if (updateTime) {
      long syntheticEnd = end != -1 ? end : new Date().getTime();
      start = syntheticEnd - RANGE_INTERVAL.get(range);
    }
  }

  public PlaceRequest updatePlaceRequest(PlaceRequest request) {
    request = request
        .with("range", getRange().getParamValue())
        .with("start", String.valueOf(start))
        .with("tz", getTimeZoneParamValue());

    if (graph != null) {
      request = request
          .with("graph", graph)
          .with("hidden", getHiddenLabelsParamValue());
    }

    return request;
  }

  public String getHiddenLabelsParamValue() {
    if (hiddenLabels == null) {
      return "";
    } else {
      return Joiner.on(",").join(hiddenLabels);
    }
  }

  public String getTimeZoneParamValue() {
    return timeZone.getStandardOffset() == 0 ? "UTC" : "local";
  }

}
