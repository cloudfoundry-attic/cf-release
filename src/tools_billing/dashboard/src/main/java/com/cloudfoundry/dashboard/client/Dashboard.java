/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client;

import com.cloudfoundry.dashboard.client.gin.ClientGinjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

public class Dashboard implements EntryPoint {

  public final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

  public void onModuleLoad() {
    DelayedBindRegistry.bind(ginjector);
    ginjector.getPlaceManager().revealCurrentPlace();
  }

}
