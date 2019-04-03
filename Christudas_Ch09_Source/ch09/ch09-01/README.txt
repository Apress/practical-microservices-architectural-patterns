High Availability Sample

Start Eureka Registry Server 1
------------------------------

cd ch09\ch09-01\Eureka
D:\binil\gold\pack03\ch09\ch09-01\Eureka>make
D:\binil\gold\pack03\ch09\ch09-01\Eureka>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch09\ch09-01\Eureka>run1
D:\binil\gold\pack03\ch09\ch09-01\Eureka>java -jar -Dserver.port=8761 -Dspring.application.name=eureka-registry1 -Deureka.client.registerWithEureka=false -Deureka.client.fetchRegistry=true -Deureka.client.server.waitTimeInMsWhenSyncEmpty=0-Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -Deureka.server.enableSelfPreservation=false .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Eureka Registry Server 2
------------------------------

cd ch09\ch09-01\Eureka
D:\binil\gold\pack03\ch09\ch09-01\Eureka>run2
D:\binil\gold\pack03\ch09\ch09-01\Eureka>java -jar -Dserver.port=8762 -Dspring.application.name=eureka-registry2 -Deureka.client.registerWithEureka=false -Deureka.client.fetchRegistry=true -Deureka.client.server.waitTimeInMsWhenSyncEmpty=0-Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -Deureka.server.enableSelfPreservation=false .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar


Start Zuul API Gateway 1
------------------------

cd ch09\ch09-01\ProductApiZuul
D:\binil\gold\pack03\ch09\ch09-01\ProductApiZuul>make
D:\binil\gold\pack03\ch09\ch09-01\ProductApiZuul>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch09\ch09-01\ProductApiZuul>run1
D:\binil\gold\pack03\ch09\ch09-01\ProductApiZuul>java -Dserver.port=8082 -Dspring.application.name=product-service-api -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Zuul API Gateway 2
------------------------

cd ch09\ch09-01\ProductApiZuul
D:\binil\gold\pack03\ch09\ch09-01\ProductApiZuul>run2
D:\binil\gold\pack03\ch09\ch09-01\ProductApiZuul>java -Dserver.port=8083 -Dspring.application.name=product-service-api -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Data Server 1
---------------------------

cd ch09\ch09-01\ProductDataServer
D:\binil\gold\pack03\ch09\ch09-01\ProductDataServer>make
D:\binil\gold\pack03\ch09\ch09-01\ProductDataServer>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch09\ch09-01\ProductDataServer>run1
D:\binil\gold\pack03\ch09\ch09-01\ProductDataServer>java -Dserver.port=8080 -Dspring.application.name=product-service -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Data Server 2
---------------------------

cd ch09\ch09-01\ProductDataServer
D:\binil\gold\pack03\ch09\ch09-01\ProductDataServer>run2
D:\binil\gold\pack03\ch09\ch09-01\ProductDataServer>java -Dserver.port=8081 -Dspring.application.name=product-service -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web Server 1
--------------------------

cd ch09\ch09-01\ProductWebServer
D:\binil\gold\pack03\ch09\ch09-01\ProductWebServer>make
D:\binil\gold\pack03\ch09\ch09-01\ProductWebServer>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch09\ch09-01\ProductWebServer>run1
D:\binil\gold\pack03\ch09\ch09-01\ProductWebServer>java -Dserver.port=8084 -Dspring.application.name=product-web -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web Server 2
--------------------------

cd ch09\ch09-01\ProductWebServer
D:\binil\gold\pack03\ch09\ch09-01\ProductWebServer>run2
D:\binil\gold\pack03\ch09\ch09-01\ProductWebServer>java -Dserver.port=8085 -Dspring.application.name=product-web -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web App Server 1
------------------------------

cd ch09\ch09-01\WebApp
D:\binil\gold\pack03\ch09\ch09-01\WebApp>make
D:\binil\gold\pack03\ch09\ch09-01\WebApp>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch09\ch09-01\WebApp>run1
D:\binil\gold\pack03\ch09\ch09-01\WebApp>java -Dserver.port=9001 -Dspring.application.name=product-home -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web App Server 2
------------------------------

cd ch09\ch09-01\WebApp
D:\binil\gold\pack03\ch09\ch09-01\WebApp>run2
D:\binil\gold\pack03\ch09\ch09-01\WebApp>java -Dserver.port=9002 -Dspring.application.name=product-home -Deureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/ -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Nginx Reverse Proxy
-------------------------

cd D:\Applns\nginx\nginx-1.13.5
D:\Applns\nginx\nginx-1.13.5>nginx

Test the Client
---------------
Open below URL in Chrome:
http://tiger:8090/
<<Replace "tiger" with your hostname>>