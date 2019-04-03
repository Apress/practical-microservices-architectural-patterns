Config Server

Start Config Server
-------------------

cd ch08\ch08-07\ConfigServer
D:\binil\gold\pack03\ch08\ch08-07\ConfigServer>make
D:\binil\gold\pack03\ch08\ch08-07\ConfigServer>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-07\ConfigServer>run
D:\binil\gold\pack03\ch08\ch08-07\ConfigServer>java -Dserver.port=8888 -Dspring.application.name=productservice -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Inspect Config Server
---------------------
http://localhost:8888/productservice/default

Start Product Server
--------------------

cd ch08\ch08-07\ProductServer
D:\binil\gold\pack03\ch08\ch08-07\ProductServer>make
D:\binil\gold\pack03\ch08\ch08-07\ProductServer>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-07\ProductServer>run
D:\binil\gold\pack03\ch08\ch08-07\ProductServer>java -Dserver.port=8081 -Dspring.application.name=productservice -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

D:\binil\gold\pack03\ch08\ch08-07\ProductWeb>make
D:\binil\gold\pack03\ch08\ch08-07\ProductWeb>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-07\ProductWeb>run
D:\binil\gold\pack03\ch08\ch08-07\ProductWeb>java -Dserver.port=8080 -Dspring.application.name=productweb -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-07\ProductWeb\src\main\resources\product.html

Change value in:
---------------
ch08\ch08-07\ConfigServer\config-repo\productservice.yml

Inspect Config Server
---------------------
http://localhost:8888/productservice/default

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-07\ProductWeb\src\main\resources\product.html

Refresh Product Server
----------------------
curl -d {} localhost:8081/refresh

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-07\ProductWeb\src\main\resources\product.html
