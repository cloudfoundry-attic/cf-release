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
 * Framework Resources Presenter
 *
 * @author Vadim Spivak
 */
public class FrameworkHttpPresenter extends BaseDashboardGraphPresenter<FrameworkHttpPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.FRAMEWORK_HTTP_PAGE)
  public interface MyProxy extends TabContentProxyPlace<FrameworkHttpPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("Framework HTTP", 7);
  }

  @Inject
  public FrameworkHttpPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                                @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("frameworks.http_all"));
    addGraph(graphs.getGraph("frameworks.http_2xx"));
    addGraph(graphs.getGraph("frameworks.http_3xx"));
    addGraph(graphs.getGraph("frameworks.http_4xx"));
    addGraph(graphs.getGraph("frameworks.http_5xx"));
    addGraph(graphs.getGraph("frameworks.http_xxx"));
    addGraph(graphs.getGraph("frameworks.http_latency"));
  }

}
