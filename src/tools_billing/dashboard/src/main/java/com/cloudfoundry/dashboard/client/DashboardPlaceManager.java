/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client;

import com.cloudfoundry.dashboard.client.gin.DefaultPlace;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

public class DashboardPlaceManager extends PlaceManagerImpl {

  private final PlaceRequest defaultPlaceRequest;

  @Inject
  public DashboardPlaceManager(final EventBus eventBus,
                               final TokenFormatter tokenFormatter, @DefaultPlace String defaultNameToken) {
    super(eventBus, tokenFormatter);
    this.defaultPlaceRequest = new PlaceRequest(defaultNameToken);
  }

  @Override
  public void revealDefaultPlace() {
    revealPlace(defaultPlaceRequest, false);
  }

}
