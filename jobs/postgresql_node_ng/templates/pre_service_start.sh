<%
service = "postgresql"
plan_enabled = properties.service_plans && properties.service_plans.send(service.to_sym)
plan = properties.plan || "free"
plan_conf = plan_enabled && properties.service_plans.send(service.to_sym).send(plan.to_sym).configuration
%>
<% if plan_conf && plan_conf.shmmax %>
sysctl -w 'kernel.shmmax=<%=plan_conf.shmmax%>'
<%else%>
sysctl -w 'kernel.shmmax=284934144'
<%end%>

<% if plan_conf && plan_conf.shmall%>
sysctl -w 'kernel.shmall=<%=plan_conf.shmall%>'
<%else%>
sysctl -w 'kernel.shmall=2097152'
<%end%>
