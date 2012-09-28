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
 * BillingPresenter
 *
 * @author Vadim Spivak
 */
public class BillingPresenter extends BaseDashboardGraphPresenter<BillingPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.BILLING_PAGE)
  public interface MyProxy extends TabContentProxyPlace<BillingPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTabLabel(ClientGinjector ginjector) {
    return new TabDataBasic("Billing Services", 5);
  }

  @Inject
  public BillingPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                                     @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);
    addGraph(graphs.getGraph("billing.requests"));
    addGraph(graphs.getGraph("billing.success"));
    addGraph(graphs.getGraph("billing.cpu"));
    addGraph(graphs.getGraph("billing.uptime"));
    addGraph(graphs.getGraph("billing.errors"));
  }

}
