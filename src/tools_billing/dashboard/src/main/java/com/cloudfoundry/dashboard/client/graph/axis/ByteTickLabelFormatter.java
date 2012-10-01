/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph.axis;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Singleton;
import com.googlecode.gchart.client.GChart;

/**
 * Byte Tick Label Formatter - formats the Y axis for data sources measured in bytes.
 *
 * @author Vadim Spivak
 */
@Singleton
public class ByteTickLabelFormatter implements TickLabelFormatter {

  String[] UNITS = {"KB", "MB", "GB", "TB", "PB", "EB", "ZB"};

  NumberFormat format = NumberFormat.getFormat("#.## ");

  private final int initialUnitOffset;

  public ByteTickLabelFormatter(String initialUnit) {
    for (int i = 0; i < UNITS.length; i++) {
      String unit = UNITS[i];
      if (initialUnit.equalsIgnoreCase(unit)) {
        this.initialUnitOffset = i;
        return;
      }
    }

    throw new IllegalArgumentException("initialUnitOffset");
  }

  @Override
  public String format(GChart.Axis axis, double value) {
    int index;
    for (index = initialUnitOffset; index < UNITS.length; index++) {
      if (value < 1024) {
        return format.format(value) + UNITS[index];
      }
      value /= 1024;
    }

    return format.format(value) + UNITS[index];
  }

}
