/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph.label;

import java.util.Map;

/**
 * Label Extractor
 *
 * @author Vadim Spivak
 */
public interface LabelExtractor {

  String extract(Map<String, String> tags);

}
