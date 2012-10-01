/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.util;

/**
 * Prioritized Label
 *
 * @author Vadim Spivak
 */
public class PrioritizedLabel implements Comparable<PrioritizedLabel> {

  private final String label;

  private final int priority;

  public static PrioritizedLabel fromParamValue(String paramValue) {
    int index = paramValue.indexOf("/");
    return new PrioritizedLabel(paramValue.substring(index + 1), Integer.parseInt(paramValue.substring(0, index)));
  }

  public PrioritizedLabel(String label, int priority) {
    this.label = label;
    this.priority = priority;
  }

  public String getLabel() {
    return label;
  }

  public int getPriority() {
    return priority;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PrioritizedLabel that = (PrioritizedLabel) o;

    if (priority != that.priority) return false;
    if (!label.equals(that.label)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = label.hashCode();
    result = 31 * result + priority;
    return result;
  }

  @Override
  public int compareTo(PrioritizedLabel o) {
    int result = priority - o.priority;
    if (result == 0) {
      result = label.compareTo(o.getLabel());
    }
    return result;
  }

  public String toParamValue() {
    return priority + "/" + label;
  }

}
