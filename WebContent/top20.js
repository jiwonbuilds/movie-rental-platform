/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    console.log("handleStarResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        // movie title
        rowHTML += "<td>" +
            '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">' + resultData[i]["movie_title"] + '</a>' +
            "</td>";
        // movie year
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        // movie director
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        // movie genre (upto 3)
        rowHTML += "<td>" +
            resultData[i]["movie_genres"].slice(0, 3).join(", ") +
            "</td>";
        // movie star (upto 3)
        rowHTML += "<td>";
        for (let j = 0; j < Math.min(3, resultData[i]["movie_stars"].length); j++) {
            if (j > 0) {
                rowHTML += ", ";
            }
            rowHTML += '<a href="single-star.html?id=' + resultData[i]["movie_stars"][j]["star_id"] + '">' + resultData[i]["movie_stars"][j]["star_name"] + '</a>';
        }
        rowHTML += "</td>";
        // movie rating
        rowHTML += "<td>" + "&star;&nbsp;" + resultData[i]["movie_rating"] + "</td>";
        rowHTML += "</tr>"

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/top20", // Setting request url, which is mapped by MoviesServlet in Top20Servlet.java
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