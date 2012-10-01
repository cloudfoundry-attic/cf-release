/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph.label;

import java.util.Map;

/**
 * Tag Label Extractor
 *
 * @author Vadim Spivak
 */
public class TagLabelExtractor implements LabelExtractor {

  private final String tagName;

  public TagLabelExtractor(String tagName) {
    this.tagName = tagName;
  }

  @Override
  public String extract(Map<String, String> tags) {
    return tags.get(tagName);
  }

}
