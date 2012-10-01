/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.server;

import com.cloudfoundry.dashboard.shared.FetchChartData;

import com.gwtplatform.dispatch.server.AbstractHttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.server.RequestProvider;
import com.gwtplatform.dispatch.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.server.spring.DispatchModule;
import com.gwtplatform.dispatch.server.spring.HandlerModule;
import com.gwtplatform.dispatch.server.spring.HttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.server.spring.actionvalidator.DefaultActionValidator;
import com.gwtplatform.dispatch.server.spring.request.DefaultRequestProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.URI;

/**
 * ServerModule
 *
 * @author Vadim Spivak
 */

@Configuration
@Import({DispatchModule.class})
public class ServerModule extends HandlerModule {

  @Value("${tsdb.uri}")
  private String tsdbUri;

  @Value("${securityCookieName}")
  private String securityCookieName;

  @Value("${logback.xml}")
  private String logbackCongiguration;

  @Value("${timeout.connection}")
  private Integer connectionTimeout;

  @Value("${timeout.socket}")
  private Integer socketTimeout;

  @Bean
  String getSecurityCookieName() {
    return securityCookieName;
  }

  protected void configureHandlers() {
    bindHandler(FetchChartData.class, FetchChartDataHandler.class);
  }

  @Bean(name = "cookieFilter")
  AbstractHttpSessionSecurityCookieFilter getCookieFilter() {
    return new HttpSessionSecurityCookieFilter(getSecurityCookieName());
  }

  @Bean
  public ActionValidator getDefaultActionValidator() {
    return new DefaultActionValidator();
  }

  @Bean
  HttpClient getHttpClient() {
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
    HttpConnectionParams.setSoTimeout(params, socketTimeout);
    return new DefaultHttpClient(new ThreadSafeClientConnManager(), params);
  }

  @Bean
  LoggingConfigurator getLoggingConfigurator() {
    return new LoggingConfigurator(logbackCongiguration);
  }

  @Bean
  RequestProvider getRequestProvider() {
    return new DefaultRequestProvider();
  }

  @Bean(name = "tsdb")
  URI getTsdbUri() {
    return URI.create(tsdbUri);
  }

}
