Ax3-Commands-Events-Distributed_2018-Jul-25

MySQL Setup(application.properties)
-----------------
server.port=8081
spring.datasource.url=jdbc:mysql://localhost/ecom01
spring.datasource.username=root
spring.datasource.password=rootpassword

Rabbit MQSetup(application.properties)
-----------------
spring.rabbitmq.host = 127.0.0.1
spring.rabbitmq.port = 5672
spring.rabbitmq.username = guest
spring.rabbitmq.password = guest
spring.rabbitmq.virtual-host: /  

axon.amqp.exchange: Ecom-02 
axon.amqp.queue: Ecom-createcommand 

# Bring Up RabbitMQ Server
(You may want to flush the RabbitMQ queues. This can be done easily by bringing down RabbitMQ, delete the data folders and then bring back RabbitMQ. Refer Appendix B for related details)
D:\Applns\RabbitMQ\rabbitmq_server-3.6.3\sbin>D:\Applns\RabbitMQ\rabbitmq_server-3.6.3\sbin\rabbitmq-server.bat






=====================

# Bring Up MySQL Server
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysqld --console

# Open MySQL prompt
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysql -u root -p

show tables;

#---------- Clean tables for the sample ------------
mysql> 

drop table ecom_order;
drop table ecom_product;
drop table ecom_order_view;
drop table ecom_product_view;
drop table ecom_order_audit;

#---------- Explicitly create tables ------------
mysql> 

create table ecom_order (id integer not null, last_event_sequence_number bigint, version bigint, number integer, order_status varchar(255), price double precision, product_id integer, primary key (id)) ENGINE=InnoDB;
create table ecom_product (id integer not null, last_event_sequence_number bigint, version bigint, description varchar(255), price double precision, stock integer, primary key (id)) ENGINE=InnoDB;
alter table ecom_order add constraint FK_f3rnd79i90twafllfhpo1sihi foreign key (product_id) references ecom_product (id);
create table ecom_product_view(id INT , price DOUBLE, stock INT ,description VARCHAR(255));
create table ecom_order_view(id INT , price DOUBLE, number INT ,description VARCHAR(225),status VARCHAR(50));
create table ecom_order_audit(id INT ,status VARCHAR(50),date TIMESTAMP);

#---------- Bring up Axon 4 Server ------------

Download and expand https://download.axoniq.io/axonserver/AxonServer.zip
cd D:\binil\Study\Sites\axon\AxonQuickStart-4.0.3_2019-02-19\AxonServer
java -jar axonserver-4.0.3.jar

#--------- Build & Run Micro Services --------------

cd D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\06-Ecom-common
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\06-Ecom-common>make
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\06-Ecom-common>mvn clean install

Change:
ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\00-Ecom-registry\src\main\resources\application.properties

D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\00-Ecom-registry>make
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\00-Ecom-registry>mvn clean install
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\00-Ecom-registry>run
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\00-Ecom-registry>java -jar .\target\00-Ecom-registry-0.0.1-SNAPSHOT.jar

Change:
ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\01-Ecom-web\src\main\resources\application.properties

D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\01-Ecom-web>make
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\01-Ecom-web>mvn clean install
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\01-Ecom-web>run
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\01-Ecom-web>java -jar .\target\01-Ecom-web-0.0.1-SNAPSHOT.jar

Change:
ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\02-Ecom-CreateCommandRestController\src\main\resources\application.properties

D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\02-Ecom-CreateCommandRestController>make
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\02-Ecom-CreateCommandRestController>mvn clean install
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\02-Ecom-CreateCommandRestController>run
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\02-Ecom-CreateCommandRestController>java -jar .\target\02-Ecom-CreateCommandRestC
ontroller-0.0.1-SNAPSHOT.jar

Change:
ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\03-Ecom-HandleCommandAndCreateEvent\src\main\resources\application.properties

D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\03-Ecom-HandleCommandAndCreateEvent>make
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\03-Ecom-HandleCommandAndCreateEvent>mvn clean install
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\03-Ecom-HandleCommandAndCreateEvent>run
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\03-Ecom-HandleCommandAndCreateEvent>java -jar .\target\03-Ecom-HandleCommandAndCreateEvent-0.0.1-SNAPSHOT.jar

Change:
ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\04-Ecom-EventHandleCore\src\main\resources\application.properties

D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\04-Ecom-EventHandleCore>make
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\04-Ecom-EventHandleCore>mvn clean install
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\04-Ecom-EventHandleCore>run
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\04-Ecom-EventHandleCore>java -jar .\target\04-Ecom-EventHandleCore-0.0.1-SNAPSHOT.jar

Change:
ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit\src\main\resources\application.properties

D:\binil\gold\shuffle\pack02\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>make
D:\binil\gold\shuffle\pack02\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>mvn clean install
D:\binil\gold\shuffle\pack02\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>run
D:\binil\gold\shuffle\pack02\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>java -Dserver.port=8084 -Dspring.application.na
me=product-audit-01 -jar .\target\05-Ecom-EventHandlerAudit-0.0.1-SNAPSHOT.jar

D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>make
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>mvn clean install
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>run
D:\binil\gold\pack03\ch19\ch19-02\Ax3-Commands-Multi-Event-Handler-Distributed\05-Ecom-EventHandlerAudit>java -Dserver.port=8084 -Dspring.application.name=product-audit-01 -jar .\target\05-Ecom-EventHandlerAudit-0.0.1-SNAPSHOT.jar
#--------- Product initial data --------------
mysql> 

insert into ecom_product(id,description,price,stock,version) values(1,'Shirts',100,5,0);
insert into ecom_product(id,description,price,stock,version) values(2,'Pants',100,5,0);
insert into ecom_product(id,description,price,stock,version) values(3,'T-Shirt',100,5,0);
insert into ecom_product(id,description,price,stock,version) values(4,'Shoes',100,5,0);

insert into ecom_product_view(id,description,price,stock) values(1,'Shirts',100,5);
insert into ecom_product_view(id,description,price,stock) values(2,'Pants',100,5);
insert into ecom_product_view(id,description,price,stock) values(3,'T-Shirt',100,5);
insert into ecom_product_view(id,description,price,stock) values(4,'Shoes',100,5);

#---------- Test the sample ------------


Chrome Borwser preferred
http://localhost:8080/
(In case the change is not reflected, you may close the browser session and take a new browser instance and try above URL)





