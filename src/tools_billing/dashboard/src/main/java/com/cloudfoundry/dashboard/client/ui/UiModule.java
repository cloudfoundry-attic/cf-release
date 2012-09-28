/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.ui;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * UI Module
 *
 * @author Vadim Spivak
 */
public class UiModule extends AbstractGinModule {

  @Override
  protected void configure() {
    bind(HorizontalOptionSelector.Binder.class).in(Singleton.class);
    bind(NavigationBar.Binder.class).in(Singleton.class);
    bind(NavigationButton.Binder.class).in(Singleton.class);
    bind(StyledTab.Binder.class).in(Singleton.class);
    bind(StyledTabPanel.Binder.class).in(Singleton.class);
    bind(StyledCheckbox.Binder.class).in(Singleton.class);
  }

}
