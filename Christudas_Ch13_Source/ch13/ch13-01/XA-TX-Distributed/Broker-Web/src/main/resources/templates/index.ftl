<html>
<head>
    <style type="text/css">
        table#quotesstable td,
        table#quotesstable th {
            padding: 0px 10px;
        }
    </style>
    <script type="application/javascript" src="/webjars/jquery/2.1.4/jquery.min.js">
    </script>
    <link rel="stylesheet" href="/webjars/bootstrap-css-only/3.3.4/css/bootstrap.min.css">
    <title>Exploring Quotes</title>
</head>

<body>
<!--
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">Explore Quotes with Boot Framework</a>
        </div>
        <div>
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#">About</a></li>
            </ul>
        </div>
    </div>
</nav>
-->

<div class="container" style="margin-top: 10px">
   
     <div class="row">
        <div class="col-md-12">
            <div>
                <table id="quotesstable" border="1">
                    <caption>Quotes Table</caption>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Symbol</th>
                        <th>SellerId</th>
                        <th>BuyerId</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Test</th>
                        <th>Delay</th>
                        <th>Created @</th>
                        <th>Updated @</th>
                    </tr>
                    </thead>
                    <tbody>
                    <!-- updated by js -->
                    </tbody>
                </table>
            </div>

        </div>
    </div>
</div>

</body>
<script type="text/javascript" src="/script.js"></script>
<script type="text/javascript" src="/date.js"></script>

</html>