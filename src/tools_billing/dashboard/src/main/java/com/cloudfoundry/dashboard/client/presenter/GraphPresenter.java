/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.presenter;

import com.cloudfoundry.dashboard.client.NameTokens;
import com.cloudfoundry.dashboard.client.event.BackendErrorEvent;
import com.cloudfoundry.dashboard.client.event.DataUpdateEvent;
import com.cloudfoundry.dashboard.client.event.RangeChangeEvent;
import com.cloudfoundry.dashboard.client.event.StartTimeChangeEvent;
import com.cloudfoundry.dashboard.client.event.TimeZoneChangeEvent;
import com.cloudfoundry.dashboard.client.graph.Context;
import com.cloudfoundry.dashboard.client.graph.Query;
import com.cloudfoundry.dashboard.client.graph.axis.SimpleTickLabelFormatter;
import com.cloudfoundry.dashboard.client.graph.axis.TickLabelFormatter;
import com.cloudfoundry.dashboard.client.util.GlobalPendingRequests;
import com.cloudfoundry.dashboard.client.util.PrioritizedLabel;
import com.cloudfoundry.dashboard.client.view.GraphUiHandlers;
import com.cloudfoundry.dashboard.shared.DataPoint;
import com.cloudfoundry.dashboard.shared.FetchChartData;
import com.cloudfoundry.dashboard.shared.Metric;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.googlecode.gchart.client.GChart;
import com.googlecode.gchart.client.HoverParameterInterpreter;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph Presenter
 *
 * @author Vadim Spivak
 */
public class GraphPresenter extends PresenterWidget<GraphPresenter.MyView>
    implements GraphUiHandlers, RangeChangeEvent.RangeChangeHandler, TimeZoneChangeEvent.TimeZoneChangeHandler,
    HoverParameterInterpreter, StartTimeChangeEvent.StartTimeChangeHandler, ClickHandler {

  public interface MyView extends View, HasUiHandlers<GraphUiHandlers> {

    GChart getChart();

    void setTitleWidget(Widget widget);

    void setNavigationWidget(Widget widget);

    void addLabel(PrioritizedLabel label, String color, boolean visible);

    void removeLabel(PrioritizedLabel label);

    void setWidth(int width);

    void setHeight(int height);

  }

  private static DateTimeFormat SHORT_DATE_TIME_FORMAT = DateTimeFormat.getFormat("HH:mm");

  private static DateTimeFormat LONG_DATE_TIME_FORMAT = DateTimeFormat.getFormat("MM/dd HH:mm");

  // TODO: move
  private static String[] COLORS = new String[]{
      "#FFB300",
      "#803E75",
      "#FF6800",
      "#A6BDD7",
      "#C10020",
      "#CEA262",
      "#817066",
      "#007D34",
      "#F6768E",
      "#00538A",
      "#FF7A5C",
      "#53377A",
      "#FF8E00",
      "#B32851",
      "#F4C800",
      "#7F180D",
      "#93AA00",
      "#593315",
      "#F13A13",
      "#232C16"
  };

  private static final int DEFAULT_TICK_RESOLUTION = 50;

  private final PlaceManager placeManager;

  private final List<Query> queries;

  private Context context;

  private String title;

  private String graphId;

  private int xTickResolution = DEFAULT_TICK_RESOLUTION;

  private int yTickResolution = DEFAULT_TICK_RESOLUTION;

  private long start;

  private long end;

  private TickLabelFormatter yTickLabelFormatter;

  private final DispatchAsync dispatcher;

  private final BiMap<PrioritizedLabel, GChart.Curve> curves;

  private final Set<PrioritizedLabel> initialHiddenLabels;

  private final GlobalPendingRequests globalPendingRequests;

  @Inject
  public GraphPresenter(EventBus eventBus, MyView view, DispatchAsync dispatcher, PlaceManager placeManager,
                        SimpleTickLabelFormatter yTickLabelFormatter, GlobalPendingRequests globalPendingRequests) {
    super(eventBus, view);
    this.dispatcher = dispatcher;
    this.globalPendingRequests = globalPendingRequests;
    this.placeManager = placeManager;
    this.queries = Lists.newArrayList();
    this.curves = HashBiMap.create();
    this.initialHiddenLabels = Sets.newHashSet();
    this.yTickLabelFormatter = yTickLabelFormatter;

    getView().setUiHandlers(this);
    getView().getChart().setHoverParameterInterpreter(this);
  }

  public Context getContext() {
    return context;
  }

  public String getGraphId() {
    return graphId;
  }

  public String getTitle() {
    return title;
  }

  public int getXTickResolution() {
    return xTickResolution;
  }

  public TickLabelFormatter getYTickLabelFormatter() {
    return yTickLabelFormatter;
  }

  public int getYTickResolution() {
    return yTickResolution;
  }

  public PlaceRequest getZoomRequest() {
    List<String> hidden = Lists.newArrayList();
    for (Map.Entry<PrioritizedLabel, GChart.Curve> curve : curves.entrySet()) {
      if (!curve.getValue().isVisible()) {
        PrioritizedLabel label = curve.getKey();
        hidden.add(label.toParamValue());
      }
    }

    PlaceRequest zoomRequest = new PlaceRequest(NameTokens.ZOOM_PAGE)
        .with("graph", getGraphId())
        .with("start", String.valueOf(getContext().getStart()))
        .with("tz", getContext().getTimeZoneParamValue())
        .with("range", getContext().getRange().getParamValue())
        .with("hidden", Joiner.on(",").join(hidden));

    return zoomRequest;
  }

  public GraphPresenter addQuery(Query query) {
    this.queries.add(query);
    return this;
  }

  public void enableZoom() {
    Anchor anchor = new Anchor(title);
    anchor.addClickHandler(this);
    getView().setTitleWidget(anchor);
  }

  @Override
  public String getHoverParameter(String param, GChart.Curve.Point point) {
    if ("Y".equals(param)) {
      return yTickLabelFormatter.format(getView().getChart().getYAxis(), point.getY());
    } else if ("X".equals(param)) {
      return formatXTick((long) point.getX(), (long) getView().getChart().getXAxis().getDataMin(), false);
    } else if ("L".equals(param)) {
      return curves.inverse().get(point.getParent()).getLabel();
    }
    return null;
  }

  private String formatXTick(long value, long minValue, boolean split) {
    DateTimeFormat format;
    if (new Date().getTime() - minValue > 24 * 60 * 60 * 1000) {
      format = LONG_DATE_TIME_FORMAT;
    } else {
      format = SHORT_DATE_TIME_FORMAT;
    }

    String label = format.format(new Date(value), context.getTimeZone());
    if (split) {
      label = label.replaceAll(" ", "<br/>");
    }
    return label;
  }

  public GraphPresenter hideLabel(PrioritizedLabel label) {
    initialHiddenLabels.add(label);
    return this;
  }

  @Override
  protected void onBind() {
    super.onBind();
    addRegisteredHandler(RangeChangeEvent.getType(), this);
    addRegisteredHandler(TimeZoneChangeEvent.getType(), this);
    addRegisteredHandler(StartTimeChangeEvent.getType(), this);
  }

  @Override
  public void onClick(ClickEvent event) {
    placeManager.revealRelativePlace(getZoomRequest());
  }

  @Override
  public void onLabelToggle(PrioritizedLabel label, boolean value) {
    curves.get(label).setVisible(value);

    updateYAxis();

    MyView view = getView();
    GChart chart = view.getChart();
    chart.update();
  }

  private void updateYAxis() {
    MyView view = getView();
    GChart chart = view.getChart();
    GChart.Axis yAxis = chart.getYAxis();

    yAxis.clearTicks();

    if (yTickLabelFormatter == null) {
      yTickLabelFormatter = new SimpleTickLabelFormatter();
    }

    int steps = (int) Math.round(Math.ceil((double) chart.getXChartSize() / yTickResolution));

    double step;
    if (yAxis.getDataMax() > .9) {
      step = Math.round(Math.ceil(yAxis.getDataMax() * 1.1 / (steps - 1)));
    } else if (yAxis.getDataMax() > 0) {
      step = yAxis.getDataMax() * 1.1 / (steps - 1);
    } else {
      step = (double) 1 / (steps - 1);
    }

    yAxis.setAxisMax(step * (steps - 1));

    for (int i = 0; i < steps; i++) {
      yAxis.addTick(i * step, yTickLabelFormatter.format(yAxis, i * step));
    }
  }

  @Override
  public void onRangeChange(RangeChangeEvent event) {
    if (event.getContext() == context) {
      fetchData();
    }
  }

  public double getValueForPointsAt(List<DataPoint> points, long atTimestamp,
      String name) {
    for (int i = 0; i < points.size(); i++) {
      boolean lastIteration = (i == points.size() - 1);
      DataPoint point = points.get(i);
      if (point.getTimestamp() == atTimestamp) {
        // If we have the exact point, return it.
        return point.getValue();
      } else if (point.getTimestamp() > atTimestamp) {
        // If we've iterated to a point beyond the timestamp we are looking
        // for, then the previous point will be before the timestamp, so we'll
        // average the two and estimate the value for the exact timestamp.
        DataPoint prevPoint = (i - 1 >= 0) ? points.get(i - 1) : null;
        if (prevPoint == null) {
          // If there is no last timestamp, that means this is the first point.
          return point.getValue();
        }
        double deltaBegEnd = (double) point.getTimestamp() - prevPoint.getTimestamp();
        double deltaMidEnd = (double) point.getTimestamp() - atTimestamp;
        double deltaBegEndVal = point.getValue() - prevPoint.getValue();
        double pctBetwBegEnd = (1 - (deltaMidEnd / deltaBegEnd));
        // The estimated value at the mid point.
        return (pctBetwBegEnd * deltaBegEndVal) + prevPoint.getValue();
      } else if (lastIteration == true) {
        // If this is the last point in this set of points, return it.
        return point.getValue();
      }
    }
    return 0;
  }

  public void addAllLinePoint(List<DataPoint> totals,
      Map<Metric, List<DataPoint>> mapEntries, Map.Entry<Metric,
      List<DataPoint>> entry, Query query) {
    List<DataPoint> points = entry.getValue();
    for (DataPoint point : points) {
      boolean totalHasIt = false;
      for (DataPoint total : totals) {
        if (total.getTimestamp() == point.getTimestamp()) {
          totalHasIt = true;
        }
      }
      if (totalHasIt == false) {
        double total = point.getValue();
        for (Map.Entry<Metric, List<DataPoint>> entryForTotals : mapEntries.entrySet()) {
          if (entryForTotals != entry) {
            String name = query.getLabelExtractor().extract(entryForTotals.getKey().getTags());
            double val = getValueForPointsAt(entryForTotals.getValue(), point.getTimestamp(), name);
            total += val;
          }
        }
        totals.add(new DataPoint(point.getTimestamp(), total));
      }
    }
  }

  public void addAllLineToData(List<DataPoint> totals, Query query,
      Map<PrioritizedLabel, List<DataPoint>> data) {
    Collections.sort(totals, new Comparator<DataPoint>() {
      @Override
      public int compare(DataPoint o1, DataPoint o2) {
        long result = o1.getTimestamp() - o2.getTimestamp();
        if (result < 0) {
          return -1;
        } else if (result > 0) {
          return 1;
        } else {
          return 0;
        }
      }
    });

    data.put(new PrioritizedLabel("all", query.getPriority()), totals);
  }

  private void fetchData() {
    final Map<PrioritizedLabel, List<DataPoint>> data = Maps.newHashMap();

    final int[] pendingRequests = {queries.size()};

    for (final Query query : queries) {
      String requestQuery = query.toTsdbQuery(context.getRange());

      if (globalPendingRequests.increment() == 1) {
        DataUpdateEvent.fire(getEventBus(), false, false);
      }

      final long start = context.getStart();
      final long end = context.getEnd();

      dispatcher.execute(new FetchChartData(requestQuery, start, end),
          new AsyncCallback<FetchChartData.Result>() {

            @Override
            public void onFailure(Throwable caught) {
              if (globalPendingRequests.decrement() == 0) {
                DataUpdateEvent.fire(getEventBus(), true, false);
              }

              pendingRequests[0]--;
              if (pendingRequests[0] == 0) {
                GraphPresenter.this.start = start;
                GraphPresenter.this.end = end;
                updateGraph(data);
              }

              BackendErrorEvent.fire(getEventBus(), caught);
            }

            @Override
            public void onSuccess(FetchChartData.Result result) {
              if (globalPendingRequests.decrement() == 0) {
                DataUpdateEvent.fire(getEventBus(), true, true);
              }

              pendingRequests[0]--;

              Map<Metric, List<DataPoint>> mapEntries = result.getPoints();
              List<DataPoint> totals = new ArrayList<DataPoint>();
              boolean createAllLine = query.getIncludeAllLine() == true;
              for (Map.Entry<Metric, List<DataPoint>> entry : mapEntries.entrySet()) {
                Metric metric = entry.getKey();
                String label = query.getLabelExtractor().extract(metric.getTags());
                data.put(new PrioritizedLabel(label, query.getPriority()), entry.getValue());

                if (createAllLine == true) {
                  addAllLinePoint(totals, mapEntries, entry, query);
                }
              }

              if (createAllLine == true) {
                addAllLineToData(totals, query, data);
              }

              if (pendingRequests[0] == 0) {
                GraphPresenter.this.start = start;
                GraphPresenter.this.end = end;
                updateGraph(data);
              }
            }

          }
      );
    }
  }

  private void updateGraph(Map<PrioritizedLabel, List<DataPoint>> data) {
    MyView view = getView();
    GChart chart = view.getChart();

    configureAxis(chart);

    Set<PrioritizedLabel> leftoverLabels = Sets.newHashSet();
    leftoverLabels.addAll(curves.keySet());

    for (Map.Entry<PrioritizedLabel, List<DataPoint>> entry : data.entrySet()) {
      PrioritizedLabel label = entry.getKey();
      leftoverLabels.remove(label);

      GChart.Curve curve = getCurve(label);

      curve.clearPoints();
      List<DataPoint> points = entry.getValue();
      for (DataPoint point : points) {
        curve.addPoint(point.getTimestamp(), point.getValue());
      }
    }

    initialHiddenLabels.clear();

    for (PrioritizedLabel label : leftoverLabels) {
      chart.removeCurve(curves.remove(label));
      view.removeLabel(label);
    }

    updateXAxis();
    updateYAxis();

    chart.update();
  }

  private void configureAxis(GChart chart) {
    GChart.Axis xAxis = chart.getXAxis();
    xAxis.setHasGridlines(true);
    xAxis.setTickCount(GChart.NAI);
    xAxis.setTickLabelFormat("=(Date)HH:mm");

    GChart.Axis yAxis = chart.getYAxis();
    yAxis.setAxisMin(0);
    yAxis.setHasGridlines(true);
    yAxis.setTickCount(GChart.NAI);
  }

  private GChart.Curve getCurve(PrioritizedLabel label) {
    MyView view = getView();
    GChart chart = view.getChart();

    GChart.Curve curve = curves.get(label);
    if (curve == null) {
      chart.addCurve();
      curve = chart.getCurve();
      curves.put(label, curve);

      GChart.Symbol symbol = curve.getSymbol();
      symbol.setSymbolType(GChart.SymbolType.LINE);
      symbol.setBorderColor(COLORS[curves.size() % COLORS.length]);
      symbol.setBackgroundColor(symbol.getBorderColor());
      symbol.setFillThickness(3);
      symbol.setWidth(7);
      symbol.setHeight(7);
      symbol.setHovertextTemplate(GChart.formatAsHovertext("${L} @ ${X} = ${Y}"));

      if (initialHiddenLabels.contains(label)) {
        curve.setVisible(false);
      }

      view.addLabel(label, symbol.getBorderColor(), curve.isVisible());
    }
    return curve;
  }

  @SuppressWarnings({"deprecation"})
  private void updateXAxis() {
    MyView view = getView();
    GChart chart = view.getChart();
    GChart.Axis xAxis = chart.getXAxis();

    xAxis.setAxisMin(start);
    if (end != -1) {
      xAxis.setAxisMax(end);
    } else {
      xAxis.setAxisMax(xAxis.getDataMax());
    }

    xAxis.clearTicks();

    int steps = (int) Math.round(Math.ceil((double) chart.getYChartSize() / yTickResolution));
    double step = (xAxis.getAxisMax() - start) / (steps - 1);

    for (int i = 0; i < steps; i++) {
      double position = start + i * step;
      xAxis.addTick(position, "<html>" + formatXTick((long) position, (long) xAxis.getDataMin(), true));
    }
  }

  @Override
  protected void onReset() {
    super.onReset();
    fetchData();
  }

  @Override
  protected void onReveal() {
    super.onReveal();
  }

  @Override
  public void onStartTimeChange(StartTimeChangeEvent event) {
    if (event.getContext() == context) {
      fetchData();
    }
  }

  @Override
  public void onTimeZoneChange(TimeZoneChangeEvent event) {
    if (event.getContext() == context) {
      updateXAxis();
      getView().getChart().update();
    }
  }

  public GraphPresenter setContext(Context context) {
    this.context = context;
    return this;
  }

  public GraphPresenter setGraphId(String graphId) {
    this.graphId = graphId;
    return this;
  }

  public GraphPresenter setHeight(int height) {
    getView().setHeight(height);
    getView().getChart().setYChartSize(height - 50);
    return this;
  }

  public void setNavigationWidget(Widget widget) {
    getView().setNavigationWidget(widget);
  }

  public GraphPresenter setTitle(String title) {
    this.title = title;
    getView().setTitleWidget(new Label(title));
    return this;
  }

  public GraphPresenter setWidth(int width) {
    getView().setWidth(width);
    getView().getChart().setXChartSize(width - 75);
    return this;
  }

  public GraphPresenter setXTickResolution(int xTickResolution) {
    this.xTickResolution = xTickResolution;
    return this;
  }

  public GraphPresenter setYTickLabelFormatter(TickLabelFormatter yTickLabelFormatter) {
    this.yTickLabelFormatter = yTickLabelFormatter;
    return this;
  }

  public GraphPresenter setYTickResolution(int yTickResolution) {
    this.yTickResolution = yTickResolution;
    return this;
  }

}
