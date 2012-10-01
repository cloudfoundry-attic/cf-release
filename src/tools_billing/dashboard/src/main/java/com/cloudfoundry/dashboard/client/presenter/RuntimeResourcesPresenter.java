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
 * RuntimeResourcesPresenter
 *
 * @author Vadim Spivak
 */
public class RuntimeResourcesPresenter extends BaseDashboardGraphPresenter<RuntimeResourcesPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.RUNTIME_RESOURCES_PAGE)
  public interface MyProxy extends TabContentProxyPlace<RuntimeResourcesPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("Runtime Resources", 6);
  }

  @Inject
  public RuntimeResourcesPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                                   @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("runtimes.memory"));
    addGraph(graphs.getGraph("runtimes.started_memory"));
    addGraph(graphs.getGraph("runtimes.reserved_memory"));
    addGraph(graphs.getGraph("runtimes.used_memory"));
    addGraph(graphs.getGraph("runtimes.used_cpu"));
    addGraph(graphs.getGraph("runtimes.used_disk"));
  }

}
