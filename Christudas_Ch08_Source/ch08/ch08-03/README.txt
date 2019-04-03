Hystrix Dashboard

Start MongoDB Server
--------------------
cd D:\Applns\MongoDB\Server\3.2.6\bin
D:\Applns\MongoDB\Server\3.2.6\bin>mongod --dbpath D:\Applns\MongoDB\Server\3.2.6\data

Start Product Server
--------------------
cd ch08\ch08-03\ProductServer
D:\binil\gold\pack03\ch08\ch08-03\ProductServer>make
D:\binil\gold\pack03\ch08\ch08-03\ProductServer>mvn -Dmaven.test.skip=true clean package

D:\binil\gold\pack03\ch08\ch08-03\ProductServer>run
D:\binil\gold\pack03\ch08\ch08-03\ProductServer>java -jar -Dserver.port=8080 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Alternate Server
------------------------------
cd ch08\ch08-03\ProductServerAlternate
D:\binil\gold\pack03\ch08\ch08-03\ProductServerAlternate>make
D:\binil\gold\pack03\ch08\ch08-03\ProductServerAlternate>mvn -Dmaven.test.skip=true clean package

D:\binil\gold\pack03\ch08\ch08-03\ProductServerAlternate>run
D:\binil\gold\pack03\ch08\ch08-03\ProductServerAlternate>java -jar -Dserver.port=8079 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

cd ch08\ch08-03\ProductWeb
D:\binil\gold\pack03\ch08\ch08-03\ProductWeb>make
D:\binil\gold\pack03\ch08\ch08-03\ProductWeb>mvn -Dmaven.test.skip=true clean package

D:\binil\gold\pack03\ch08\ch08-03\ProductWeb>run
D:\binil\gold\pack03\ch08\ch08-03\ProductWeb>java -jar -Dserver.port=8081 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-03\ProductWeb\src\main\resources\product.html

To demonstrate Hysterix fallback, simply bring down the Product Server and refresh the browser again.