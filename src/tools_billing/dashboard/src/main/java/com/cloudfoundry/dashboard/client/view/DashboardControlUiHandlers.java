/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.gwtplatform.mvp.client.UiHandlers;

/**
 * Dashboard Control UI Handlers
 *
 * @author Vadim Spivak
 */
public interface DashboardControlUiHandlers extends UiHandlers {

  void onRangeChange(String range);

  void onShowDashboard();

  void onShowLinkPopup();

  void onTimeZoneChange(String timeZone);

  void onUpdateChange(String option);

}
