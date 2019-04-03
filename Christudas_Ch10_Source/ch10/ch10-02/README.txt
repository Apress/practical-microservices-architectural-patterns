Protocol Buffer Sample

Start Product Server
--------------------

cd ch10\ch10-02\ProductServer
D:\binil\gold\pack03\ch10\ch10-02\ProductServer>make
D:\binil\gold\pack03\ch10\ch10-02\ProductServer>rem D:\Applns\Google\ProtocolBuffer\protoc-3.4.0-win32\bin\protoc --proto_path .\src\main\resources --java_out .\src\main\java .\src\main\resources\product.proto
D:\binil\gold\pack03\ch10\ch10-02\ProductServer>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch10\ch10-02\ProductServer>run
D:\binil\gold\pack03\ch10\ch10-02\ProductServer>java -jar -Dserver.port=8080 .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar

Start Apache TCPMon Proxy
-------------------------

cd D:\Applns\apache\TCPMon\tcpmon-1.0-bin\build
D:\Applns\apache\TCPMon\tcpmon-1.0-bin\build>tcpmon 8081 127.0.0.1 8080


Start Product Web
-----------------

cd ch10\ch10-02\ProductWeb
D:\binil\gold\pack03\ch10\ch10-02\ProductWeb>make
D:\binil\gold\pack03\ch10\ch10-02\ProductWeb>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch10\ch10-02\ProductWeb>run
D:\binil\gold\pack03\ch10\ch10-02\ProductWeb>java -Dserver.port=8082 -jar .\target\Ecom-Product-Microservice-0.0.1-SNAPSHOT.jar


Test the Client
---------------
Open below URL in Chrome
http://localhost:8082/
