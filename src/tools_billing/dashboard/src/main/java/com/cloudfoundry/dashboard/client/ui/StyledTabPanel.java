/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.ui;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Tab;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.TabPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Styled Tab Panel
 *
 * @author Vadim Spivak
 */
public class StyledTabPanel extends ResizeComposite implements TabPanel {

  public interface Binder extends UiBinder<Widget, StyledTabPanel> {

  }

  @UiField
  FlowPanel tabPanel;

  @UiField
  ScrollPanel contentPanel;

  Tab activeTab;

  private final List<Tab> tabs;

  private final StyledTab.Factory styledTabFactory;

  @Inject
  public StyledTabPanel(Binder binder, StyledTab.Factory styledTabFactory) {
    this.styledTabFactory = styledTabFactory;
    this.tabs = new ArrayList<Tab>();
    initWidget(binder.createAndBindUi(this));
  }

  @Override
  public Tab addTab(TabData tabData, String historyToken) {
    int index;
    for (index = 0; index < tabs.size(); index++) {
      Tab existingTab = tabs.get(index);
      if (existingTab.getPriority() > tabData.getPriority()) {
        break;
      }
    }

    StyledTab tab = styledTabFactory.create(tabData);
    tab.setVisible(true);
    tab.setTargetHistoryToken(historyToken);

    tabs.add(index, tab);
    tabPanel.insert(tab, index);

    return tab;
  }

  @Override
  public void removeTab(Tab tab) {
    tabPanel.remove(tab.asWidget());
    tabs.remove(tab);
  }

  @Override
  public void removeTabs() {
    for (Tab tab : tabs) {
      tabPanel.remove(tab.asWidget());
    }
    tabs.clear();
  }

  @Override
  public void setActiveTab(Tab tab) {
    if (activeTab != null) {
      activeTab.deactivate();
    }

    activeTab = tab;

    if (activeTab != null) {
      activeTab.activate();
    }
  }

  public void setContent(Widget content) {
    contentPanel.clear();
    if (content != null) {
      contentPanel.setWidget(content);
    }
  }

}
