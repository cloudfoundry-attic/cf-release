/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph.axis;

import com.google.inject.Singleton;
import com.googlecode.gchart.client.GChart;

/**
 * Simple Tick Label Formatter
 *
 * @author Oleg Shaldybin
 */
@Singleton
public class PercentTickLabelFormatter implements TickLabelFormatter {

  @Override
  public String format(GChart.Axis axis, double value) {
    return axis.formatAsTickLabel(value) + "%";
  }

}
