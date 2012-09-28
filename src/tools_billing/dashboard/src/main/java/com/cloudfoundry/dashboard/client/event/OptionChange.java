/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.event;

import com.gwtplatform.dispatch.annotation.GenEvent;

/**
 * Option Change
 *
 * @author Vadim Spivak
 */
@GenEvent
public class OptionChange {

  String option;

}
