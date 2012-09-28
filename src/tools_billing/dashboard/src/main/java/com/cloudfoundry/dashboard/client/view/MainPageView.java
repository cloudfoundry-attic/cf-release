/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.presenter.MainPagePresenter;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Main Page View
 *
 * @author Vadim Spivak
 */
public class MainPageView extends ViewImpl implements MainPagePresenter.MyView {

  public interface Binder extends UiBinder<Widget, MainPageView> {

  }

  public final Widget widget;

  @UiField
  SimpleLayoutPanel contentPanel;

  @UiField
  SimpleLayoutPanel controlPanel;

  @Inject
  public MainPageView(Binder binder) {
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setInSlot(Object slot, Widget content) {
    if (slot == MainPagePresenter.MAIN_CONTENT_SLOT) {
      setMainContent(content);
    } else if (slot == MainPagePresenter.CONTROL_SLOT) {
      setControlContent(content);
    } else {
      super.setInSlot(slot, content);
    }
  }

  private void setControlContent(Widget content) {
    controlPanel.clear();
    if (content != null) {
      controlPanel.add(content);
    }
  }

  private void setMainContent(Widget content) {
    contentPanel.clear();
    if (content != null) {
      contentPanel.add(content);
    }
  }

}
