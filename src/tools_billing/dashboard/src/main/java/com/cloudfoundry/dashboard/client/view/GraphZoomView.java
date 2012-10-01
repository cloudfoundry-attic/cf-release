/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.presenter.GraphZoomPresenter;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * Graph Zoom View
 *
 * @author Vadim Spivak
 */
public class GraphZoomView extends ViewWithUiHandlers<GraphZoomUiHandlers> implements GraphZoomPresenter.MyView {

  public interface Binder extends UiBinder<Widget, GraphZoomView> {

  }

  public final Widget widget;

  @UiField
  SimplePanel graphContainer;

  @Inject
  public GraphZoomView(Binder binder) {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setInSlot(Object slot, Widget content) {
    if (slot == GraphZoomPresenter.GRAPH_SLOT) {
      graphContainer.setWidget(content);
    } else {
      super.setInSlot(slot, content);
    }
  }

}
