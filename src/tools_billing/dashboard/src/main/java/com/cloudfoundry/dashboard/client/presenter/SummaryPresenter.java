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
 * Summary Presenter
 *
 * @author Vadim Spivak
 */
public class SummaryPresenter extends BaseDashboardGraphPresenter<SummaryPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.SUMMARY_PAGE)
  public interface MyProxy extends TabContentProxyPlace<SummaryPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("Summary", 0);
  }

  @Inject
  public SummaryPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                          @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("summary.expected_vitals"));
    addGraph(graphs.getGraph("summary.actual_vitals"));
    addGraph(graphs.getGraph("summary.requests"));
    addGraph(graphs.getGraph("summary.capacity"));
    addGraph(graphs.getGraph("summary.components"));
    addGraph(graphs.getGraph("summary.components_memory"));
  }

}
