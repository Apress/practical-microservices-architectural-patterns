set RABBITMQ_NODE_PORT=5672
set RABBITMQ_NODENAME=rabbit1
set RABBITMQ_SERVICE_NAME=rabbit1
set RABBITMQ_SERVER_START_ARGS=-rabbitmq_management listener [{port,15672}] 
REM rabbitmq-server -detached
rabbitmq-server
