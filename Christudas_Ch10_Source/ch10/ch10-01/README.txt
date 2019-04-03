Async Processing Sample

Start Product Server
--------------------

cd ch10\ch10-01\ProductServer
D:\binil\gold\pack03\ch10\ch10-01\ProductServer>make
D:\binil\gold\pack03\ch10\ch10-01\ProductServer>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch10\ch10-01\ProductServer>run
D:\binil\gold\pack03\ch10\ch10-01\ProductServer>java -jar -Dserver.port=8080 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

cd ch10\ch10-01\ProductWeb
D:\binil\gold\pack03\ch10\ch10-01\ProductWeb>make
D:\binil\gold\pack03\ch10\ch10-01\ProductWeb>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch10\ch10-01\ProductWeb>run
D:\binil\gold\pack03\ch10\ch10-01\ProductWeb>java -jar -Dserver.port=8081 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch10\ch10-01\ProductWeb\src\main\resources\product.html

Alternatively, use curl or postman to test GET request of the Product Web micro service pointing to the URL:
http://localhost:8081/productsweb/