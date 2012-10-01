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
 * Http Presenter
 *
 * @author Vadim Spivak
 */
public class HttpPresenter extends BaseDashboardGraphPresenter<HttpPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.HTTP_PAGE)
  public interface MyProxy extends TabContentProxyPlace<HttpPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("HTTP", 2);
  }

  @Inject
  public HttpPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                       @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("http.all"));
    addGraph(graphs.getGraph("http.latency"));
    addGraph(graphs.getGraph("http.2xx"));
    addGraph(graphs.getGraph("http.3xx"));
    addGraph(graphs.getGraph("http.4xx"));
    addGraph(graphs.getGraph("http.5xx"));
    addGraph(graphs.getGraph("http.xxx"));
  }

}
