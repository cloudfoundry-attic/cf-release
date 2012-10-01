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
 * Runtimes Presenter
 *
 * @author Vadim Spivak
 */
public class RuntimesPresenter extends BaseDashboardGraphPresenter<RuntimesPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.RUNTIMES_PAGE)
  public interface MyProxy extends TabContentProxyPlace<RuntimesPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("Runtimes", 4);
  }

  @Inject
  public RuntimesPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                           @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("runtimes.apps"));
    addGraph(graphs.getGraph("runtimes.instances"));
    addGraph(graphs.getGraph("runtimes.started_apps"));
    addGraph(graphs.getGraph("runtimes.started_instances"));
    addGraph(graphs.getGraph("runtimes.running_apps"));
    addGraph(graphs.getGraph("runtimes.running_instances"));
    addGraph(graphs.getGraph("runtimes.flapping_instances"));
    addGraph(graphs.getGraph("runtimes.missing_instances"));
    addGraph(graphs.getGraph("runtimes.crashes"));
  }

}
