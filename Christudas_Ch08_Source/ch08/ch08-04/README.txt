Ribbon

Start MongoDB Server
--------------------
cd D:\Applns\MongoDB\Server\3.2.6\bin
D:\Applns\MongoDB\Server\3.2.6\bin>mongod --dbpath D:\Applns\MongoDB\Server\3.2.6\data

Start Product Server 1
----------------------
cd ch08\ch08-04\ProductServer
D:\binil\gold\pack03\ch08\ch08-04\ProductServer>make
D:\binil\gold\pack03\ch08\ch08-04\ProductServer>mvn -Dmaven.test.skip=true clean package

cd ch08\ch08-04\ProductServer
D:\binil\gold\pack03\ch08\ch08-04\ProductServer>run1
D:\binil\gold\pack03\ch08\ch08-04\ProductServer>java -jar -Dserver.port=8080 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Server 2
----------------------

cd ch08\ch08-04\ProductServer
D:\binil\gold\pack03\ch08\ch08-04\ProductServer>run2
D:\binil\gold\pack03\ch08\ch08-04\ProductServer>java -jar -Dserver.port=8081 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

cd ch08\ch08-03\ProductWeb
D:\binil\gold\pack03\ch08\ch08-04\ProductWeb>make
D:\binil\gold\pack03\ch08\ch08-04\ProductWeb>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch08\ch08-04\ProductWeb>run
D:\binil\gold\pack03\ch08\ch08-04\ProductWeb>java -jar -Dserver.port=8082 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-04\ProductWeb\src\main\resources\product.html

To demonstrate Ribbon, Repeated browser refresh will load balance the hits from Product Web micro service to Product Server micro server instances