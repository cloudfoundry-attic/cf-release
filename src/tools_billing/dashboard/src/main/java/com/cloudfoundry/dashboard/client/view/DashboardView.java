/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.presenter.DashboardPresenter;
import com.cloudfoundry.dashboard.client.ui.StyledTabPanel;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Dashboard Control View
 *
 * @author Vadim Spivak
 */
public class DashboardView extends ViewImpl implements DashboardPresenter.MyView {

  public interface Binder extends UiBinder<Widget, DashboardView> {

  }

  public final Widget widget;

  @UiField
  StyledTabPanel tabPanel;

  @Inject
  public DashboardView(Binder binder) {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Tab addTab(TabData tabData, String historyToken) {
    return tabPanel.addTab(tabData, historyToken);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void removeTab(Tab tab) {
    tabPanel.removeTab(tab);
  }

  @Override
  public void removeTabs() {
    tabPanel.removeTabs();
  }

  @Override
  public void setActiveTab(Tab tab) {
    tabPanel.setActiveTab(tab);
  }

  @Override
  public void setInSlot(Object slot, Widget content) {
    if (slot == DashboardPresenter.TAB_CONTENT_SLOT) {
      tabPanel.setContent(content);
    } else {
      super.setInSlot(slot, content);
    }
  }

}
