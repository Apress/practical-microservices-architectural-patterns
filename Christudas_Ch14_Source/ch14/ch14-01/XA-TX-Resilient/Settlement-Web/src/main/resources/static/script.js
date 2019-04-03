$(function() {
    
    // queries for the transactionstable
    setInterval(function() {
  
            $.ajax({
            url: "http://localhost:8081/api/trade",
        	//url: "/view",
            method: "GET",
            success: function(trades) {
                var html = "";
                trades.forEach(function(trade){
                    //for (var key in account) {
                        html += "<tr><td>" + trade.id + "</td><td>" + trade.quoteId +  "</td><td>"+ trade.stockSymbol +  "</td><td>" + trade.sellerId +  "</td><td>" + trade.buyerId + "</td><td>" + trade.amount + "</td><td>" + trade.status + "</td><td>" + convertToDate(trade.quoteCreated) + "</td><td>" + trade.test + "</td></tr>"
                    //}
                });
                $("table#transactionstable tbody").html(html);
            },
            error: function(a) {
                console.log(a);
            }
        });

    }, 1000);

    // queries for the userstable
    setInterval(function() {
  
            $.ajax({
            url: "http://localhost:8081/api/users",
        	//url: "/view",
            method: "GET",
            success: function(users) {
                var html = "";
                users.forEach(function(user){
                    //for (var key in account) {
                        html += "<tr><td>" + user.id + "</td><td>" + user.name +  "</td><td>"+ user.amountSold +  "</td><td>" + user.amountBought +  "</td><td>" + convertToDate(user.lastQuoteAt) + "</td><td>" + convertToDate(user.createdAt) + "</td><td>" + convertToDate(user.updatedAt) + "</td></tr>"
                    //}
                });
                $("table#userstable tbody").html(html);
            },
            error: function(a) {
                console.log(a);
            }
        });

    }, 1000);

});


function convertToDate(dateInMilli) {
    return new Date(dateInMilli).toLocaleString();              
}
