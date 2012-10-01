/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

/**
 * LoggingConfigurator
 *
 * @author Vadim Spivak
 */
public class LoggingConfigurator {

  public LoggingConfigurator(String location) {
    if (location != null && !location.isEmpty()) {
      LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

      try {
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        context.reset();
        configurator.doConfigure(location);
      } catch (JoranException e) {
        throw new RuntimeException(e);
      }

      StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
  }

}
