VCAP Metric Collector
=====================
The `collector` will discover the various components on the message bus and
query their /healthz and /varz interfaces.

Based on the results it will publish metrics to TSDB for `mem` usage and if
the component is healthy. Additional metrics can be written by providing
`Handler` plugins. See `lib/collector/handler.rb` and
`lib/collector/handlers/dea.rb` for an example.


