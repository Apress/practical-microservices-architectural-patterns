Ax2-Commands-Events-Same-JVM

MySQL Setup(application.properties)
-----------------
server.port=8081
spring.datasource.url=jdbc:mysql://localhost/ecom01
spring.datasource.username=root
spring.datasource.password=rootpassword

# Bring Up MySQL Server
cd D:\Applns\MySQL\mysql-8.0.14-winx64\bin
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysqld --console

# Open MySQL prompt
D:\Applns\MySQL\mysql-8.0.14-winx64\bin>mysql -u root -p

mysql> use ecom01;
Database changed
mysql>

show tables;

#---------- Clean tables for the sample ------------

mysql> 
drop table ecom_order;
drop table ecom_product;
drop table ecom_order_view;
drop table ecom_product_view;

#---------- Explicitly create tables - Optional ------------

mysql> 
create table ecom_order (id integer not null, last_event_sequence_number bigint, version bigint, number integer, order_status varchar(255), price double precision, product_id integer, primary key (id)) ENGINE=InnoDB;
create table ecom_product (id integer not null, last_event_sequence_number bigint, version bigint, description varchar(255), price double precision, stock integer, primary key (id)) ENGINE=InnoDB;
alter table ecom_order add constraint FK_f3rnd79i90twafllfhpo1sihi foreign key (product_id) references ecom_product (id);

#---------- Explicitly create tables - Mandatory ------------

create table ecom_product_view(id INT , price DOUBLE, stock INT ,description VARCHAR(255));
create table ecom_order_view(id INT , price DOUBLE, number INT ,description VARCHAR(225),status VARCHAR(50));

#--------- Build & Run Micro Services --------------

cd D:\binil\gold\pack03\ch12\ch12-01\Ax2-Commands-Events-Same-JVM
D:\binil\gold\pack03\ch12\ch12-01\Ax2-Commands-Events-Same-JVM>make
D:\binil\gold\pack03\ch12\ch12-01\Ax2-Commands-Events-Same-JVM>mvn clean install

D:\binil\gold\pack03\ch12\ch12-01\Ax2-Commands-Events-Same-JVM>run
D:\binil\gold\pack03\ch12\ch12-01\Ax2-Commands-Events-Same-JVM>mvn spring-boot:run

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




