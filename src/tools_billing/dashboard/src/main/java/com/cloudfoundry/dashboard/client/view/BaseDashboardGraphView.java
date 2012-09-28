/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.presenter.BaseDashboardGraphPresenter;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Base Dashboard Graph View -
 *
 * @author Vadim Spivak
 */
public class BaseDashboardGraphView extends ViewImpl implements BaseDashboardGraphPresenter.MyView {

  public interface Binder extends UiBinder<Widget, BaseDashboardGraphView> {

  }

  public final Widget widget;

  @UiField
  FlowPanel graphPanel;

  @Inject
  public BaseDashboardGraphView(Binder binder) {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public void addToSlot(Object slot, Widget content) {
    if (slot == BaseDashboardGraphPresenter.GRAPH_SLOT) {
      this.graphPanel.add(content);
    } else {
      super.addToSlot(slot, content);
    }
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void removeFromSlot(Object slot, Widget content) {
    if (slot == BaseDashboardGraphPresenter.GRAPH_SLOT) {
      this.graphPanel.remove(content);
    } else {
      super.removeFromSlot(slot, content);
    }
  }

}
