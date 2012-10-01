/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.server;

import com.cloudfoundry.dashboard.shared.DataPoint;

import java.util.List;

/**
 * Aggregator
 *
 * @author Vadim Spivak
 */
public interface Aggregator {

  DataPoint aggregate(List<DataPoint> points, int start, int end);

}
