REM https://gist.github.com/Kostassoid/8422347
set RABBITMQ_NODE_PORT=5673
set RABBITMQ_NODENAME=rabbit2 
set RABBITMQ_SERVICE_NAME=rabbit2 
set RABBITMQ_SERVER_START_ARGS=-rabbitmq_management listener [{port,15673}] 
REM call rabbitmq-server -detached
rabbitmq-server
