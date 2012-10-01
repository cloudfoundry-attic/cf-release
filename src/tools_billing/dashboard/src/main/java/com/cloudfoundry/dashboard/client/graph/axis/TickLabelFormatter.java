/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph.axis;

import com.googlecode.gchart.client.GChart;

/**
 * Tick Label Formatter
 *
 * @author Vadim Spivak
 */
public interface TickLabelFormatter {

  String format(GChart.Axis axis, double value);

}
