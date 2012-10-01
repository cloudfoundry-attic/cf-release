/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.server;

import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Caching Filter
 *
 * @author Vadim Spivak
 */
public class CachingFilter implements Filter {

  private static final Set<String> EXTENSIONS = ImmutableSet.of("html", "css", "js", "gif", "jpg", "jpeg", "png");

  private static final long SECONDS_IN_YEAR = 365L * 24 * 60 * 60;

  private static final long MILLISECONDS_IN_YEAR = SECONDS_IN_YEAR * 1000;

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {
    if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      HttpServletResponse response = (HttpServletResponse) servletResponse;

      String servletPath = request.getServletPath();
      String name = servletPath.substring(servletPath.lastIndexOf('/') + 1);
      String[] parts = name.split("\\.");
      String extension = parts[parts.length - 1];
      if (EXTENSIONS.contains(extension)) {
        boolean preventCaching = true;
        if (parts.length > 1) {
          if (parts[parts.length - 2].toLowerCase().equals("cache")) {
            preventCaching = false;
          }
        }

        if (preventCaching) {
          preventCaching(response);
        } else {
          enableCaching(response);
        }
      }

      chain.doFilter(servletRequest, servletResponse);
    }
  }

  private void enableCaching(HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, must-revalidate, max-age=" + SECONDS_IN_YEAR);
    response.setDateHeader("Expires", new Date().getTime() + MILLISECONDS_IN_YEAR);
  }

  private void preventCaching(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

}
