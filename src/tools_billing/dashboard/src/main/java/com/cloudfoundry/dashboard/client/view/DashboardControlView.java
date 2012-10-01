/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.event.OptionChangeEvent;
import com.cloudfoundry.dashboard.client.presenter.DashboardControlPresenter;
import com.cloudfoundry.dashboard.client.ui.HorizontalOptionSelector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.NotificationMole;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * Dashboard Control View
 *
 * @author Vadim Spivak
 */
public class DashboardControlView extends ViewWithUiHandlers<DashboardControlUiHandlers>
    implements DashboardControlPresenter.MyView {

  public interface Binder extends UiBinder<Widget, DashboardControlView> {

  }

  public final Widget widget;

  @UiField
  HorizontalOptionSelector timezoneSelector;

  @UiField
  HorizontalOptionSelector updateSelector;

  @UiField
  HorizontalOptionSelector rangeSelector;

  @UiField
  NotificationMole notificationMole;

  @UiField
  Image spinner;

  @UiField
  InlineLabel lastUpdated;

  @UiField
  PushButton linkButton;

  @UiField
  Anchor logoAnchor;

  @Inject
  public DashboardControlView(Binder binder) {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @UiHandler("linkButton")
  public void onLinkHereClick(ClickEvent clickEvent) {
    getUiHandlers().onShowLinkPopup();
  }

  @UiHandler("logoAnchor")
  public void onLogoClick(ClickEvent event) {
    getUiHandlers().onShowDashboard();
  }

  @UiHandler("rangeSelector")
  public void rangeChange(OptionChangeEvent event) {
    getUiHandlers().onRangeChange(event.getOption());
  }

  public void setRangeValue(String value) {
    rangeSelector.setSelectedOption(value);
  }

  @Override
  public void setSpinnerVisible(boolean visible) {
    spinner.setVisible(visible);
  }

  public void setTimezoneValue(String value) {
    timezoneSelector.setSelectedOption(value);
  }

  public void setUpdateValue(String value) {
    updateSelector.setSelectedOption(value);
  }

  public void showNotification(String message) {
    notificationMole.show(message);
    new Timer() {

      @Override
      public void run() {
        notificationMole.hide();
      }
    }.schedule(5000);
  }

  @UiHandler("timezoneSelector")
  public void timeZoneChange(OptionChangeEvent event) {
    getUiHandlers().onTimeZoneChange(event.getOption());
  }

  @UiHandler("updateSelector")
  public void updateChanged(OptionChangeEvent event) {
    getUiHandlers().onUpdateChange(event.getOption());
  }

  public void updateLastUpdated(String date) {
    lastUpdated.setText(date);
  }

}
