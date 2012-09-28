/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DataPoint implements IsSerializable {

  private long timestamp;

  private double value;

  @SuppressWarnings("unused")
  private DataPoint() {
  }

  public DataPoint(long timestamp, double value) {
    this.timestamp = timestamp;
    this.value = value;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public double getValue() {
    return value;
  }

  @Override
  public String toString() {
    return new StringBuilder("DataPoint{")
        .append("timestamp=")
        .append(timestamp)
        .append(", value=")
        .append(value)
        .append('}')
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DataPoint dataPoint = (DataPoint) o;

    if (timestamp != dataPoint.timestamp) return false;
    if (Double.compare(dataPoint.value, value) != 0) return false;

    return true;
  }

}
