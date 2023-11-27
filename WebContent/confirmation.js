function showCartItems(resultData) {
    let totalPrice = 0;
    for (const movieId in resultData) {
        const mtitle = resultData[movieId]["movieTitle"];
        const mprice = resultData[movieId]["moviePrice"];
        const mquantity = resultData[movieId]["quantity"];
        let rowHTML =
            '<tr>' +
            '<td>' + mtitle + '</td>' +
            '<td>' + mprice + '</td>' +
            '<td>' + mquantity + '</td>' +
            '<td>' + mprice * mquantity + '</td>' +
            '</tr>';
        let $itemRow = $(rowHTML);
        $("#cart_table tbody").append($itemRow);

        totalPrice += mprice * mquantity;
    }
    $("#total-price").text(totalPrice.toFixed(2));
    $("#sales-id").text(generateUUID());
}

function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0,
            v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "POST", // Setting request method
    url: "api/shopping-cart?action=confirm",
    success: (resultData) => showCartItems(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")

    // TODO: if you want to check past query results first, you can do it here
    let cached = localStorage.getItem(query);
    if (cached) {
        console.log("using cached results");
        handleLookupAjaxSuccess(cached, query, doneCallback);
    } else {
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/autocomplete?mtitle=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                console.log("sending AJAX request to backend Java Servlet")
                localStorage.setItem(query, JSON.stringify(data));
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        });
    }


}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    var jsonData = typeof data === "object" ? data : JSON.parse(data);
    console.log(jsonData);
    doneCallback( { suggestions: jsonData } );
}


function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    // console.log("you select " + suggestion["movieTitle"] + " with ID " + suggestion["data"]["movieId"])
    window.location.href = "single-movie.html?id=" + suggestion["data"]["movieId"];
}


$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3
});