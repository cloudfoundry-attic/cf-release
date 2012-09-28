/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.ui;

import com.cloudfoundry.dashboard.client.event.OptionChangeEvent;

import com.google.common.collect.Maps;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.Map;

/**
 * Horizontal Option Selector
 *
 * @author Vadim Spivak
 */
public class HorizontalOptionSelector extends Composite
    implements OptionChangeEvent.HasOptionChangeHandlers, ClickHandler {

  interface MyStyle extends CssResource {

    String selected();

  }

  public interface Binder extends UiBinder<Widget, HorizontalOptionSelector> {

  }

  @UiField
  MyStyle style;

  @UiField
  HorizontalPanel panel;

  private final Map<String, Button> buttons;

  private String selectedOption;

  @Inject
  public HorizontalOptionSelector(Binder binder) {
    initWidget(binder.createAndBindUi(this));

    buttons = Maps.newHashMap();
    selectedOption = null;
  }

  public String getSelectedOption() {
    return selectedOption;
  }

  @Override
  public HandlerRegistration addOptionChangeHandler(OptionChangeEvent.OptionChangeHandler handler) {
    return addHandler(handler, OptionChangeEvent.getType());
  }

  @Override
  public void onClick(ClickEvent event) {
    if (event.getSource() instanceof Button) {
      Button button = (Button) event.getSource();
      setSelectedOption(button.getText());
      OptionChangeEvent.fire(this, getSelectedOption());
    }
  }

  public void setSelectedOption(String selectedOption) {
    if (this.selectedOption != null) {
      buttons.get(this.selectedOption).removeStyleName(style.selected());
    }
    this.selectedOption = selectedOption;
    buttons.get(this.selectedOption).addStyleName(style.selected());
  }

  public void setOptions(String options) {
    panel.clear();
    buttons.clear();

    String[] parsedOptions = options.split(",");
    if (parsedOptions.length == 0) {
      throw new IllegalArgumentException("Options can't be empty.");
    }

    for (int i = 0; i < parsedOptions.length; i++) {
      parsedOptions[i] = parsedOptions[i].trim();
    }

    for (String option : parsedOptions) {
      Button button = new Button(option);
      button.setWidth("50px");
      button.setHeight("24px");
      button.addClickHandler(this);
      panel.add(button);
      buttons.put(option, button);
    }

    setSelectedOption(parsedOptions[0]);
  }

}
