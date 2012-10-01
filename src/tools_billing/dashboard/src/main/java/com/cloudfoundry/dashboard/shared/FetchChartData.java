/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.shared;

import com.gwtplatform.dispatch.shared.ActionImpl;

import java.util.List;
import java.util.Map;

public class FetchChartData extends ActionImpl<FetchChartData.Result> {

  public static class Result implements com.gwtplatform.dispatch.shared.Result {

    private Map<Metric, List<DataPoint>> points;

    public Result(Map<Metric, List<DataPoint>> points) {
      this.points = points;
    }

    @SuppressWarnings("unused")
    private Result() {
    }

    public Map<Metric, List<DataPoint>> getPoints() {
      return points;
    }

  }

  private String query;
  private long start;
  private long end;

  @SuppressWarnings("unused")
  private FetchChartData() {
  }

  public FetchChartData(String query, long start, long end) {
    this.query = query;
    this.start = start;
    this.end = end;
  }

  public long getEnd() {
    return end;
  }

  public String getQuery() {
    return query;
  }

  public long getStart() {
    return start;
  }

}
