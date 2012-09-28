/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.cloudfoundry.dashboard.client.NameTokens;
import com.cloudfoundry.dashboard.client.event.GraphContextSwitchEvent;
import com.cloudfoundry.dashboard.client.event.NavigationClickEvent;
import com.cloudfoundry.dashboard.client.event.NavigationEvent;
import com.cloudfoundry.dashboard.client.event.StartTimeChangeEvent;
import com.cloudfoundry.dashboard.client.graph.Context;
import com.cloudfoundry.dashboard.client.graph.Graphs;
import com.cloudfoundry.dashboard.client.ui.NavigationBar;
import com.cloudfoundry.dashboard.client.util.PrioritizedLabel;
import com.cloudfoundry.dashboard.client.view.GraphZoomUiHandlers;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * Graph Zoom Presenter
 *
 * @author Vadim Spivak
 */
public class GraphZoomPresenter extends Presenter<GraphZoomPresenter.MyView, GraphZoomPresenter.MyProxy>
    implements GraphZoomUiHandlers, NavigationClickEvent.NavigationClickHandler {

  @ProxyStandard
  @NameToken(NameTokens.ZOOM_PAGE)
  public interface MyProxy extends ProxyPlace<GraphZoomPresenter> {

  }

  public interface MyView extends View, HasUiHandlers<GraphZoomUiHandlers> {

  }

  @ContentSlot
  public static final GwtEvent.Type<RevealContentHandler<?>> GRAPH_SLOT =
      new GwtEvent.Type<RevealContentHandler<?>>();

  private final Provider<NavigationBar> navigationBarProvider;

  private final Context context;

  private final Graphs graphs;

  @Inject
  public GraphZoomPresenter(EventBus eventBus, MyView view, MyProxy proxy, Graphs graphs,
                            @Named("zoom") Context context, Provider<NavigationBar> navigationBarProvider) {
    super(eventBus, view, proxy);
    this.context = context;
    this.graphs = graphs;
    this.navigationBarProvider = navigationBarProvider;
    view.setUiHandlers(this);
  }

  @Override
  public void onNavigationClick(NavigationClickEvent event) {
    if (context != null) {
      context.navigateBy(event.getAmount());
      NavigationEvent.fire(getEventBus(), context, event.getAmount());
      StartTimeChangeEvent.fire(getEventBus(), context);
    }
  }

  @Override
  public void prepareFromRequest(PlaceRequest request) {
    super.prepareFromRequest(request);

    context.setFromPlaceRequest(request);

    GraphPresenter graph = graphs.getGraph(context.getGraph());
    graph.setContext(context);
    graph.setWidth(800);
    graph.setHeight(600);

    NavigationBar navigationBar = navigationBarProvider.get();
    navigationBar.addNavigationClickHandler(this);
    graph.setNavigationWidget(navigationBar);

    String[] hiddenLabels = context.getHiddenLabels();
    if (hiddenLabels != null) {
      for (String label : hiddenLabels) {
        graph.hideLabel(PrioritizedLabel.fromParamValue(label));
      }
    }

    setInSlot(GRAPH_SLOT, graph);

    GraphContextSwitchEvent.fire(getEventBus(), context);
  }

  @Override
  protected void revealInParent() {
    RevealContentEvent.fire(this, MainPagePresenter.MAIN_CONTENT_SLOT, this);
  }

}
