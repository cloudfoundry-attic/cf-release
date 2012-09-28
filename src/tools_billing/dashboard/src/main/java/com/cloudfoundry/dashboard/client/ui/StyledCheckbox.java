/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.ui;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Styled Checkbox
 *
 * @author Vadim Spivak
 */
public class StyledCheckbox extends Composite implements HasValue<Boolean> {

  public interface Binder extends UiBinder<Widget, StyledCheckbox> {

  }

  @UiField
  DivElement symbol;

  @UiField
  Label label;

  @UiField
  CheckBox checkbox;

  @Inject
  public StyledCheckbox(Binder binder) {
    initWidget(binder.createAndBindUi(this));
  }

  public Label getLabel() {
    return label;
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  @UiHandler("checkbox")
  void checkboxChanged(ValueChangeEvent<Boolean> event) {
    ValueChangeEvent.fire(this, event.getValue());
  }

  @Override
  public Boolean getValue() {
    return checkbox.getValue();
  }

  public void setSymbolColor(String color) {
    symbol.getStyle().setBackgroundColor(color);
  }

  @Override
  public void setValue(Boolean value) {
    checkbox.setValue(value);
  }

  @Override
  public void setValue(Boolean value, boolean fireEvents) {
    checkbox.setValue(value, fireEvents);
  }

}
