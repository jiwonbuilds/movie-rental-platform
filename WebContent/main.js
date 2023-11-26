function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating types of browsing");

    function createListItems(container, items, linkBase) {
        items.forEach((item, _) => {
            container.append('<li style="display:inline-block;list-style-type:disc;text-align:center;margin-right:10px;">' +
                '<a href="' + linkBase + item + '">' + item + '</a></li>');
        });
    }

    // Populate the list of genres
    let genres = jQuery("#genres");
    resultData.forEach((item, _) => {
        genres.append('<li style="display:inline-block;list-style-type:disc;text-align:center;margin-right:10px;">' +
            '<a href="' + 'movies.html?gid=' + item['genre_id'] + '">' + item['genre_name'] + '</a></li>')
    });

    console.log("success putting genre hyperlinks");

    let titles = jQuery("#titles");
    const alphabets = [...'ABCDEFGHIJKLMNOPQRSTUVWXYZ'];
    const numbers = [...'0123456789*'];
    createListItems(titles, alphabets, 'movies.html?titlePrefix=');
    titles.append('<br>');
    createListItems(titles, numbers, 'movies.html?titlePrefix=');
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/main", // Setting request url, which is mapped by BrowseServlet in SelectionServlet.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
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
