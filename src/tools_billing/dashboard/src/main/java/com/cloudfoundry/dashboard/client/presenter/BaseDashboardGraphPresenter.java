/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.cloudfoundry.dashboard.client.event.GraphContextSwitchEvent;
import com.cloudfoundry.dashboard.client.graph.Context;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

import java.util.List;

/**
 * Base Dashboard Graph Presenter - used for all dashboard tab presenters,
 * extended via the {@link #addGraph(GraphPresenter)}.
 *
 * @author Vadim Spivak
 */
public abstract class BaseDashboardGraphPresenter<Proxy_ extends Proxy<?>> extends Presenter<BaseDashboardGraphPresenter.MyView, Proxy_> {

  public interface MyView extends View {

  }

  @ContentSlot
  public static final GwtEvent.Type<RevealContentHandler<?>> GRAPH_SLOT =
      new GwtEvent.Type<RevealContentHandler<?>>();

  private final Context context;

  private List<GraphPresenter> graphs;

  public BaseDashboardGraphPresenter(EventBus eventBus, MyView view, Proxy_ proxy,
                                     PlaceManager placeManager, Context context) {
    super(eventBus, view, proxy);
    this.context = context;
    this.graphs = Lists.newArrayList();
  }

  protected void addGraph(final GraphPresenter graph) {
    graphs.add(graph);
    graph.enableZoom();
    graph.setContext(context);
    graph.setWidth(500);
    graph.setHeight(400);
    addToSlot(GRAPH_SLOT, graph);
  }

  @Override
  protected void onHide() {
    super.onHide();
    for (GraphPresenter graph : graphs) {
      graph.unbind();
    }
  }

  @Override
  protected void onReveal() {
    super.onReveal();

    for (GraphPresenter graph : graphs) {
      graph.bind();
    }
  }

  @Override
  public void prepareFromRequest(PlaceRequest request) {
    super.prepareFromRequest(request);

    if (!context.isInitialized()) {
      context.setFromPlaceRequest(request);
    }

    GraphContextSwitchEvent.fire(getEventBus(), context);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, DashboardPresenter.TAB_CONTENT_SLOT, this);
  }

}
