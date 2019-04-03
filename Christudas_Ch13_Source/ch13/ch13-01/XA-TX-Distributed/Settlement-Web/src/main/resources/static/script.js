$(function() {
    
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
