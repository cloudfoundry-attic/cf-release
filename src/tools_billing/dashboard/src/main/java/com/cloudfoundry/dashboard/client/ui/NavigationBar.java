/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.ui;

import com.cloudfoundry.dashboard.client.event.NavigationClickEvent;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * Navigation Bar
 *
 * @author Vadim Spivak
 */
public class NavigationBar extends Composite
    implements NavigationClickEvent.NavigationClickHandler, NavigationClickEvent.HasNavigationClickHandlers {

  public interface Binder extends UiBinder<Widget, NavigationBar> {

  }

  interface MyStyle extends CssResource {

    String now();

  }

  @UiField
  MyStyle style;

  @UiField
  HorizontalPanel container;

  private Provider<NavigationButton> navigationButtonProvider;

  @Inject
  public NavigationBar(Binder binder, Provider<NavigationButton> navigationButtonProvider) {
    this.navigationButtonProvider = navigationButtonProvider;
    initWidget(binder.createAndBindUi(this));

    String[] labels = {"1w", "1d", "4h", "30m"};
    long[] values = {
        7L * 24 * 60 * 60 * 1000,
        24L * 60 * 60 * 1000,
        4L * 60 * 60 * 1000,
        30L * 60 * 1000
    };

    for (int i = 0; i < labels.length; i++) {
      String label = labels[i];
      addButton("seek-backward", label, -values[i]);
    }

    for (int i = labels.length - 1; i >= 0; i--) {
      String label = labels[i];
      addButton("seek-forward", label, values[i]);
    }

    NavigationButton nowButton = addButton("skip-forward", "now", 0);
    nowButton.addStyleName(style.now());
  }

  private NavigationButton addButton(String type, String label, long value) {
    NavigationButton button = navigationButtonProvider.get();
    button.setImageType(type);
    button.setText(label);
    button.setValue(value);
    button.addNavigationClickHandler(this);
    container.add(button);
    return button;
  }

  @Override
  public HandlerRegistration addNavigationClickHandler(NavigationClickEvent.NavigationClickHandler handler) {
    return addHandler(handler, NavigationClickEvent.getType());
  }

  @Override
  public void onNavigationClick(NavigationClickEvent event) {
    NavigationClickEvent.fire(this, event.getAmount());
  }

}
