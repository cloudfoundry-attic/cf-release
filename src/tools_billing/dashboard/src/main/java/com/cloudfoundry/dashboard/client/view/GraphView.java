/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.presenter.GraphPresenter;
import com.cloudfoundry.dashboard.client.ui.StyledCheckbox;
import com.cloudfoundry.dashboard.client.util.PrioritizedLabel;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlecode.gchart.client.GChart;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import java.util.List;

/**
 * Graph View
 *
 * @author Vadim Spivak
 */
public class GraphView extends ViewWithUiHandlers<GraphUiHandlers>
    implements GraphPresenter.MyView, ValueChangeHandler<Boolean> {

  public interface Binder extends UiBinder<Widget, GraphView> {

  }

  public final Widget widget;

  @UiField
  SimplePanel chartContainer;

  @UiField
  VerticalPanel labelContainer;

  @UiField
  SimplePanel navigationContainer;

  @UiField
  Grid container;

  @UiField
  SimplePanel titleContainer;

  private final GChart chart;

  private final List<PrioritizedLabel> sortedLabels;

  private final BiMap<PrioritizedLabel, StyledCheckbox> labels;

  private final Provider<StyledCheckbox> styledCheckboxProvider;

  @Inject
  public GraphView(Binder binder, Provider<StyledCheckbox> styledCheckboxProvider) {
    this.styledCheckboxProvider = styledCheckboxProvider;
    this.widget = binder.createAndBindUi(this);

    chart = new GChart();
    chart.setClipToPlotArea(true);
    chartContainer.setWidget(chart);

    labels = HashBiMap.create();
    sortedLabels = Lists.newArrayList();
    container.getRowFormatter().setVisible(2, false);
    container.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
    container.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
  }

  @Override
  public GChart getChart() {
    return chart;
  }

  @Override
  public void addLabel(PrioritizedLabel label, String color, boolean visible) {
    StyledCheckbox checkbox = styledCheckboxProvider.get();

    checkbox.getLabel().setText(label.getLabel());
    checkbox.setSymbolColor(color);
    checkbox.setValue(visible);
    checkbox.addValueChangeHandler(this);

    labels.put(label, checkbox);

    int index;
    for (index = 0; index < sortedLabels.size(); index++) {
      if (label.compareTo(sortedLabels.get(index)) < 0) {
        break;
      }
    }

    sortedLabels.add(index, label);
    labelContainer.insert(checkbox, index);
  }

  @Override
  public Widget asWidget() {
    return widget;
  }

  @Override
  public void onValueChange(ValueChangeEvent<Boolean> event) {
    if (event.getSource() instanceof StyledCheckbox) {
      StyledCheckbox checkbox = (StyledCheckbox) event.getSource();
      PrioritizedLabel prioritizedLabel = labels.inverse().get(checkbox);
      getUiHandlers().onLabelToggle(prioritizedLabel, checkbox.getValue());
    }
  }

  @Override
  public void removeLabel(PrioritizedLabel label) {
    StyledCheckbox checkBox = labels.remove(label);
    sortedLabels.remove(label);
    labelContainer.remove(checkBox);
  }

  @Override
  public void setHeight(int height) {
    chartContainer.getElement().getStyle().setHeight(height, Style.Unit.PX);
  }

  public void setNavigationWidget(Widget widget) {
    navigationContainer.setWidget(widget);
    container.getRowFormatter().setVisible(2, widget != null);
  }

  @Override
  public void setTitleWidget(Widget widget) {
    titleContainer.setWidget(widget);
  }

  @Override
  public void setWidth(int width) {
    chartContainer.getElement().getStyle().setWidth(width, Style.Unit.PX);
  }

}
