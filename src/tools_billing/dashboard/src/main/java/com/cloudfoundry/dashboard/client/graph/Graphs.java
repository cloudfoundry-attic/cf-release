/**
 * Copyright (c) 2011 VMware, Inc.
 */

package com.cloudfoundry.dashboard.client.graph;

import com.cloudfoundry.dashboard.client.graph.axis.ByteTickLabelFormatter;
import com.cloudfoundry.dashboard.client.graph.axis.PercentTickLabelFormatter;
import com.cloudfoundry.dashboard.client.graph.axis.SimpleTickLabelFormatter;
import com.cloudfoundry.dashboard.client.graph.axis.TickLabelFormatter;
import com.cloudfoundry.dashboard.client.graph.label.ConstantLabelExtractor;
import com.cloudfoundry.dashboard.client.graph.label.TagLabelExtractor;
import com.cloudfoundry.dashboard.client.presenter.GraphPresenter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.Map;
import javax.annotation.Nullable;


/**
 * Graphs
 *
 * @author Vadim Spivak
 */
@Singleton
public class Graphs {

  private static String[] GROUPED_BY_TYPES = {"framework", "runtime"};
  private static String[] COMPONENT_ROLES = {"core", "service", "unknown"};
  private static Map<String, String> COMPONENT_NAMES =
      ImmutableMap.of("core", "Core", "service", "Services", "unknown", "Other");

  private final Provider<GraphPresenter> graphProvider;

  private final ByteTickLabelFormatter mbTickLabelFormatter;
  private final ByteTickLabelFormatter kbTickLabelFormatter;
  private final PercentTickLabelFormatter percentFormatter;

  private final Map<String, Provider<GraphPresenter>> graphProviders;

  @Inject
  public Graphs(Provider<GraphPresenter> graphProvider,
                @Named("MbFormatter") ByteTickLabelFormatter mbTickLabelFormatter,
                @Named("KbFormatter") ByteTickLabelFormatter kbTickLabelFormatter,
                @Named("PercentFormatter") PercentTickLabelFormatter percentFormatter) {
    this.graphProvider = graphProvider;
    this.mbTickLabelFormatter = mbTickLabelFormatter;
    this.kbTickLabelFormatter = kbTickLabelFormatter;
    this.percentFormatter = percentFormatter;
    this.graphProviders = Maps.newHashMap();

    for (String foo : new String[] {"hello", "bar"}) {}

    registerGraphs();
  }

  private void registerGraphs() {
    registerSummaryExpectedVitalsGraph("summary.expected_vitals");
    registerSummaryActualVitalsGraph("summary.actual_vitals");
    registerSummaryRequestsGraph("summary.requests");
    registerSummaryCapacity("summary.capacity");
    registerSummaryComponentsGraph("summary.components");
    registerSummaryComponentsMemoryGraph("summary.components_memory");

    for (String role: COMPONENT_ROLES) {
      registerSystemGraph("Load Average", "system.load.1m", role);

      registerSystemGraph("CPU", "system.cpu.user",
          role, null, percentFormatter);

      registerSystemGraph("Memory", "system.mem.percent", role, null, percentFormatter);

      registerSystemGraph("Ephemeral Disk", "system.disk.ephemeral.percent",
          role, null, percentFormatter);
      registerSystemGraph("System Disk", "system.disk.system.percent",
          role, null, percentFormatter);
      registerSystemGraph("Persistent Disk", "system.disk.persistent.percent",
          role, null, percentFormatter);
    }

    registerHttpResponseGraph("All", "http.all", null);
    registerHttpResponseGraph("2XX", "http.2xx", "2xx");
    registerHttpResponseGraph("3XX", "http.3xx", "3xx");
    registerHttpResponseGraph("4XX", "http.4xx", "4xx");
    registerHttpResponseGraph("5XX", "http.5xx", "5xx");
    registerHttpResponseGraph("Other", "http.xxx", "xxx");
    registerHttpLatencyGraph("http.latency");

    for (String groupedBy : GROUPED_BY_TYPES) {
      registerSimpleGroupedGraph("All Apps", "apps", groupedBy);
      registerSimpleGroupedGraph("Started Apps", "started_apps", groupedBy);
      registerSimpleGroupedGraph("Running Apps", "running_apps", groupedBy);
      registerSimpleGroupedGraph("All Instances", "instances", groupedBy);
      registerSimpleGroupedGraph("Running Instances", "running_instances", groupedBy);
      registerSimpleGroupedGraph("Flapping Instances", "flapping_instances", groupedBy);
      registerSimpleGroupedGraph("Missing Instances", "missing_instances", groupedBy);
      registerSimpleGroupedGraph("Started Instances", "started_instances", groupedBy);
      registerSimpleGroupedGraph("Crashes", "crashes", groupedBy);

      registerSimpleGroupedGraph("Reserved Memory for All", "memory", groupedBy, mbTickLabelFormatter);
      registerSimpleGroupedGraph("Reserved Memory for Started", "started_memory", groupedBy, mbTickLabelFormatter);
      registerSimpleGroupedGraph("Reserved Memory for Running", "reserved_memory", groupedBy, mbTickLabelFormatter);
      registerSimpleGroupedGraph("Used Memory", "used_memory", groupedBy, mbTickLabelFormatter);
      registerSimpleGroupedGraph("Used CPU", "used_cpu", groupedBy);
      registerSimpleGroupedGraph("Used Disk", "used_disk", groupedBy, kbTickLabelFormatter);

      registerHttpGroupedResponseGraph("All", "http_all", null, groupedBy);
      registerHttpGroupedResponseGraph("2XX", "http_2xx", "2xx", groupedBy);
      registerHttpGroupedResponseGraph("3XX", "http_3xx", "3xx", groupedBy);
      registerHttpGroupedResponseGraph("4XX", "http_4xx", "4xx", groupedBy);
      registerHttpGroupedResponseGraph("5XX", "http_5xx", "5xx", groupedBy);
      registerHttpGroupedResponseGraph("Other", "http_xxx", "xxx", groupedBy);
      registerHttpGroupedLatencyGraph(groupedBy);
    }
  }

  private void registerHttpGroupedLatencyGraph(final String groupedBy) {
    final String id = groupedBy + "s.http_latency";
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Latency")
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.AVG)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("router.latency.1m")
                .setTags(ImmutableMap.<String, String>of(groupedBy, "*"))
                .setLabelExtractor(new TagLabelExtractor(groupedBy))
                .setPriority(1)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerHttpGroupedResponseGraph(final String title, final String suffix, @Nullable final String status,
                                                final String groupedBy) {
    final String id = groupedBy + "s." + suffix;
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        Map<String, String> tags = Maps.newHashMap();
        tags.put(groupedBy, "*");
        if (status != null) {
          tags.put("status", status);
        }
        return graphProvider.get()
            .setTitle(title)
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("router.responses")
                .setIncludeAllLine(true)
                .setRate(true)
                .setTags(tags)
                .setLabelExtractor(new TagLabelExtractor(groupedBy))
                .setPriority(1)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerHttpLatencyGraph(final String id) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Latency")
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.AVG)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("router.latency.1m")
                .setTags(ImmutableMap.<String, String>of("component", "*"))
                .setLabelExtractor(new TagLabelExtractor("component"))
                .setPriority(1)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerHttpResponseGraph(final String title, final String id, @Nullable final String status) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        Map<String, String> tags = Maps.newHashMap();
        tags.put("component", "*");
        if (status != null) {
          tags.put("status", status);
        }
        return graphProvider.get()
            .setTitle(title)
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("router.responses")
                .setIncludeAllLine(true)
                .setRate(true)
                .setTags(tags)
                .setLabelExtractor(new TagLabelExtractor("component"))
                .setPriority(1)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSimpleGroupedGraph(final String title, final String metric, final String groupedBy) {
    registerSimpleGroupedGraph(title, metric, groupedBy, null);
  }

  private void registerSimpleGroupedGraph(final String title, final String metric, final String groupedBy,
                                          @Nullable final TickLabelFormatter formatter) {
    final String id = groupedBy + "s." + metric;
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        GraphPresenter graph = graphProvider.get()
            .setTitle(title)
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric(groupedBy + "s." + metric)
                .setIncludeAllLine(true)
                .setTags(ImmutableMap.<String, String>of(groupedBy, "*"))
                .setLabelExtractor(new TagLabelExtractor(groupedBy))
                .setPriority(1)
                .createQuery());

        if (formatter != null) {
          graph.setYTickLabelFormatter(formatter);
        }

        return graph;
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSummaryActualVitalsGraph(final String id) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Actual Vitals")
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.running_apps")
                .setLabelExtractor(new ConstantLabelExtractor("Running Apps"))
                .setPriority(1)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.running_instances")
                .setLabelExtractor(new ConstantLabelExtractor("Running Instances"))
                .setPriority(2)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.flapping_instances")
                .setLabelExtractor(new ConstantLabelExtractor("Flapping Instances"))
                .setPriority(2)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.missing_instances")
                .setLabelExtractor(new ConstantLabelExtractor("Missing Instances"))
                .setPriority(2)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.crashes")
                .setLabelExtractor(new ConstantLabelExtractor("Crashes"))
                .setPriority(2)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSummaryCapacity(final String id) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Capacity")
            .setGraphId(id)
            .setYTickLabelFormatter(mbTickLabelFormatter)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("dea.max_memory")
                .setLabelExtractor(new ConstantLabelExtractor("Capacity"))
                .setPriority(1)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.reserved_memory")
                .setLabelExtractor(new ConstantLabelExtractor("Reserved for Running"))
                .setPriority(2)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.used_memory")
                .setLabelExtractor(new ConstantLabelExtractor("Used"))
                .setPriority(3)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.started_memory")
                .setLabelExtractor(new ConstantLabelExtractor("Reserved for Started"))
                .setPriority(4)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.memory")
                .setLabelExtractor(new ConstantLabelExtractor("Reserved for All"))
                .setPriority(5)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSummaryComponentsGraph(final String id) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Components")
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("healthy")
                .setIncludeAllLine(true)
                .setTags(ImmutableMap.<String, String>of("role", "core", "job", "*"))
                .setLabelExtractor(new TagLabelExtractor("job"))
                .setPriority(1)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSummaryComponentsMemoryGraph(final String id) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Components Memory")
            .setGraphId(id)
            .setYTickLabelFormatter(mbTickLabelFormatter)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("mem")
                .setIncludeAllLine(true)
                .setTags(ImmutableMap.<String, String>of("role", "core", "job", "*"))
                .setLabelExtractor(new TagLabelExtractor("job"))
                .setPriority(1)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSummaryExpectedVitalsGraph(final String id) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Expected Vitals")
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("total_users")
                .setLabelExtractor(new ConstantLabelExtractor("Total Users"))
                .setPriority(1)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.apps")
                .setLabelExtractor(new ConstantLabelExtractor("Total Apps"))
                .setPriority(2)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.started_apps")
                .setLabelExtractor(new ConstantLabelExtractor("Started Apps"))
                .setPriority(3)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.instances")
                .setLabelExtractor(new ConstantLabelExtractor("Total Instances"))
                .setPriority(4)
                .createQuery())
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("frameworks.started_instances")
                .setLabelExtractor(new ConstantLabelExtractor("Started Instances"))
                .setPriority(5)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSummaryRequestsGraph(final String id) {
    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {

      @Override
      public GraphPresenter get() {
        return graphProvider.get()
            .setTitle("Requests")
            .setGraphId(id)
            .addQuery(new Query.Builder()
                .setAggregator(Query.Aggregator.SUM)
                .setDownsamplingAggregator(Query.Aggregator.AVG)
                .setMetric("router.requests")
                .setIncludeAllLine(true)
                .setRate(true)
                .setTags(ImmutableMap.<String, String>of("component", "*"))
                .setLabelExtractor(new TagLabelExtractor("component"))
                .setPriority(1)
                .createQuery());
      }

    };
    graphProviders.put(id, provider);
  }

  private void registerSystemGraph(final String title, final String metricName, final String role) {
    registerSystemGraph(title, metricName, role, null, null);
  }

  private void registerSystemGraph(final String title, final String metricName,
                                   final String role, @Nullable final Query.Aggregator aggregator,
                                   @Nullable final TickLabelFormatter formatter) {
    final String id = metricName + "." + role;
    final String graphTitle = title + " (" + COMPONENT_NAMES.get(role) + ")";

    Provider<GraphPresenter> provider = new Provider<GraphPresenter>() {
      @Override
      public GraphPresenter get() {
        Query.Aggregator effectiveAggregator = aggregator;
        TickLabelFormatter effectiveFormatter = formatter;

        if (effectiveAggregator == null) {
          effectiveAggregator = Query.Aggregator.MAX;
        }

        if (effectiveFormatter == null) {
          effectiveFormatter = new SimpleTickLabelFormatter();
        }

        return graphProvider.get()
            .setTitle(graphTitle)
            .setGraphId(id)
            .setYTickLabelFormatter(effectiveFormatter)
            .addQuery(new Query.Builder()
                .setAggregator(effectiveAggregator)
                .setDownsamplingAggregator(effectiveAggregator)
                .setMetric(metricName)
                .setTags(ImmutableMap.<String, String>of("role", role, "job", "*"))
                .setLabelExtractor(new TagLabelExtractor("job"))
                .setPriority(1)
                .createQuery());
      }
    };
    graphProviders.put(id, provider);
  }

  public GraphPresenter getGraph(String name) {
    return graphProviders.get(name).get();
  }

}
