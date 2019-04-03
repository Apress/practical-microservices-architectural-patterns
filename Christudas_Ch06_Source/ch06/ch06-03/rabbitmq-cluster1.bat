call rabbitmqctl -n rabbit1 stop_app 
call rabbitmqctl -n rabbit1 join_cluster rabbit2@tiger
call rabbitmqctl -n rabbit1 start_app 
call rabbitmqctl -n rabbit2 set_policy ha-all "^.*" "{""ha-mode"":""all""}" 
