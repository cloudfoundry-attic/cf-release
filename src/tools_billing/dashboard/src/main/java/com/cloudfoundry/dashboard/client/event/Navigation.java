/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.event;

import com.cloudfoundry.dashboard.client.graph.Context;

import com.gwtplatform.dispatch.annotation.GenEvent;
import com.gwtplatform.dispatch.annotation.Order;

/**
 * Navigation
 *
 * @author Vadim Spivak
 */
@GenEvent
public class Navigation {

  @Order(1)
  Context context;
  @Order(2)
  long amount;

}
