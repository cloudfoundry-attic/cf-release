/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.util;

import com.google.inject.Singleton;

/**
 * GlobalPendingRequests
 *
 * @author Vadim Spivak
 */
@Singleton
public class GlobalPendingRequests {

  private int requests;

  public GlobalPendingRequests() {
    requests = 0;
  }

  public int decrement() {
    return --requests;
  }

  public int increment() {
    return ++requests;
  }

}
