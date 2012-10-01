/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.event;

import com.cloudfoundry.dashboard.client.graph.Context;

import com.gwtplatform.dispatch.annotation.GenEvent;

/**
 * Range Change
 *
 * @author Vadim Spivak
 */
@GenEvent
public class RangeChange {

  Context context;

}
