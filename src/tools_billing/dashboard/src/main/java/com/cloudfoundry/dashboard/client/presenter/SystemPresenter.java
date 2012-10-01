/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.cloudfoundry.dashboard.client.NameTokens;
import com.cloudfoundry.dashboard.client.gin.ClientGinjector;
import com.cloudfoundry.dashboard.client.graph.Context;
import com.cloudfoundry.dashboard.client.graph.Graphs;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.gwtplatform.mvp.client.TabData;
import com.gwtplatform.mvp.client.TabDataBasic;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.TabInfo;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TabContentProxyPlace;

/**
 * SystemPresenter
 *
 * @author Oleg Shaldybin
  */
public class SystemPresenter extends BaseDashboardGraphPresenter<SystemPresenter.MyProxy> {

  @ProxyStandard
  @NameToken(NameTokens.SYSTEM_PAGE)
  public interface MyProxy extends TabContentProxyPlace<SystemPresenter> {

  }

  @TabInfo(container = DashboardPresenter.class)
  static TabData getTagLabel(ClientGinjector ginjector) {
    return new TabDataBasic("System", 1);
  }

  @Inject
  public SystemPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
                         @Named("dashboard") Context graphContext, Graphs graphs) {
    super(eventBus, view, proxy, placeManager, graphContext);

    for (String role: new String[] {"core", "service", "unknown" }) {
      addGraph(graphs.getGraph("system.load.1m." + role));
      addGraph(graphs.getGraph("system.cpu.user." + role));
      addGraph(graphs.getGraph("system.mem.percent." + role));
      addGraph(graphs.getGraph("system.disk.system.percent." + role));
      addGraph(graphs.getGraph("system.disk.ephemeral.percent." + role));
      addGraph(graphs.getGraph("system.disk.persistent.percent." + role));
    }

  }
}
