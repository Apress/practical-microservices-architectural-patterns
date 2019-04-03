Hysterix Fallback
=================

Start MongoDB Server
--------------------
cd D:\Applns\MongoDB\Server\3.2.6\bin
D:\Applns\MongoDB\Server\3.2.6\bin>mongod --dbpath D:\Applns\MongoDB\Server\3.2.6\data

Start Product Server
--------------------
cd ch08\ch08-02\ProductServer
D:\binil\gold\pack03\ch08\ch08-02\ProductServer>make
D:\binil\gold\pack03\ch08\ch08-02\ProductServer>mvn -Dmaven.test.skip=true clean package

cd ch08\ch08-02\ProductServer
D:\binil\gold\pack03\ch08\ch08-02\ProductServer>run
D:\binil\gold\pack03\ch08\ch08-02\ProductServer>java -jar -Dserver.port=8080 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Alternate Server
------------------------------
cd ch08\ch08-02\ProductServerAlternate
D:\binil\gold\pack03\ch08\ch08-02\ProductServerAlternate>make
D:\binil\gold\pack03\ch08\ch08-02\ProductServerAlternate>mvn -Dmaven.test.skip=true clean package

cd ch08\ch08-02\ProductServerAlternate
D:\binil\gold\pack03\ch08\ch08-02\ProductServerAlternate>run
D:\binil\gold\pack03\ch08\ch08-02\ProductServerAlternate>java -jar -Dserver.port=8079 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Product Web
-----------------

cd ch08\ch08-02\ProductWeb
D:\binil\gold\pack03\ch08\ch08-02\ProductWeb>make
D:\binil\gold\pack03\ch08\ch08-02\ProductWeb>mvn -Dmaven.test.skip=true clean package

D:\binil\gold\pack03\ch08\ch08-02\ProductWeb>run
D:\binil\gold\pack03\ch08\ch08-02\ProductWeb>java -jar -Dserver.port=8081 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Test the Client
---------------
Open below HTML File in Chrome
ch08\ch08-02\ProductWeb\src\main\resources\product.html

To demonstrate Hysterix fallback, simply bring down the Product Server and refresh the browser again.