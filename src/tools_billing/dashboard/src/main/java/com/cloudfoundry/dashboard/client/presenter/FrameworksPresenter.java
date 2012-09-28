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
 * Frameworks Presenter
 *
 * @author Vadim Spivak
 */
public class FrameworksPresenter extends BaseDashboardGraphPresenter<FrameworksPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.FRAMEWORKS_PAGE)
  public interface MyProxy extends TabContentProxyPlace<FrameworksPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("Frameworks", 3);
  }

  @Inject
  public FrameworksPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                             @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("frameworks.apps"));
    addGraph(graphs.getGraph("frameworks.instances"));
    addGraph(graphs.getGraph("frameworks.started_apps"));
    addGraph(graphs.getGraph("frameworks.started_instances"));
    addGraph(graphs.getGraph("frameworks.running_apps"));
    addGraph(graphs.getGraph("frameworks.running_instances"));
    addGraph(graphs.getGraph("frameworks.flapping_instances"));
    addGraph(graphs.getGraph("frameworks.missing_instances"));
    addGraph(graphs.getGraph("frameworks.crashes"));
  }

}
