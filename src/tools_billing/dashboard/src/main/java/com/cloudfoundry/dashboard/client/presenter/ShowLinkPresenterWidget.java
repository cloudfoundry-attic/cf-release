/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.cloudfoundry.dashboard.client.graph.Context;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * ShowLinkPresenterWidget
 *
 * @author Vadim Spivak
 */
public class ShowLinkPresenterWidget extends PresenterWidget<ShowLinkPresenterWidget.MyView> {

  public interface MyView extends PopupView {

    void setUrl(String url);

  }

  private final PlaceManager placeManager;

  private Context context;

  @Inject
  public ShowLinkPresenterWidget(final EventBus eventBus, final MyView view, PlaceManager placeManager) {
    super(eventBus, view);
    this.placeManager = placeManager;
  }

  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  @Override
  protected void onReveal() {
    super.onReveal();

    PlaceRequest request = new PlaceRequest(placeManager.getCurrentPlaceRequest().getNameToken());
    request = context.updatePlaceRequest(request);
    String historyToken = placeManager.buildRelativeHistoryToken(request, -1);
    String url = Window.Location.createUrlBuilder().setHash(historyToken).buildString();
    getView().setUrl(url);
  }

}
