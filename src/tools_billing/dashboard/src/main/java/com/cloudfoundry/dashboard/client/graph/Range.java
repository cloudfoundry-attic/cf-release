/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * Range
 *
 * @author Vadim Spivak
 */
public enum Range {

  RANGE_30M("30m"),
  RANGE_4H("4h"),
  RANGE_1D("1d"),
  RANGE_1W("1w"),
  RANGE_4W("4w");

  private static Map<String, Range> paramValueMap;

  private final String paramValue;

  static {
    paramValueMap = Maps.newHashMap();
    for (Range range : Range.values()) {
      paramValueMap.put(range.paramValue, range);
    }
    paramValueMap = Collections.unmodifiableMap(paramValueMap);
  }

  public static Range fromParamValue(String paramValue) {
    return paramValueMap.get(paramValue);
  }

  Range(String paramValue) {
    this.paramValue = paramValue;
  }

  public String getParamValue() {
    return paramValue;
  }

}
