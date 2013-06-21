#!/usr/bin/env escript

%%! -sname backup

-record(amqqueue, {name, durable, auto_delete, exclusive_owner, arguments, pid, slave_pids, sync_slave_pids, policy, gm_pids}).
-record(exchange, {name, type, durable, auto_delete, internal, arguments, scratches, policy}).

%% Mnesia restore only recover the db info for the queues and exchanges of rabbit, to make these queues and exchanges work, we still
%% need to redeclare these old items after clear its mnesia record info
main([Type, Name, Backup]) ->
if Type == "restore" ->
  Host = lists:concat([Name, "@localhost"]),
  %% Restore the mnesia database
  rpc:call(list_to_atom(Host), mnesia, restore, [Backup, []]),

  %% Get queues and exchanges info from restored db
  Queues = rpc:call(list_to_atom(Host), mnesia, dirty_select, [rabbit_durable_queue, [{#amqqueue{name='$1', durable='$2', auto_delete='$3', arguments='$4', _='_'}, [], ['$$']}]]),
  Exchanges = rpc:call(list_to_atom(Host), mnesia, dirty_select, [rabbit_durable_exchange, [{#exchange{name='$1', type='$2', durable='$3', auto_delete='$4', internal='$5', arguments='$6', _='_'}, [], ['$$']}]]),

  %% Remove all the queues and exchanges info in db
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

  %% Redeclare old queues and exchanges
  lists:foreach(fun(Queue) ->
    rpc:call(list_to_atom(Host), rabbit_amqqueue, declare, lists:append(Queue, [none])) end,
  Queues),
  lists:foreach(fun(Exchange) ->
    rpc:call(list_to_atom(Host), rabbit_exchange, declare, Exchange) end,
  Exchanges);
  Type == "backup" ->
    Host = lists:concat([Name, "@localhost"]),
    %% Backup the mnesia database
    rpc:call(list_to_atom(Host), mnesia, backup, [Backup])
end.
