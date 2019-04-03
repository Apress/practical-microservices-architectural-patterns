$(function() {
    
    // queries for the quotesstable
    setInterval(function() {
  
            $.ajax({
            url: "http://localhost:8080/api/quotes",
        	//url: "/view",
            method: "GET",
            success: function(quotes) {
                var html = "";
                quotes.forEach(function(quote){
                    //for (var key in account) {
                        html += "<tr><td>" + quote.id + "</td><td>" + quote.symbol +  "</td><td>"+ quote.sellerId +  "</td><td>" + quote.buyerId +  "</td><td>" + quote.amount +  "</td><td>" + quote.status +  "</td><td>" + quote.test + "</td><td>" + quote.delay + "</td><td>" + convertToDate(quote.createdAt) + "</td><td>" + convertToDate(quote.updatedAt) + "</td></tr>"
                    //}
                });
                $("table#quotesstable tbody").html(html);
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
