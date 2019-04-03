Zuul

Start Eureka Registry Server
----------------------------

cd ch08\ch08-06\Eureka
D:\binil\gold\pack03\ch08\ch08-06\Eureka>make
D:\binil\gold\pack03\ch08\ch08-06\Eureka>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-06\Eureka>run
D:\binil\gold\pack03\ch08\ch08-06\Eureka>java -jar -Dserver.port=8761 -Dspring.application.name=eureka-registry -Deureka.client.registerWithEureka=false -Deureka.client.fetchRegistry=true -Deureka.client.server.waitTimeInMsWhenSyncEmpty=0 -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/ -Deureka.server.enableSelfPreservation=false .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Zuul API Gateway
----------------------

D:\binil\gold\pack03\ch08\ch08-06\ProductApiZuul>make
D:\binil\gold\pack03\ch08\ch08-06\ProductApiZuul>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-06\ProductApiZuul>run
D:\binil\gold\pack03\ch08\ch08-06\ProductApiZuul>java -Dserver.port=8082 -Dspring.application.name=product-service-api -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Server 1
----------------------

cd ch08\ch08-06\ProductServer
D:\binil\gold\pack03\ch08\ch08-06\ProductServer>make
D:\binil\gold\pack03\ch08\ch08-06\ProductServer>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-06\ProductServer>run1
D:\binil\gold\pack03\ch08\ch08-06\ProductServer>java -Dserver.port=8080 -Dspring.application.name=product-service -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Server 2
----------------------

cd ch08\ch08-06\ProductServer
D:\binil\gold\pack03\ch08\ch08-06\ProductServer>run2
D:\binil\gold\pack03\ch08\ch08-06\ProductServer>java -Dserver.port=8081 -Dspring.application.name=product-service -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

cd ch08\ch08-06\ProductWeb
D:\binil\gold\pack03\ch08\ch08-06\ProductWeb>make
D:\binil\gold\pack03\ch08\ch08-06\ProductWeb>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-06\ProductWeb>run
D:\binil\gold\pack03\ch08\ch08-06\ProductWeb>java -Dserver.port=8084 -Dspring.application.name=product-web -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-06\ProductWeb\src\main\resources\product.html

