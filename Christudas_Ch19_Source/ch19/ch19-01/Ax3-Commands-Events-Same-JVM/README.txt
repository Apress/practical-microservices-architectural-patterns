ch19\ch19-01\Ax3-Commands-Events-Same-JVM

MySQL Setup(application.properties)
-----------------
server.port=8081
spring.datasource.url=jdbc:mysql://localhost/ecom01
spring.datasource.username=root
spring.datasource.password=rootpassword

# Bring Up MySQL Server
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysqld --console

# Open MySQL prompt
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysql -u root -p

use ecom01;

show tables;

#---------- Clean tables for the sample ------------

mysql> 
drop table association_value_entry;
drop table domain_event_entry;
drop table ecom_order;
drop table ecom_product;
drop table saga_entry;
drop table snapshot_event_entry;
drop table token_entry;

drop table ecom_order_view;
drop table ecom_product_view;

#---------- Explicitly create tables ------------

D:\binil\gold\shuffle\pack02\ch11\ch11-11\11_CommandeAndEvents_2017-June-09\Ecom>mvn clean install
D:\binil\gold\shuffle\pack02\ch11\ch11-11\11_CommandeAndEvents_2017-June-09\Ecom>mvn spring-boot:run

# Below tables, will be created when micro service startus up

association_value_entry
domain_event_entry     
ecom_order             
ecom_product           
saga_entry             
snapshot_event_entry   
token_entry 

#---------- Explicitly create View tables ------------
create table ecom_product_view(id INT , price DOUBLE, stock INT ,description VARCHAR(255));
create table ecom_order_view(id INT , price DOUBLE, number INT ,description VARCHAR(225),status VARCHAR(50));

#--------- Product initial data --------------

insert into ecom_product(id,description,price,stock) values(1,'Shirts',100,5);
insert into ecom_product(id,description,price,stock) values(2,'Pants',100,5);
insert into ecom_product(id,description,price,stock) values(3,'T-Shirt',100,5);
insert into ecom_product(id,description,price,stock) values(4,'Shoes',100,5);

insert into ecom_product_view(id,description,price,stock) values(1,'Shirts',100,5);
insert into ecom_product_view(id,description,price,stock) values(2,'Pants',100,5);
insert into ecom_product_view(id,description,price,stock) values(3,'T-Shirt',100,5);
insert into ecom_product_view(id,description,price,stock) values(4,'Shoes',100,5);
commit;

http://localhost:8080/
