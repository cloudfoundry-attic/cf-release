/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.view;

import com.cloudfoundry.dashboard.client.util.PrioritizedLabel;

import com.gwtplatform.mvp.client.UiHandlers;

/**
 * Graph UI Handlers
 *
 * @author Vadim Spivak
 */
public interface GraphUiHandlers extends UiHandlers {

  void onLabelToggle(PrioritizedLabel label, boolean value);

}
