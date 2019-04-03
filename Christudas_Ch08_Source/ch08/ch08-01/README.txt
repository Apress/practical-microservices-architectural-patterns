FeignClient
===========

Start MongoDB Server
--------------------
cd D:\Applns\MongoDB\Server\3.2.6\bin
D:\Applns\MongoDB\Server\3.2.6\bin>D:\Applns\MongoDB\Server\3.2.6\bin>mongod --dbpath D:\Applns\MongoDB\Server\3.2.6\data

Start Product Server
--------------------
cd ch08\ch08-01\ProductServer
D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductServer>make

D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductServer>mvn -Dmaven.test.skip=true clean package

D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductServer>run

D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductServer>java -jar -Dserver.port=8080 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

cd ch08\ch08-01\ProductWeb
D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductWeb>make

D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductWeb>mvn -Dmaven.test.skip=true clean package

D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductWeb>run

D:\binil\gold\shuffle\pack02\ch08\ch08-01\ProductWeb>java -jar -Dserver.port=8081 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-01\ProductWeb\src\main\resources\product.html