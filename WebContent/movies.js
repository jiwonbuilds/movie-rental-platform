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
    // Find the empty table body by id "movies_listing"
    let moviesListingBodyElement = jQuery("#movies_listing");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let row = document.createElement("tr");
        row.innerHTML =
            '<td><a href="single-movie.html?id=' + resultData[i]["movieId"] + '">' + resultData[i]["movieTitle"] + '</a></td>' +
            '<td>' + resultData[i]["year"] + '</td>' +
            '<td>' + resultData[i]["director"] + '</td>';

        let genresHTML = "";
        for (let j = 0; j < resultData[i]["genres"].length; j++) {
            if (j > 0) {
                genresHTML += ", ";
            }
            genresHTML += '<a href="movies.html?gid=' + resultData[i]["genres"][j]["genreId"] + '">' + resultData[i]["genres"][j]["genreName"] + '</a>';
        }
        let genresCell = document.createElement("td");
        genresCell.innerHTML = genresHTML;
        row.appendChild(genresCell);

        let starsHTML = "";
        for (let j = 0; j < resultData[i]["stars"].length; j++) {
            if (j > 0) {
                starsHTML += ", ";
            }
            starsHTML += '<a href="single-star.html?id=' + resultData[i]["stars"][j]["starId"] + '">' + resultData[i]["stars"][j]["starName"] + '</a>';
        }
        let starsCell = document.createElement("td");
        starsCell.innerHTML = starsHTML;
        row.appendChild(starsCell);

        let ratingCell = document.createElement("td");
        if (resultData[i]["rating"] !== 0) {
            ratingCell.innerHTML = "&star;&nbsp;" + resultData[i]["rating"];
        } else {
            ratingCell.innerHTML = "N/A";
        }
        row.appendChild(ratingCell);

        let cartButton = document.createElement("button");
        cartButton.className = "hover-effect-button";
        cartButton.textContent = "Add";
        cartButton.style.cssText = "background: #5c6c7c; " +
            "color: #63e983; border: none; padding: 10px 20px; border-radius: 5px; " +
            "font-family: Gill Sans, serif; font-size: 14px;";
        cartButton.addEventListener("click", function() {
            $.ajax({
                type: "POST",
                url: "api/shopping-cart?action=add",
                data: { mid: resultData[i]["movieId"],
                    mtitle: resultData[i]["movieTitle"]},
                success: function() {
                    alert(resultData[i]["movieTitle"] + " added to your cart!");
                }
            });
        });

        let cartCell = document.createElement("td");
        cartCell.appendChild(cartButton);
        row.appendChild(cartCell);

        // Append the row created to the table body, which will refresh the page
        moviesListingBodyElement.append(row);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

function callAjax(url) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: url, // Setting request url, which is mapped by MoviesServlet in BrowseServlet.java
        success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}



let urlParams = new URLSearchParams(window.location.search);
let newQuery = true;

if (urlParams.has("gid") || urlParams.has("titlePrefix")
    || urlParams.has("mtitle") || urlParams.has("myear")
    || urlParams.has("mdirector") || urlParams.has("mstar")) {
    sessionStorage.setItem("sort", 1); sessionStorage.setItem("rowCount", 10); sessionStorage.setItem("page", 1);
    sessionStorage.removeItem("gid"); sessionStorage.removeItem("titlePrefix");
    sessionStorage.removeItem("mtitle"); sessionStorage.removeItem("myear");
    sessionStorage.removeItem("mdirector"); sessionStorage.removeItem("mstar");
} else {
    newQuery = false;
    if (urlParams.has("sort")) {
        sessionStorage.setItem("sort", urlParams.get("sort"))
    }
    if (urlParams.has("rowCount")) {
        sessionStorage.setItem("rowCount", urlParams.get("rowCount"))
    }
    if (urlParams.has("page")) {
        sessionStorage.setItem("page", urlParams.get("page"))
    }
}

if (newQuery && (urlParams.has("gid") || urlParams.has("titlePrefix"))) {
    sessionStorage.setItem("gid", (urlParams.get("gid") ?? ""))
    sessionStorage.setItem("titlePrefix", (urlParams.get("titlePrefix") ?? ""))
    sessionStorage.setItem("type", "browse")
    let newURL = "api/browse?"
        + "gid=" + (urlParams.get("gid") ?? "")
        + "&titlePrefix=" + (urlParams.get("titlePrefix") ?? "")
        + "&sort=" + sessionStorage.getItem("sort")
        + "&rowCount=" + sessionStorage.getItem("rowCount")
        + "&page=" + sessionStorage.getItem("page");
    callAjax(newURL);
} else if (newQuery){
    sessionStorage.setItem("mtitle", (urlParams.get("mtitle") ?? ""))
    sessionStorage.setItem("myear", (urlParams.get("myear") ?? ""))
    sessionStorage.setItem("mdirector", (urlParams.get("mdirector") ?? ""))
    sessionStorage.setItem("mstar", (urlParams.get("mstar") ?? ""))
    sessionStorage.setItem("type", "search")
    let newURL = "api/search?"
        + "mtitle=" + (urlParams.get("mtitle") ?? "")
        + "&myear=" + (urlParams.get("myear") ?? "")
        + "&mdirector=" + (urlParams.get("mdirector") ?? "")
        + "&mstar=" + (urlParams.get("mstar") ?? "")
        + "&sort=" + sessionStorage.getItem("sort")
        + "&rowCount=" + sessionStorage.getItem("rowCount")
        + "&page=" + sessionStorage.getItem("page");
    callAjax(newURL);
} else if (sessionStorage.getItem("type") === "browse") {
    let newURL = "api/browse?"
        + "gid=" + sessionStorage.getItem("gid")
        + "&titlePrefix=" + sessionStorage.getItem("titlePrefix")
        + "&sort=" + sessionStorage.getItem("sort")
        + "&rowCount=" + sessionStorage.getItem("rowCount")
        + "&page=" + sessionStorage.getItem("page");
    callAjax(newURL);
} else {
    let newURL = "api/search?"
        + "mtitle=" + sessionStorage.getItem("mtitle")
        + "&myear=" + sessionStorage.getItem("myear")
        + "&mdirector=" + sessionStorage.getItem("mdirector")
        + "&mstar=" + sessionStorage.getItem("mstar")
        + "&sort=" + sessionStorage.getItem("sort")
        + "&rowCount=" + sessionStorage.getItem("rowCount")
        + "&page=" + sessionStorage.getItem("page");
    callAjax(newURL);
}

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