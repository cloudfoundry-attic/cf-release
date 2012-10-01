/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;


/**
 * Main Page Presenter
 *
 * @author Vadim Spivak
 */
public class MainPagePresenter extends Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> {

  @ProxyStandard
  public interface MyProxy extends Proxy<MainPagePresenter> {

  }

  public interface MyView extends View {

  }

  @ContentSlot
  public static final GwtEvent.Type<RevealContentHandler<?>> MAIN_CONTENT_SLOT =
      new GwtEvent.Type<RevealContentHandler<?>>();

  @ContentSlot
  public static final GwtEvent.Type<RevealContentHandler<?>> CONTROL_SLOT =
      new GwtEvent.Type<RevealContentHandler<?>>();

  private final DashboardControlPresenter dashboardControlPresenter;

  @Inject
  public MainPagePresenter(
      final EventBus eventBus,
      final MyView view,
      final MyProxy proxy,
      final DashboardControlPresenter dashboardControlPresenter) {
    super(eventBus, view, proxy);
    this.dashboardControlPresenter = dashboardControlPresenter;
  }

  @Override
  protected void onReveal() {
    super.onReveal();
    setInSlot(CONTROL_SLOT, dashboardControlPresenter);
  }

  @Override
  protected void revealInParent() {
    RevealRootLayoutContentEvent.fire(this, this);
  }

}
