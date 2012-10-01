/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.ui;

import com.cloudfoundry.dashboard.client.event.NavigationClickEvent;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

/**
 * NavigationButton
 *
 * @author Vadim Spivak
 */
public class NavigationButton extends Composite
    implements HasText, NavigationClickEvent.HasNavigationClickHandlers, ClickHandler {

  public interface Binder extends UiBinder<Button, NavigationButton> {

  }

  interface MyStyle extends CssResource {

    @ClassName("seek-backward")
    String seekBackward();

    @ClassName("seek-forward")
    String seekForward();

    @ClassName("skip-forward")
    String skipForward();

  }

  @UiField
  DivElement label;

  @UiField
  DivElement image;

  @UiField
  MyStyle style;
  @UiField
  Button button;

  private long value;

  @Inject
  public NavigationButton(Binder binder) {
    initWidget(binder.createAndBindUi(this));
    button.addClickHandler(this);
  }

  public long getValue() {
    return value;
  }

  public void setValue(long value) {
    this.value = value;
  }

  @Override
  public HandlerRegistration addNavigationClickHandler(NavigationClickEvent.NavigationClickHandler handler) {
    return addHandler(handler, NavigationClickEvent.getType());
  }

  @Override
  public String getText() {
    return label.getInnerText();
  }

  @Override
  public void onClick(ClickEvent event) {
    NavigationClickEvent.fire(this, value);
  }

  public void setImageType(String type) {
    type = type.replaceAll("[^A-Za-z]", "").toLowerCase();
    if (type.equals("seekbackward")) {
      image.setClassName(style.seekBackward());
      image.removeFromParent();
      label.getParentElement().insertFirst(image);
    } else if (type.equals("seekforward")) {
      image.setClassName(style.seekForward());
    } else if (type.equals("skipforward")) {
      image.setClassName(style.skipForward());
    } else {
      throw new IllegalArgumentException("type");
    }
  }

  @Override
  public void setText(String text) {
    label.setInnerText(text);
  }

}
