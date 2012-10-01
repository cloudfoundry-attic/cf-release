/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.presenter.ShowLinkPresenterWidget;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;

/**
 * Show Link View
 *
 * @author Vadim Spivak
 */
public class ShowLinkView extends PopupViewImpl implements ShowLinkPresenterWidget.MyView {

  public interface Binder extends UiBinder<PopupPanel, ShowLinkView> {

  }

  @UiField
  TextBox urlTextBox;

  private Widget widget;

  @Inject
  public ShowLinkView(EventBus eventBus, Binder binder) {
    super(eventBus);
    widget = binder.createAndBindUi(this);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void setUrl(String url) {
    urlTextBox.setText(url);
    urlTextBox.selectAll();
    urlTextBox.setFocus(true);
  }

}
