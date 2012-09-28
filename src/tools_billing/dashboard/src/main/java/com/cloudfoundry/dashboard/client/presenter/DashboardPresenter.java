/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.RequestTabsHandler;
import com.gwtplatform.mvp.client.TabContainerPresenter;
import com.gwtplatform.mvp.client.TabView;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.RequestTabs;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * Dashboard Presenter
 *
 * @author Vadim Spivak
 */
public class DashboardPresenter
    extends TabContainerPresenter<DashboardPresenter.MyView, DashboardPresenter.MyProxy> {

  @ProxyStandard
  public interface MyProxy extends Proxy<DashboardPresenter> {

  }

  public interface MyView extends TabView {

  }

  @RequestTabs
  public static final GwtEvent.Type<RequestTabsHandler> REQUEST_TABS_EVENT =
      new GwtEvent.Type<RequestTabsHandler>();

  @ContentSlot
  public static final GwtEvent.Type<RevealContentHandler<?>> TAB_CONTENT_SLOT =
      new GwtEvent.Type<RevealContentHandler<?>>() {

      };

  @Inject
  public DashboardPresenter(final EventBus eventBus, final MyView view,
                            final MyProxy proxy) {
    super(eventBus, view, proxy, TAB_CONTENT_SLOT, REQUEST_TABS_EVENT);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.MAIN_CONTENT_SLOT, this);
  }

}
