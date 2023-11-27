let payment_form = $("#payment_form");
function handlePaymentResult(resultData) {
    let paymentResult = JSON.parse(resultData);
    if (paymentResult["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        $("#checkout_error_message").text(paymentResult["message"]);
    }
}

function confirmPayment(submitEvent) {
    submitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

payment_form.submit(confirmPayment);


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