/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.cloudfoundry.dashboard.client.event.BackendErrorEvent;
import com.cloudfoundry.dashboard.client.event.DataUpdateEvent;
import com.cloudfoundry.dashboard.client.event.GraphContextSwitchEvent;
import com.cloudfoundry.dashboard.client.event.NavigationEvent;
import com.cloudfoundry.dashboard.client.event.RangeChangeEvent;
import com.cloudfoundry.dashboard.client.event.StartTimeChangeEvent;
import com.cloudfoundry.dashboard.client.event.TimeZoneChangeEvent;
import com.cloudfoundry.dashboard.client.graph.Context;
import com.cloudfoundry.dashboard.client.graph.Range;
import com.cloudfoundry.dashboard.client.view.DashboardControlUiHandlers;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;

import java.util.Date;

/**
 * Dashboard Control Presenter - top presenter that contains the dashboard controls and logo.
 *
 * @author Vadim Spivak
 */
public class DashboardControlPresenter
    extends Presenter<DashboardControlPresenter.MyView, DashboardControlPresenter.MyProxy>
    implements DashboardControlUiHandlers, GraphContextSwitchEvent.GraphContextSwitchHandler,
    DataUpdateEvent.DataUpdateHandler, BackendErrorEvent.BackendErrorHandler,
    NavigationEvent.NavigationHandler {

  @ProxyStandard
  public interface MyProxy extends Proxy<DashboardControlPresenter> {

  }

  public interface MyView extends View, HasUiHandlers<DashboardControlUiHandlers> {

    void setTimezoneValue(String value);

    void setUpdateValue(String value);

    void setRangeValue(String value);

    void setSpinnerVisible(boolean visible);

    void updateLastUpdated(String date);

    void showNotification(String message);

  }

  private final static DateTimeFormat LAST_UPDATED_FORMAT = DateTimeFormat.getFormat("MM/dd HH:mm:ss");

  private final PlaceManager placeManager;

  private final ShowLinkPresenterWidget showLinkPresenterWidget;

  private Context context;

  private Timer timer;

  @Inject
  public DashboardControlPresenter(final EventBus eventBus, final MyView view,
                                   final MyProxy proxy, PlaceManager placeManager,
                                   ShowLinkPresenterWidget showLinkPresenterWidget) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
    this.showLinkPresenterWidget = showLinkPresenterWidget;
    view.setUiHandlers(this);
  }

  @Override
  public void onBackendError(BackendErrorEvent event) {
    getView().showNotification(event.getError().getMessage());
  }

  @Override
  protected void onBind() {
    super.onBind();
    addRegisteredHandler(GraphContextSwitchEvent.getType(), this);
    addRegisteredHandler(DataUpdateEvent.getType(), this);
    addRegisteredHandler(BackendErrorEvent.getType(), this);
    addRegisteredHandler(NavigationEvent.getType(), this);
  }

  @Override
  public void onDataUpdate(DataUpdateEvent event) {
    getView().setSpinnerVisible(!event.isFinished());
    if (event.isFinished() && event.isSuccessful()) {
      updateLastUpdated(true);
    }
  }

  private void updateLastUpdated(boolean updateTime) {
    if (context != null) {
      if (updateTime) {
        context.setLastUpdated(new Date().getTime());
      }
      if (context.getLastUpdated() != -1) {
        getView().updateLastUpdated(LAST_UPDATED_FORMAT.format(new Date(context.getLastUpdated()),
            context.getTimeZone()));
      }
    }
  }

  @Override
  @ProxyEvent
  public void onGraphContextSwitch(GraphContextSwitchEvent event) {
    context = event.getContext();
    getView().setRangeValue(context.getRange().getParamValue());
    getView().setTimezoneValue(context.getTimeZoneParamValue());
    getView().setUpdateValue(context.getUpdatePeriodParamValue());
    updateTimer();
    updateLastUpdated(false);
  }

  private void updateTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }

    int interval = context.getUpdatePeriod();
    if (interval > 0) {
      timer = new Timer() {

        @Override
        public void run() {
          if (context != null) {
            context.navigateBy(0);
            StartTimeChangeEvent.fire(getEventBus(), context);
          } else {
            timer.cancel();
            timer = null;
          }
        }

      };
      timer.scheduleRepeating(interval);
    }
  }

  @Override
  public void onNavigation(NavigationEvent event) {
    if (context == event.getContext()) {
      if (event.getAmount() != 0) {
        context.setUpdatePeriod(-1);
        getView().setUpdateValue(context.getUpdatePeriodParamValue());
        updateTimer();
      }
    }
  }

  @Override
  public void onRangeChange(String range) {
    if (context != null) {
      context.setRange(Range.fromParamValue(range), true);
      RangeChangeEvent.fire(getEventBus(), context);
    }
  }

  @Override
  public void onShowDashboard() {
    if (placeManager.getHierarchyDepth() > 1) {
      placeManager.revealRelativePlace(-1);
    }
  }

  @Override
  public void onShowLinkPopup() {
    showLinkPresenterWidget.setContext(context);
    RevealRootPopupContentEvent.fire(getEventBus(), showLinkPresenterWidget);
  }

  @Override
  public void onTimeZoneChange(String timeZone) {
    if (context != null) {
      context.setTimeZoneFromParamValue(timeZone);
      TimeZoneChangeEvent.fire(getEventBus(), context);
      updateLastUpdated(false);
    }
  }

  @Override
  public void onUpdateChange(String option) {
    if (context != null) {
      context.setUpdatePeriodFromParamValue(option);
      context.navigateBy(0);
      StartTimeChangeEvent.fire(getEventBus(), context);
      updateTimer();
    }
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.CONTROL_SLOT, this);
  }

}
