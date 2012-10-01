/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.event;

import com.cloudfoundry.dashboard.client.graph.Context;

import com.gwtplatform.dispatch.annotation.GenEvent;

/**
 * Time Zone Change
 *
 * @author Vadim Spivak
 */
@GenEvent
public class TimeZoneChange {

  Context context;

}
