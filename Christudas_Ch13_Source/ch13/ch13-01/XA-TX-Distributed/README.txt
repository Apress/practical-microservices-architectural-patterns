ch13\ch13-01\XA-TX-Distributed

Active MQ Setup
---------------

apache-activemq-5.13.3\conf\activemq.xml

<beans>
    <broker>
        <destinations> 
            <queue physicalName="notification.queue" />
        </destinations>
    </broker>
</beans>

cd D:\Applns\apache\ActiveMQ\apache-activemq-5.13.3\bin
D:\Applns\apache\ActiveMQ\apache-activemq-5.13.3\bin>activemq start


MySQL Setup
-----------

D:\Applns\MySQL\mysql-5.7.14-winx64\bin>mysqld --console
D:\Applns\MySQL\mysql-5.7.14-winx64\bin>mysql -u root -p
mysql> use ecom01;
mysql> drop table quote;
mysql> create table quote (id BIGINT PRIMARY KEY AUTO_INCREMENT, symbol VARCHAR(4), sellerid BIGINT, buyerid BIGINT, amount FLOAT, status VARCHAR(9), test INTEGER, delay INTEGER, createdat DATETIME, updatedat DATETIME);

Derby Setup
-----------

D:\Applns\apache\Derby\db-derby-10.14.1.0-bin\bin>startNetworkServer
D:\Applns\apache\Derby\derbydb>ij
ij> connect 'jdbc:derby://localhost:1527/D:/Applns/apache/Derby/derbydb/sampledb;create=false';
ij> drop table stockuser;
ij> create table stockuser (id bigint not null, amountbought double, amountsold double, createdat timestamp not null, lastquoteat timestamp, name varchar(10) not null, updatedat timestamp not null, primary key (id));
ij> CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;

1. Microservice 1: Quote Processing microservice
------------------------------------------------
Change
ch13\ch13-01\XA-TX-Distributed\Broker-MySQL-ActiveMQ\src\main\resources\spring-sender-mysql.xml

cd D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-MySQL-ActiveMQ
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-MySQL-ActiveMQ>make
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-MySQL-ActiveMQ>mvn -Dmaven.test.skip=true clean install
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-MySQL-ActiveMQ>run
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-MySQL-ActiveMQ>mvn -Dtest=BrokerServiceTest#testSubmitQuote test

2. Microservice 2: Broker Web microservice
------------------------------------------
Change
ch13\ch13-01\XA-TX-Distributed\Broker-Web\src\main\resources\application.properties

cd D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-Web
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-Web>make
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-Web>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-Web>run
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Broker-Web>java -jar -Dserver.port=8080 .\target\quotes-web-1.0.0.jar

Chrome
http://localhost:8080

3. Microservice 3: Quote Settlement microservice
------------------------------------------------
Change
ch13\ch13-01\XA-TX-Distributed\Settlement-ActiveMQ-Derby\src\main\resources\spring-listener-derby.xml

cd D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-ActiveMQ-Derby
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-ActiveMQ-Derby>make
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-ActiveMQ-Derby>mvn -Dmaven.test.skip=true clean install
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-ActiveMQ-Derby>run
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-ActiveMQ-Derby>mvn -Dtest=SettlementListenerServiceTest#testSettleQuote test

4. Microservice 4: Settlement Web microservice
----------------------------------------------
Change
ch13\ch13-01\XA-TX-Distributed\Settlement-Web\src\main\resources\application.properties

cd D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-Web
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-Web>make
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-Web>mvn -Dmaven.test.skip=true clean package
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-Web>run
D:\binil\gold\pack03\ch13\ch13-01\XA-TX-Distributed\Settlement-Web>java -jar -Dserver.port=8081 .\target\user-web-1.0.0.jar

Chrome
http://localhost:8081


Architecture for Sample:
=======================

       µ Service 1                 
 _____________________          µ Service 2
|                     |  1       _________
| QuotesProcessorTask |___      |         |
|_____________________|   \     | Broker  |                                      
			   |    |   Web   |
			   |    |_________|
  ________________________/          |
 /                                   |
|			          ___|___
|                               /         \
|                              |\_________/|
|                              |           |<----
|                              |  MySQL DB |     |
|                               \_________/      |       |        Legend
|                                               3|     __|__      ------
|                                                |    /  |  \     1. processNewQuote  
|                          (XA)________________  |   |\_____/|    2. confirmQuote
|                          |                   | |   |       |    3. quote.setStatus(Quote.CONFIRMED)
|    (XA)____________   2  | AuctionService    |_|   |   A   |    4. sendOrderMessage
|    |		     |---->|___________________|     |	 C   |    5. jmsTemplate.send(quote)
 \-->|               |                               |   T   |    6. onMessage(Quote)
     | BrokerService |     (XA)________________      |   I   |    7. settleQuote
     |_______________|---->|                   |     |   V   |    8. reconcile Quotes processing
                        4  | StockOrderService |---->|   E   |    
                           |___________________|  5  |       |
                                                     |       |      (XA)_________________________
                                                     |   M   |  6  |                             |   7
                                                     |   Q   |---->| SettlementListenerService   |-----
 Q U O T E   P R O C E S S I N G   µ S E R V I C E   |       |     |_____________________________|     |
 <------------------------------------------------   |       |                µ Service 3              |                  
                                                     |   Q   |                                         |
                                                     |   U   |                                         |
                                                     |   E   |                                         |
                                                     |   U   |      (XA)_________________________      |
                                                     |   E   |     |                             |     |
                                                      \_____/   ---| QuotesReconcileService      |<----
                                                         |     |   |_____________________________|
                                                         |     |                   
                                                               |8          
                                                               |               ________
                                                               |             /         \      ___________                                           
                                                               |            |\_________/|    |           |
                                                                ----------->|           |____| Settlement|
                                                                            |  Derby DB |    |    Web    |
                                                                             \_________/     |___________|
                                                                                              µ Service 4



                                                               Q U O T E   S E T T L E   µ S E R V I C E
                                                               -----------------------------------------

Test Scenarios:
==============
 ____________________________________________________________  __________________________________________________________________
|           |          |           |          |              ||    Settlement   | Record User|                                   |                  
| Test Case | Broker   | Auction   | Delay    | Stock Order  ||     Listener    | Transaction| Comments                          |            
|           |          |(DB Update)|  (s)     |(Send Message)||(Receive Message)| (DB Update)|                                   |
 ------------------------------------------------------------  ------------------------------------------------------------------
| 1         | No Error | No Error  | 2        | No Error     || No Error        | No Error   | Happy Flow - All Good             | 
 ------------------------------------------------------------  ------------------------------------------------------------------
| 2         |    Error | No Error  | 2        | No Error     ||                 |            | New Quote gets Processed          |
|           | No Error | No Error  |          | No Error     || No Error        | No Error   | only in the second run            |
 ------------------------------------------------------------  ------------------------------------------------------------------
| 3         | No Error | No Error  | 2        |    Error     ||                 |            | Message Lost, Quote               |
|           |          |           |          |              ||                 |            | Never Settled                     |
 ------------------------------------------------------------  ------------------------------------------------------------------
| 4         | No Error |    Error  | 2        | No Error     || No Error        | No Error   | Duplicate Message send, 2nd       |
|           | No Error | No Error  | 2        | No Error     || No Error        | No Error   | time only Quote marked processed; |
|           |          |           |          |              ||                 |            | Hence, Quote Settled twice        |
 ------------------------------------------------------------  -------------------------------------------------------------------
| 5         | No Error | No Error  | 2        | No Error     ||    Error        | No Error   | Duplicate settlement caused by    |
|           |          |           |          |              || No Error        | No Error   | message consumed more than once.  |
|           |          |           |          |              ||                 |            | Quote Settled twice               |
 ------------------------------------------------------------  ------------------------------------------------------------------
|           |          |           |          |              ||                 |            | Quote Processed, however          |
| 6         | No Error | No Error  | 2        | No Error     || No Error        |    Error   | Quote Never Settled               |
 ------------------------------------------------------------  ------------------------------------------------------------------
| 7         | No Error | No Error  | 2        | No Error     ||    Error        |    Error   | Message ReDelivery comes to rescue|
|           |          |           |          |              || No Error        | No Error   | - Quote Settled second chance     |
 ------------------------------------------------------------  ------------------------------------------------------------------
| 8         | No Error | No Error  | 1st: 120 | No Error     || No Error        | No Error   | Message Received out of order!    |
|           | No Error | No Error  | 2nd: 2   | No Error     || No Error        | No Error   |                                   |
 ------------------------------------------------------------  ------------------------------------------------------------------

User Data
=========
Use Postman
http://localhost:8081/api/users
POST BODY Raw JSON

{ "id" : 11, "name" : "Sam", "amountSold" : 1000.0, "amountBought" : 1000.0 }
{ "id" : 21, "name" : "Joe", "amountSold" : 5000.0, "amountBought" : 5000.0 }

Test Data:
=========
Use Postman
http://localhost:8080/api/quotes
POST BODY Raw JSON

Test Case 1
{ "symbol" : "AAPL", "sellerId" : 11, "buyerId" : 21, "amount" : 100, "test" : 1, "delay" : 2 }

Test Case 2
{ "symbol" : "AMZN", "sellerId" : 11, "buyerId" : 21, "amount" : 200, "test" : 2, "delay" : 2 }

Test Case 3
{ "symbol" : "GOOG", "sellerId" : 11, "buyerId" : 21, "amount" : 300, "test" : 3, "delay" : 2 }

Test Case 4
{ "symbol" : "NFLX", "sellerId" : 11, "buyerId" : 21, "amount" : 400, "test" : 4, "delay" : 2 }

Test Case 5
{ "symbol" : "TSLA", "sellerId" : 11, "buyerId" : 21, "amount" : 500, "test" : 5, "delay" : 2 }

Test Case 6
{ "symbol" : "MSFT", "sellerId" : 11, "buyerId" : 21, "amount" : 600, "test" : 6, "delay" : 2 }

Test Case 7
{ "symbol" : "ORCL", "sellerId" : 11, "buyerId" : 21, "amount" : 700, "test" : 7, "delay" : 2 }

Test Case 8
{ "symbol" : "QCOM", "sellerId" : 11, "buyerId" : 21, "amount" : 800, "test" : 8, "delay" : 90}
{ "symbol" : "GILD", "sellerId" : 11, "buyerId" : 21, "amount" : 900, "test" : 8, "delay" : 2 }

delete from quote;
select * from quote;

delete from stockuser;
select * from stockuser;


