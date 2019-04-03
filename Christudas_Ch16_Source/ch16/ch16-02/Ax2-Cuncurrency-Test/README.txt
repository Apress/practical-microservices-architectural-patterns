ch16\ch16-02\Ax2-Cuncurrency-Test

MySQL Setup(application.properties)
===================================
ch16\ch16-02\Ax2-Cuncurrency-Test\src\main\resources\application.properties
-----------------
server.port=8080
spring.datasource.url=jdbc:mysql://localhost/ecom01
spring.datasource.username=root
spring.datasource.password=rootpassword

# Bring Up MySQL Server
D:\Applns\MySQL\mysql-5.7.14-winx64\bin>mysqld --console

# Open MySQL prompt
D:\Applns\MySQL\mysql-5.7.14-winx64\bin>mysql -u root -p

mysql> use ecom01;

show tables;
drop table user;
create table user (id bigint not null, last_event_sequence_number bigint, version bigint, age integer, user_name varchar(255), primary key (id))

1. Build the Microservice
-------------------------

cd D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test
D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test>make
D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test>mvn clean install

2. Microservice Instance 1
--------------------------

D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test>run1
D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test>java -Dserver.port=8080 -jar .\target\Axon-Concurrency-test-0.0.1-SNAPSHOT.jar

3. Microservice Instance 2
--------------------------

cd D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test
D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test>run2
D:\binil\gold\pack03\ch16\ch16-02\Ax2-Cuncurrency-Test>java -Dserver.port=8081 -jar .\target\Axon-Concurrency-test-0.0.1-SNAPSHOT.jar

User Data
=========
Use Postman
http://127.0.0.1:8080/user/create
POST BODY Raw JSON
{ "id" : 1, "userName" : "TestUser-01", "age" : 11 }

http://127.0.0.1:8080/user/update
PUT BODY Raw JSON
{ "id" : 1, "userName" : "TestUser-02", "age" : 12 }

http://127.0.0.1:8081/user/update
PUT BODY Raw JSON
{ "id" : 1, "userName" : "TestUser-03", "age" : 13 }

select * from user;
delete from user;
