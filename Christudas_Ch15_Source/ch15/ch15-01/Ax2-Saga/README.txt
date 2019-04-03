ch15\ch15-01\Ax2-Saga

RabbitMQ Setup
--------------
cd D:\Applns\RabbitMQ\rabbitmq_server-3.6.3\sbin
D:\Applns\RabbitMQ\rabbitmq_server-3.6.3\sbin>D:\Applns\RabbitMQ\rabbitmq_server-3.6.3\sbin\rabbitmq-server.bat

MySQL Setup
-----------
cd D:\Applns\MySQL\mysql-8.0.14-winx64\bin
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysqld --console
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysql -u root -p

mysql> use ecom01;

show tables;

mysql> drop table ecom_order;
mysql> drop table ecom_product;
mysql> drop table ecom_order_view;
mysql> drop table ecom_product_view;
mysql> drop table ecom_order_audit;

Newly create the required tables

mysql> create table ecom_order (id integer not null, last_event_sequence_number bigint, version bigint, number integer, order_status varchar(255), price double precision, product_id integer, primary key (id)) ENGINE=InnoDB;
mysql> create table ecom_product (id integer not null, last_event_sequence_number bigint, version bigint, description varchar(255), price double precision, stock integer, primary key (id)) ENGINE=InnoDB;
mysql> alter table ecom_order add constraint FK_f3rnd79i90twafllfhpo1sihi foreign key (product_id) references ecom_product (id);
mysql> create table ecom_product_view(id INT , price DOUBLE, stock INT ,description VARCHAR(255));
mysql> create table ecom_order_view(id INT , price DOUBLE, number INT ,description VARCHAR(225),status VARCHAR(50));
mysql> create table ecom_order_audit(id INT ,status VARCHAR(50),date TIMESTAMP);

MongoDB Setup
-------------

Bring up MongoDB
cd D:\Applns\MongoDB\Server\3.2.6\bin
D:\Applns\MongoDB\Server\3.2.6\bin\mongod.exe --dbpath D:\Applns\MongoDB\Server\3.2.6\data

Mongo Console
cd D:\Applns\MongoDB\Server\3.2.6\bin
D:\Applns\MongoDB\Server\3.2.6\bin>D:\Applns\MongoDB\Server\3.2.6\bin\mongo

Mongo saga repository(application.properties)
=============================================
ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent\src\main\resources\application.properties
-----------------------
spring.data.mongodb.uri=mongodb://localhost:27017/test

MySQL Setup(application.properties)
===================================
ch15\ch15-01\Ax2-Saga\02-Ecom-CreateCommandRestController\src\main\resources\application.properties
ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent\src\main\resources\application.properties
ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore\src\main\resources\application.properties
ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit\src\main\resources\application.properties
-----------------
server.port=8081
spring.datasource.url=jdbc:mysql://localhost/ecom01
spring.datasource.username=root
spring.datasource.password=rootpassword

Rabbit MQSetup(application.properties)
=====================================
ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent\src\main\resources\application.properties
-----------------
ecom.amqp.rabbit.address= 127.0.0.1:5672
ecom.amqp.rabbit.username= guest
ecom.amqp.rabbit.password= guest
ecom.amqp.rabbit.vhost=/
ecom.amqp.rabbit.exchange=Ecom-02
ecom.amqp.rabbit.queue=Ecom-createcommand_01

ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore\src\main\resources\application.properties
------------------
ecom.amqp.rabbit.address= 127.0.0.1:5672
ecom.amqp.rabbit.username= guest
ecom.amqp.rabbit.password= guest
ecom.amqp.rabbit.vhost=/
ecom.amqp.rabbit.exchange=Ecom-02
ecom.amqp.rabbit.queue=Ecom-event-core_01

ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit\src\main\resources\application.properties
------------------
ecom.amqp.rabbit.address= 127.0.0.1:5672
ecom.amqp.rabbit.username= guest
ecom.amqp.rabbit.password= guest
ecom.amqp.rabbit.vhost=/
ecom.amqp.rabbit.exchange=Ecom-02
ecom.amqp.rabbit.queue=Ecom-event-history_01

Jgroups clustering
--------------------
udp_config.xml

0. Microservice Common Classes
------------------------------
cd D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\06-Ecom-common
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\06-Ecom-common>make
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\06-Ecom-common>mvn clean install

1. Microservice 1: 01-Ecom-web
------------------------------
cd D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\01-Ecom-web
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\01-Ecom-web>make
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\01-Ecom-web>mvn clean install

D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\01-Ecom-web>run
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\01-Ecom-web>java -jar .\target\01-Ecom-web-0.0.1-SNAPSHOT.jar

2. Microservice 2: 02-Ecom-CreateCommandRestController
------------------------------------------------------
Change
ch15\ch15-01\Ax2-Saga\02-Ecom-CreateCommandRestController\src\main\resources\application.properties

cd D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\02-Ecom-CreateCommandRestController
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\02-Ecom-CreateCommandRestController>make
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\02-Ecom-CreateCommandRestController>mvn clean install

D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\02-Ecom-CreateCommandRestController>run
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\02-Ecom-CreateCommandRestController>java -Dserver.port=8081 -Dlog4j.configurationFile=log4j2-spring.xml -jar .\target\02-Ecom-CreateCommandRestController-0.0.1-SNAPSHOT.jar

3. Microservice 3: 03-Ecom-HandleCommandAndCreateEvent
------------------------------------------------------
Change
ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent\src\main\resources\application.properties

cd D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent>make
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent>mvn clean install

D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent>run
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\03-Ecom-HandleCommandAndCreateEvent>java -Dserver.port=8082 -Dlog4j.configurationFile=log4j2-spring.xml -jar .\target\03-Ecom-HandleCommandAndSaga-0.0.1-SNAPSHOT.jar

4. Microservice 4: 04-Ecom-EventHandleCore
------------------------------------------
Change
ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore\src\main\resources\application.properties

cd D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore>make
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore>mvn clean install

D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore>run
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\04-Ecom-EventHandleCore>java -Dserver.port=8083 -jar .\target\04-Ecom-EventHandlerCore-0.0.1-SNAPSHOT.jar

5. Microservice 5: 05-Ecom-EventHandlerAudit
--------------------------------------------
Change
ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit\src\main\resources\application.properties

cd D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit>make
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit>mvn clean install

D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit>run
D:\binil\gold\pack03\ch15\ch15-01\Ax2-Saga\05-Ecom-EventHandlerAudit>java -Dserver.port=8084 -Dspring.application.name=product-audit-01 -jar .\target\05-Ecom-EventHandlerHistory-0.0.1-SNAPSHOT.jar

#--------- Fill Product Tables with initial data --------------

mysql> insert into ecom_product(id,description,price,stock,version) values(1,'Shirts',100,5,0);
mysql> insert into ecom_product(id,description,price,stock,version) values(2,'Pants',100,5,0);
mysql> insert into ecom_product(id,description,price,stock,version) values(3,'T-Shirt',100,5,0);
mysql> insert into ecom_product(id,description,price,stock,version) values(4,'Shoes',100,5,0);

mysql> insert into ecom_product_view(id,description,price,stock) values(1,'Shirts',100,5);
mysql> insert into ecom_product_view(id,description,price,stock) values(2,'Pants',100,5);
mysql> insert into ecom_product_view(id,description,price,stock) values(3,'T-Shirt',100,5);
mysql> insert into ecom_product_view(id,description,price,stock) values(4,'Shoes',100,5);


====================

http://localhost:8080/
(In case the change is not reflected, you may close the browser session and take a new browser instance and try above URL)


delete from ecom_order_audit;
delete from ecom_order_view;
delete from ecom_product_view;
delete from ecom_order;
delete from ecom_product;

drop table ecom_order;
drop table ecom_product;
drop table ecom_order_view;
drop table ecom_product_view;
drop table ecom_order_audit;

