cd D:\binil\gold\pack03\ch18\ch18-01\Ax2-Ecom-With-Security
mvn eclipse:eclipse

===========================================BEFORE STARTING MICRO SERVICES ====================================================

1)-------------RABBITMQ SERVER SETUP----------
(You may want to flush the RabbitMQ queues. This can be done easily by bringing down RabbitMQ, delete the data folders and then bring back RabbitMQ. Refer Appendix B for related details)
D:\Applns\RabbitMQ\rabbitmq_server-3.6.3\sbin>rabbitmq-server

1)-------------MYSQL SERVER SETUP----------
Start MySQL Server
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysqld --console

Connect to MySQL Server
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysql -u root -p
mysql> SHOW DATABASES;
mysql> drop database ecom;
mysql> create database ecom;
mysql> use ecom;

Alternatively, you could delete below tables if they exist so that they will be created automatically later.
mysql> drop table inventory;
mysql> drop table line_item;
mysql> drop table customer_order;
mysql> drop table user_info;
mysql> drop table address;
mysql> drop table user_role;
mysql> drop table user_credential;


1)-------------MONGO DB SERVER SETUP----------
D:\Applns\MongoDB\Server\3.2.6\bin>mongod --dbpath D:\Applns\MongoDB\Server\3.2.6\data

D:\Applns\MongoDB\Server\3.2.6\bin>D:\Applns\MongoDB\Server\3.2.6\bin\mongo
MongoDB shell version: 3.2.6
connecting to: test
> show dbs
axonframework  0.000GB
ecom           0.000GB
local          0.000GB
test           0.000GB
> use axonframework
switched to db axonframework
> db.dropDatabase()
{ "dropped" : "axonframework", "ok" : 1 }
> use ecom
switched to db ecom
> db.dropDatabase()
{ "dropped" : "ecom", "ok" : 1 }
>

1)Load Category data
 ----------------------------

cd D:\Applns\MongoDB\Server\3.2.6\bin
mongoimport --db ecom --jsonArray --collection productCategory --file D://binil/gold/pack03/ch18/ch18-01/Ax2-Ecom-With-Security/Ecom-xtra-setup/productCategory.json

2)Load product data
--------------------------

mongoimport --db ecom --jsonArray --collection product --file D://binil/gold/pack03/ch18/ch18-01/Ax2-Ecom-With-Security/Ecom-xtra-setup/products.json


3)Load inventory(Initial CQRS Query data - read model of inventory)
---------------------------------

mongoimport --db ecom --jsonArray --collection inventory --file D://binil/gold/pack03/ch18/ch18-01/Ax2-Ecom-With-Security/Ecom-xtra-setup/inventory.json

> show collections
inventory
product
productCategory
>

1)-------------CONFIG SERVER SETUP----------

Ensure config uri is proper and first start config server before any server 

Open ch18\Ax2-Ecom-With-Security\Ecom-config\src\main\resources\application.yml and update the value of the git uri: 

spring:
   application:
         name: ecom-config
   cloud:
     config:
      server: 
           git:
            uri: file://D:/binil/gold/pack03/ch18/ch18-01/Ax2-Ecom-With-Security/config-repo   

2)-------------- MICROSERVICES SETUP------ -----------------------

Ensure eureka clients are configured with correct service-registry

Go to "ch18\Ax2-Ecom-With-Security\config-repo" folder
Except ecom-registry.yml, update all other .yml file with following values:
eureka:
  client:
   serviceUrl:
    defaultZone: http://localhost:8761/eureka/

Open "ch18\Ax2-Ecom-With-Security\config-repo\*.yml" wherever applicable, ensure RabbitMQ configuration parameters are proper:

Change in config-repo\ecom-core.yml:
ecom:
  amqp:
   rabbit:
     address: localhost:5672
     username: guest
     password: guest
     vhost: /
     exchange: Ecom-exchange
     queue: Ecom-core-queue










Change in config-repo\ecom-delivery.yml:
ecom:
  amqp:
   rabbit:
     address: localhost:5672
     username: guest
     password: guest
     vhost: /
     exchange: Ecom-exchange
     queue: Ecom-delivery-queue




Change in config-repo\ecom-history.yml:
ecom:
  amqp:
   rabbit:
     address: localhost:5672
     username: guest
     password: guest
     vhost: /
     exchange: Ecom-exchange
     queue: Ecom-order-histo-queue
     
     
     
     
     
     
     
     
Change in config-repo\ecom-product.yml:
ecom:
  amqp:
   rabbit:
     address: localhost:5672
     username: guest
     password: guest
     vhost: /
     exchange: Ecom-exchange
     queue: Ecom-product-queue








Change in config-repo\ecom-shipping.yml:
ecom:
  amqp:
   rabbit:
     address: localhost:5672
     username: guest
     password: guest
     vhost: /
     exchange: Ecom-exchange
     queue: Ecom-shipping-queue








3)...............CLIENT APP SETUP-------------------------

Ensure client app connected to proper API Gateway

Go to ch18\Ax2-Ecom-With-Security\Ecom-web\src\main\resources\application.properties and update the value of apigateway.url:

spring.application.name: ecom-web
server.port=8080
ecom.apigateway.url=http://localhost:9000

4)..............DATABASE SETUP-------------------------

 4.1) Validate the port and host for mongo
  
Open "ch18\Ax2-Ecom-With-Security\config-repo\*.yml" wherever applicable, ensure MongoDB configuration parameters are proper:
     mongodb://localhost:27017/ecom 

 4.2) Create Mysql database ecom

     NOTE: (tables are created while starting  core micro-service but database 'ecom' should be created manually)
     
Open "ch18\Ax2-Ecom-With-Security\config-repo\*.yml" wherever applicable, ensure MySQL configuration parameters are proper:
    spring.datasource.url=jdbc:mysql://localhost/ecom
    spring.datasource.username=root
    spring.datasource.password=



5)..............IMAGE LOCATION-------------------------
modify below config property of ecom-product.yml of product microservice for image loaction, assuming the  images for product micro services resides in this location:

Open ch18\Ax2-Ecom-With-Security\config-repo\ecom-product.yml and update the value

ecom:
  product.img.location: D:/binil/gold/pack03/ch18/ch18-01/Ax2-Ecom-With-Security/Ecom-xtra-setup/productImg/


===========================================AFTER STARTING MICRO SERVICES ====================================================
mvn eclipse:eclipse

Preferred Microservice starting order in Eclipse:
Ecom-config
Ecom-registry
Ecom-gateway
Ecom-security
Ecom-user

Rest, you may follow in any order...


6)Load inventory(Initial CQRS Query data - read model of inventory)
Dependency: Micro services should be started so that tables are auto created
---------------------------------
mysql> show tables;

//insert inventory data same id & code as in mongodb for inventory 

insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(1,0,0,10,'LEE'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(2,0,0,10,'ACTION'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(3,0,0,10,'ADDIDAS');      
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(4,0,0,10,'NIKE');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(5,0,0,10,'LEE_1');
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(6,0,0,10,'ACTION_1');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(7,0,0,10,'LAXME'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(8,0,0,10,'LAXME13'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(9,0,0,10,'ADDIDAS_W');      
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(10,0,0,10,'WEDGES');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(11,0,0,10,'LEE_2');
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(12,0,0,10,'LEE_3');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(13,0,0,10,'RAYBAN1'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(14,0,0,10,'RAYBAN2'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(15,0,0,10,'RAYBAN3');      
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(16,0,0,10,'FASTTRACK1');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(17,0,0,10,'FASTTRACK2');
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(18,0,0,10,'FASTTRACK3');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(19,0,0,10,'ARROW1'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(20,0,0,10,'ARROW2'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(21,0,0,10,'ARROW3');      
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(22,0,0,10,'POLO1');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(23,0,0,10,'POLO2');
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(24,0,0,10,'POLO3');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(25,0,0,10,'DON1'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(26,0,0,10,'DON2'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(27,0,0,10,'DON3');      
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(28,0,0,10,'DON4'); 
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(29,0,0,10,'X-COTTEN1');
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(30,0,0,10,'X-COTTEN2');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(31,0,0,10,'X-COTTEN3');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(32,0,0,10,'X-COTTEN4');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(33,0,0,10,'X-COTTEN5');  
insert into inventory(id,last_event_sequence_number, version,quantity, sku) values(34,0,0,10,'X-COTTEN6');  
commit;

6)ADD Admin user 
----------------------------------

insert into user_credential (id,active,password,user_id) values (1,1,'admin','admin');
insert into user_role (id,role,user_id) values (1,'ROLE_ADMIN',1);
insert into user_role (id,role,user_id) values (2,'CUSTOMER_READ',1);
insert into user_role (id,role,user_id) values (3,'PRODUCT_WRITE',1);
insert into user_role (id,role,user_id) values (4,'ORDER_READ',1);
insert into user_role (id,role,user_id) values (5,'PRODUCT_WRITE',1);
insert into user_role (id,role,user_id) values (6,'ORDER_WRITE',1);
insert into user_info (id,email,first_name,last_name,phone,user_id) values(1,'admin@admin','admin','admin',903766787,'admin');
commit;

===========================================START APP===================================================
User url : http://localhost:8080/
Admin url : http://localhost:8080/admin.html


Addendum:
---------

MySQL Table Creation Scripts (You dont need to create below tables, since they are auto generated in previous micro services start up step):
----------------------------

CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `apartment` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `pin` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

CREATE TABLE `customer_order` (
  `id` bigint(20) NOT NULL,
  `last_event_sequence_number` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `order_date` datetime DEFAULT NULL,
  `order_status` varchar(255) DEFAULT NULL,
  `total` double DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `inventory` (
  `id` bigint(20) NOT NULL,
  `last_event_sequence_number` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `sku` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `line_item` (
  `id` bigint(20) NOT NULL,
  `last_event_sequence_number` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `inventory_id` bigint(20) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `product` varchar(255) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `order_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_1d3yqp9d56i39oajwjf0f8hkl` (`order_id`),
  CONSTRAINT `FK_1d3yqp9d56i39oajwjf0f8hkl` FOREIGN KEY (`order_id`) REFERENCES `customer_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `user_credential` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` tinyint(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_14ncv1m0gqncrbiagrs4uaqo8` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `billing_address_id` bigint(20) DEFAULT NULL,
  `shipping_address_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hixwjgx0ynne0cq4tqvoawoda` (`user_id`),
  KEY `FK_qbwlk9dbmm8uuja3agc5jo1no` (`billing_address_id`),
  KEY `FK_1wm8vskwwsippi0h0p5ub8vp4` (`shipping_address_id`),
  CONSTRAINT `FK_1wm8vskwwsippi0h0p5ub8vp4` FOREIGN KEY (`shipping_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FK_qbwlk9dbmm8uuja3agc5jo1no` FOREIGN KEY (`billing_address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

CREATE TABLE `user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_apcc8lxk2xnug8377fatvbn04` (`user_id`),
  CONSTRAINT `FK_apcc8lxk2xnug8377fatvbn04` FOREIGN KEY (`user_id`) REFERENCES `user_credential` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

