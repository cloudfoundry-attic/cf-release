/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph.label;

import java.util.Map;

/**
 * Constant Label Extractor - returns a static constant for the label.
 *
 * @author Vadim Spivak
 */
public class ConstantLabelExtractor implements LabelExtractor {

  private final String label;

  public ConstantLabelExtractor(String label) {
    this.label = label;
  }

  @Override
  public String extract(Map<String, String> tags) {
    return label;
  }

}
