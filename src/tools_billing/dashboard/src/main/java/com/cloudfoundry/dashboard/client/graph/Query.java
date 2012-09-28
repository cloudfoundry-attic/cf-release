/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph;

import com.cloudfoundry.dashboard.client.graph.label.LabelExtractor;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Query
 *
 * @author Vadim Spivak
 */
public class Query {

  public enum Aggregator {

    MIN, SUM, MAX, AVG;

    public String getTsdbName() {
      return name().toLowerCase();
    }

  }

  public static class Builder {

    private Aggregator aggregator;
    private Aggregator downsamplingAggregator;
    private boolean rate = false;
    private String metric;
    private Map<String, String> tags = null;
    private LabelExtractor labelExtractor;
    private int priority = 0;
    private boolean includeAllLine = false;

    public Builder setAggregator(Aggregator aggregator) {
      this.aggregator = aggregator;
      return this;
    }

    public Builder setDownsamplingAggregator(Aggregator downsamplingAggregator) {
      this.downsamplingAggregator = downsamplingAggregator;
      return this;
    }

    public Builder setRate(boolean rate) {
      this.rate = rate;
      return this;
    }

    public Builder setMetric(String metric) {
      this.metric = metric;
      return this;
    }

    public Builder setTags(Map<String, String> tags) {
      this.tags = tags;
      return this;
    }

    public Builder setLabelExtractor(LabelExtractor labelExtractor) {
      this.labelExtractor = labelExtractor;
      return this;
    }

    public Builder setPriority(int priority) {
      this.priority = priority;
      return this;
    }

    public Builder setIncludeAllLine(boolean includeAllLine) {
      this.includeAllLine = includeAllLine;
      return this;
    }

    public Query createQuery() {
      return new Query(aggregator, downsamplingAggregator, rate, metric, tags,
          labelExtractor, priority, includeAllLine);
    }

  }

  private static final Map<Range, String> RANGE_DOWNSAMPLING_INTERVAL = ImmutableMap.<Range, String>builder()
      .put(Range.RANGE_30M, "1m")
      .put(Range.RANGE_4H, "10m")
      .put(Range.RANGE_1D, "1h")
      .put(Range.RANGE_1W, "8h")
      .put(Range.RANGE_4W, "1d")
      .build();

  private final Aggregator aggregator;

  private final Aggregator downsamplingAggregator;

  private final boolean rate;

  private final String metric;

  private final Map<String, String> tags;

  private final LabelExtractor labelExtractor;

  private final int priority;

  private final boolean includeAllLine;

  public Query(Aggregator aggregator, Aggregator downsamplingAggregator,
               boolean rate, String metric, Map<String, String> tags,
               LabelExtractor labelExtractor, int priority,
               boolean includeAllLine) {
    this.aggregator = aggregator;
    this.downsamplingAggregator = downsamplingAggregator;
    this.rate = rate;
    this.metric = metric;
    this.tags = tags;
    this.labelExtractor = labelExtractor;
    this.priority = priority;
    this.includeAllLine = includeAllLine;
  }

  public boolean getIncludeAllLine() {
    return includeAllLine;
  }

  public Aggregator getAggregator() {
    return aggregator;
  }

  public Aggregator getDownsamplingAggregator() {
    return downsamplingAggregator;
  }

  public LabelExtractor getLabelExtractor() {
    return labelExtractor;
  }

  public String getMetric() {
    return metric;
  }

  public int getPriority() {
    return priority;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public boolean isRate() {
    return rate;
  }

  public String toTsdbQuery(Range range) {
    StringBuilder sb = new StringBuilder();
    sb.append(aggregator.getTsdbName()).append(":");
    if (downsamplingAggregator != null) {
      sb.append(RANGE_DOWNSAMPLING_INTERVAL.get(range))
          .append("-").append(downsamplingAggregator.getTsdbName())
          .append(":");
    }

    if (rate) {
      sb.append("rate:");
    }

    sb.append(metric);

    if (tags != null && tags.size() > 0) {
      sb.append("{");
      for (Map.Entry<String, String> tag : tags.entrySet()) {
        sb.append(tag.getKey()).append("=").append(tag.getValue()).append(",");
      }

      // Removes the trailing comma
      sb.setLength(sb.length() - 1);

      sb.append("}");
    }
    return sb.toString();
  }

}
