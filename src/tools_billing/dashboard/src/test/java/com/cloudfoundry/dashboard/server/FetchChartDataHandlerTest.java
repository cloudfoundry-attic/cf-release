/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.server;

import com.cloudfoundry.dashboard.shared.DataPoint;

import com.google.common.collect.Lists;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

/**
 * FetchChartDataHandlerTest
 *
 * @author Vadim Spivak
 */
@RunWith(JUnit4.class)
public class FetchChartDataHandlerTest extends TestCase {

  @Test
  public void downsample() {
    FetchChartDataHandler handler = new FetchChartDataHandler();

    List<DataPoint> points = Lists.newArrayList();
    for (int i = 0; i < 10; i++) {
      points.add(new DataPoint(i, i * 10));
    }

    List<DataPoint> downsampledPoints = handler.downsample(3, new Aggregators.AvgAggregator(), points);
    assertEquals(downsampledPoints, Lists.newArrayList(
        new DataPoint(0, 0), new DataPoint(2, 20), new DataPoint(5, 50), new DataPoint(8, 80)));
  }

}
