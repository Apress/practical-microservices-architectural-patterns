Eureka

Start MongoDB Server
--------------------
cd D:\Applns\MongoDB\Server\3.2.6\bin
D:\Applns\MongoDB\Server\3.2.6\bin>mongod --dbpath D:\Applns\MongoDB\Server\3.2.6\data

Start Eureka Registry Server 1
------------------------------
cd ch08\ch08-05\Eureka
D:\binil\gold\pack03\ch08\ch08-05\Eureka>make
D:\binil\gold\pack03\ch08\ch08-05\Eureka>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-05\Eureka>run1
D:\binil\gold\pack03\ch08\ch08-05\Eureka>java -jar -Dserver.port=8761 -Dspring.application.name=eureka-registry1 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Eureka Registry Server 2
------------------------------
D:\binil\gold\pack03\ch08\ch08-05\Eureka>run2
D:\binil\gold\pack03\ch08\ch08-05\Eureka>java -jar -Dserver.port=8762 -Dspring.application.name=eureka-registry2 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Server 1
----------------------

cd ch08\ch08-05\ProductServer
D:\binil\gold\pack03\ch08\ch08-05\ProductServer>make
D:\binil\gold\pack03\ch08\ch08-05\ProductServer>mvn -Dmaven.test.skip=true clean package

D:\binil\gold\pack03\ch08\ch08-05\ProductServer>run1
D:\binil\gold\pack03\ch08\ch08-05\ProductServer>java -Dserver.port=8080 -Dspring.application.name=product-service -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Server 2
----------------------

cd ch08\ch08-05\ProductServer
D:\binil\gold\pack03\ch08\ch08-05\ProductServer>run2
D:\binil\gold\pack03\ch08\ch08-05\ProductServer>java -Dserver.port=8081 -Dspring.application.name=product-service -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

cd ch08\ch08-05\ProductWeb
D:\binil\gold\pack03\ch08\ch08-05\ProductWeb>make
D:\binil\gold\pack03\ch08\ch08-05\ProductWeb>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-05\ProductWeb>run
D:\binil\gold\pack03\ch08\ch08-05\ProductWeb>java -Dserver.port=8082 -Dspring.application.name=product-web -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-05\ProductWeb\src\main\resources\product.html

To demonstrate Eureka, Repeated browser refresh will load balance the hits from Product Web micro service to Product Server micro server instances. If you bring down one of the Product Server micro services and refresh the browser again, you will see the hit will always goes to the other Product Server instance running.