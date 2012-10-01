/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.server;

import com.cloudfoundry.dashboard.shared.DataPoint;

import java.util.List;

/**
 * Aggregators
 *
 * @author Vadim Spivak
 */
public class Aggregators {

  public static class SumAggregator implements Aggregator {

    @Override
    public DataPoint aggregate(List<DataPoint> points, int start, int end) {
      double value = 0;
      long timestamp = 0;

      for (int i = start; i < end; i++) {
        DataPoint dataPoint = points.get(i);
        value += dataPoint.getValue();
        timestamp += dataPoint.getTimestamp();
      }

      return new DataPoint(timestamp / (end - start), value);
    }
  }

  public static class AvgAggregator implements Aggregator {

    @Override
    public DataPoint aggregate(List<DataPoint> points, int start, int end) {
      double value = 0;
      long timestamp = 0;

      for (int i = start; i < end; i++) {
        DataPoint dataPoint = points.get(i);
        value += dataPoint.getValue();
        timestamp += dataPoint.getTimestamp();
      }

      return new DataPoint(timestamp / (end - start), value / (end - start));
    }
  }

  public static class MinAggregator implements Aggregator {

    @Override
    public DataPoint aggregate(List<DataPoint> points, int start, int end) {
      double value = Double.MAX_VALUE;
      long timestamp = 0;

      for (int i = start; i < end; i++) {
        DataPoint dataPoint = points.get(i);
        value = Math.min(value, dataPoint.getValue());
        timestamp += dataPoint.getTimestamp();
      }

      return new DataPoint(timestamp / (end - start), value);
    }
  }

  public static class MaxAggregator implements Aggregator {

    @Override
    public DataPoint aggregate(List<DataPoint> points, int start, int end) {
      double value = Double.MIN_VALUE;
      long timestamp = 0;

      for (int i = start; i < end; i++) {
        DataPoint dataPoint = points.get(i);
        value = Math.max(value, dataPoint.getValue());
        timestamp += dataPoint.getTimestamp();
      }

      return new DataPoint(timestamp / (end - start), value);
    }
  }

}
