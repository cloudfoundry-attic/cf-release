/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CommonResources;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * Styled Tab
 *
 * @author Vadim Spivak
 */
public class StyledTab extends Composite implements Tab {

  public interface Binder extends UiBinder<Widget, StyledTab> {

  }

  public interface Factory {

    StyledTab create(TabData tabData);

  }

  @UiField
  HTML label;

  private final PlaceManager placeManager;

  private final TabData tabData;

  private String historyToken;

  @Inject
  public StyledTab(Binder binder, PlaceManager placeManager, @Assisted TabData tabData) {
    this.placeManager = placeManager;
    this.tabData = tabData;
    initWidget(binder.createAndBindUi(this));
    setText(tabData.getLabel());
    setStyleName("gwt-TabLayoutPanelTab");
    getElement().addClassName(CommonResources.getInlineBlockStyle());
  }

  @Override
  public void setText(String text) {
    label.setText(text);
  }

  @Override
  public void activate() {
    addStyleDependentName("selected");
  }

  @Override
  public Widget asWidget() {
    return label;
  }

  @Override
  public void deactivate() {
    removeStyleDependentName("selected");
  }

  @Override
  public float getPriority() {
    return tabData.getPriority();
  }

  @Override
  public String getText() {
    return label.getText();
  }

  @UiHandler("label")
  public void labelClicked(ClickEvent event) {
    placeManager.revealPlace(new PlaceRequest(historyToken));
  }

  @Override
  public void setTargetHistoryToken(String historyToken) {
    this.historyToken = historyToken;
  }

}
