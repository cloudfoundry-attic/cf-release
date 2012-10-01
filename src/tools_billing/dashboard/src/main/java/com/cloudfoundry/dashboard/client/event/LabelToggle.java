/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.event;

import com.gwtplatform.dispatch.annotation.GenEvent;

/**
 * Label Toggle
 *
 * @author Vadim Spivak
 */
@GenEvent
public class LabelToggle {

  String label;
  boolean value;

}
