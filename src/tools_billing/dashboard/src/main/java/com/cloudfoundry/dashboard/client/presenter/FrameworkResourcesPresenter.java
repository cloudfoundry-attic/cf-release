/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.cloudfoundry.dashboard.client.NameTokens;
import com.cloudfoundry.dashboard.client.gin.ClientGinjector;
import com.cloudfoundry.dashboard.client.graph.Context;
import com.cloudfoundry.dashboard.client.graph.Graphs;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.TabDataBasic;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;

/**
 * FrameworkResourcesPresenter
 *
 * @author Vadim Spivak
 */
public class FrameworkResourcesPresenter extends BaseDashboardGraphPresenter<FrameworkResourcesPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.FRAMEWORK_RESOURCES_PAGE)
  public interface MyProxy extends TabContentProxyPlace<FrameworkResourcesPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("Framework Resources", 5);
  }

  @Inject
  public FrameworkResourcesPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                                     @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("frameworks.memory"));
    addGraph(graphs.getGraph("frameworks.started_memory"));
    addGraph(graphs.getGraph("frameworks.reserved_memory"));
    addGraph(graphs.getGraph("frameworks.used_memory"));
    addGraph(graphs.getGraph("frameworks.used_cpu"));
    addGraph(graphs.getGraph("frameworks.used_disk"));
  }

}
