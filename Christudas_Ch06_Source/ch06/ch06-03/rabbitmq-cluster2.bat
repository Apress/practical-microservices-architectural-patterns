call rabbitmqctl -n rabbit2 stop_app 
call rabbitmqctl -n rabbit2 join_cluster rabbit1@tiger
call rabbitmqctl -n rabbit2 start_app 
call rabbitmqctl -n rabbit1 set_policy ha-all "^.*" "{""ha-mode"":""all""}" 
