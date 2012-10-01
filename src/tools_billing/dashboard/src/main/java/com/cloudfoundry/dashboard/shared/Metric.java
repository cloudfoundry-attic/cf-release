/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Map;

/**
 * Metric
 *
 * @author Vadim Spivak
 */
public class Metric implements IsSerializable {

  private String name;
  private Map<String, String> tags;

  public Metric(String name, Map<String, String> tags) {
    this.name = name;
    this.tags = tags;
  }

  @SuppressWarnings("unused")
  public Metric() {
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Metric metric = (Metric) o;

    if (!name.equals(metric.name)) return false;
    if (!tags.equals(metric.tags)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + tags.hashCode();
    return result;
  }
}
