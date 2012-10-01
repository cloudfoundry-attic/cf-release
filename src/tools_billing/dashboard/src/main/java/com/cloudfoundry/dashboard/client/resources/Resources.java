/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources
 *
 * @author Vadim Spivak
 */
public interface Resources extends ClientBundle {

  @Source("link.png")
  ImageResource link();

  @Source("logo.png")
  ImageResource logo();

  @Source("spinner.gif")
  ImageResource spinner();

}
