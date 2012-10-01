/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.event;

import com.gwtplatform.dispatch.annotation.GenEvent;
import com.gwtplatform.dispatch.annotation.Order;

/**
 * Data Update
 *
 * @author Vadim Spivak
 */
@GenEvent
public class DataUpdate {

  @Order(1)
  boolean finished;
  @Order(2)
  boolean successful;

}
