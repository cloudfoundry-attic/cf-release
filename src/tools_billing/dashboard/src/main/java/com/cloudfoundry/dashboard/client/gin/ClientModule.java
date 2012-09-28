/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.gin;

import com.cloudfoundry.dashboard.client.DashboardPlaceManager;
import com.cloudfoundry.dashboard.client.NameTokens;
import com.cloudfoundry.dashboard.client.graph.Context;
import com.cloudfoundry.dashboard.client.graph.axis.ByteTickLabelFormatter;
import com.cloudfoundry.dashboard.client.graph.axis.PercentTickLabelFormatter;
import com.cloudfoundry.dashboard.client.presenter.*;
import com.cloudfoundry.dashboard.client.ui.StyledTab;
import com.cloudfoundry.dashboard.client.view.BaseDashboardGraphView;
import com.cloudfoundry.dashboard.client.view.DashboardControlView;
import com.cloudfoundry.dashboard.client.view.DashboardView;
import com.cloudfoundry.dashboard.client.view.GraphView;
import com.cloudfoundry.dashboard.client.view.GraphZoomView;
import com.cloudfoundry.dashboard.client.view.MainPageView;
import com.cloudfoundry.dashboard.client.view.ShowLinkView;

import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.gwtplatform.dispatch.shared.SecurityCookie;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

public class ClientModule extends AbstractPresenterModule {

  @Override
  protected void configure() {
    install(new DefaultModule(DashboardPlaceManager.class));

    install(new GinFactoryModuleBuilder().build(StyledTab.Factory.class));

    bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.SUMMARY_PAGE);
    bindConstant().annotatedWith(SecurityCookie.class).to("XSRF");

    bindPresenter(MainPagePresenter.class, MainPagePresenter.MyView.class,
        MainPageView.class, MainPagePresenter.MyProxy.class);

    bindPresenter(GraphZoomPresenter.class, GraphZoomPresenter.MyView.class,
        GraphZoomView.class, GraphZoomPresenter.MyProxy.class);

    bindPresenter(DashboardControlPresenter.class, DashboardControlPresenter.MyView.class,
        DashboardControlView.class, DashboardControlPresenter.MyProxy.class);
    bindPresenter(DashboardPresenter.class, DashboardPresenter.MyView.class,
        DashboardView.class, DashboardPresenter.MyProxy.class);

    bindSharedView(BaseDashboardGraphPresenter.MyView.class, BaseDashboardGraphView.class);
    bindPresenter(SummaryPresenter.class, SummaryPresenter.MyProxy.class);
    bindPresenter(SystemPresenter.class, SystemPresenter.MyProxy.class);
    bindPresenter(HttpPresenter.class, HttpPresenter.MyProxy.class);
    bindPresenter(FrameworksPresenter.class, FrameworksPresenter.MyProxy.class);
    bindPresenter(RuntimesPresenter.class, RuntimesPresenter.MyProxy.class);
    bindPresenter(FrameworkResourcesPresenter.class, FrameworkResourcesPresenter.MyProxy.class);
    bindPresenter(RuntimeResourcesPresenter.class, RuntimeResourcesPresenter.MyProxy.class);
    bindPresenter(FrameworkHttpPresenter.class, FrameworkHttpPresenter.MyProxy.class);
    bindPresenter(RuntimeHttpPresenter.class, RuntimeHttpPresenter.MyProxy.class);
    bindPresenter(BillingPresenter.class, BillingPresenter.MyProxy.class);

    bindPresenterWidget(GraphPresenter.class, GraphPresenter.MyView.class, GraphView.class);
    bindPresenterWidget(ShowLinkPresenterWidget.class, ShowLinkPresenterWidget.MyView.class, ShowLinkView.class);
  }

  @Provides
  @Singleton
  @Named("dashboard")
  public Context getDashboardContext() {
    return new Context();
  }

  @Provides
  @Singleton
  @Named("KbFormatter")
  public ByteTickLabelFormatter getKbTickLabelFormatter() {
    return new ByteTickLabelFormatter("KB");
  }

  @Provides
  @Singleton
  @Named("MbFormatter")
  public ByteTickLabelFormatter getMbTickLabelFormatter() {
    return new ByteTickLabelFormatter("MB");
  }

  @Provides
  @Singleton
  @Named("PercentFormatter")
  public PercentTickLabelFormatter getPercentFormatter() {
    return new PercentTickLabelFormatter();
  }

  @Provides
  @Singleton
  @Named("zoom")
  public Context getZoomContext() {
    return new Context();
  }

}
