#!/usr/bin/env escript

%%! -sname backup

-record(amqqueue, {name, durable, auto_delete, exclusive_owner, arguments, pid, slave_pids, mirror_nodes}).
-record(exchange, {name, type, durable, auto_delete, internal, arguments, scratch}).

main([Type, Name, Backup]) ->
if Type == "restore" ->
  Host = lists:concat([Name, "@localhost"]),
  rpc:call(list_to_atom(Host), mnesia, restore, [Backup, []]),
  Queues = rpc:call(list_to_atom(Host), mnesia, dirty_select, [rabbit_durable_queue, [{#amqqueue{name='$1', durable='$2', auto_delete='$3', arguments='$4', _='_'}, [], ['$$']}]]),
  Exchanges = rpc:call(list_to_atom(Host), mnesia, dirty_select, [rabbit_durable_exchange, [{#exchange{name='$1', type='$2', durable='$3', auto_delete='$4', internal='$5', arguments='$6', _='_'}, [], ['$$']}]]),
  Fdel = fun() ->
    QueueKeys = mnesia:all_keys(rabbit_durable_queue),
    lists:foreach(
      fun(Key) -> mnesia:delete(rabbit_durable_queue, Key, write) end,
    QueueKeys),
    ExchangeKeys = mnesia:all_keys(rabbit_durable_exchange),
    lists:foreach(
      fun(Key) -> mnesia:delete(rabbit_durable_exchange, Key, write) end,
    ExchangeKeys)
  end,
  rpc:call(list_to_atom(Host), mnesia, transaction, [Fdel]),
  lists:foreach(fun(Queue) ->
    rpc:call(list_to_atom(Host), rabbit_amqqueue, declare, lists:append(Queue, [none])) end,
  Queues),
  lists:foreach(fun(Exchange) ->
    rpc:call(list_to_atom(Host), rabbit_exchange, declare, Exchange) end,
  Exchanges);
  Type == "backup" ->
    Host = lists:concat([Name, "@localhost"]),
    rpc:call(list_to_atom(Host), mnesia, backup, [Backup])
end.
