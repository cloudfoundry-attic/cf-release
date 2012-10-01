/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.gin;

import com.cloudfoundry.dashboard.client.presenter.*;
import com.cloudfoundry.dashboard.client.resources.Resources;
import com.cloudfoundry.dashboard.client.ui.HorizontalOptionSelector;
import com.cloudfoundry.dashboard.client.ui.NavigationBar;
import com.cloudfoundry.dashboard.client.ui.NavigationButton;
import com.cloudfoundry.dashboard.client.ui.StyledCheckbox;
import com.cloudfoundry.dashboard.client.ui.StyledTabPanel;
import com.cloudfoundry.dashboard.client.ui.UiModule;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

@GinModules({DispatchAsyncModule.class, ClientModule.class, UiModule.class})
public interface ClientGinjector extends Ginjector {
  PlaceManager getPlaceManager();

  Resources getResources();

  EventBus getEventBus();

  Provider<DashboardControlPresenter> getDashboardControlPresenter();

  Provider<DashboardPresenter> getDashboardPresenter();

  Provider<FrameworkHttpPresenter> getFrameworkHttpPresenter();

  Provider<FrameworkResourcesPresenter> getFrameworkResourcesPresenter();

  Provider<FrameworksPresenter> getFrameworksPresenter();

  Provider<GraphZoomPresenter> getGraphZoomPresenter();

  Provider<HttpPresenter> getHttpPresenter();

  Provider<MainPagePresenter> getMainPagePresenter();

  Provider<RuntimeHttpPresenter> getRuntimeHttpPresenter();

  Provider<RuntimeResourcesPresenter> getRuntimeResourcesPresenter();

  Provider<RuntimesPresenter> getRuntimesPresenter();

  Provider<SummaryPresenter> getSummaryPresenter();

  Provider<SystemPresenter> getSystemPresenter();
  
  Provider<BillingPresenter> getBillingPresenter();

  NavigationBar getNavigationBar();

  NavigationButton getNavigationButton();

  StyledCheckbox getStyledCheckbox();

  StyledTabPanel getStyledTabPanel();

  HorizontalOptionSelector getHorizontalOptionSelector();
}
