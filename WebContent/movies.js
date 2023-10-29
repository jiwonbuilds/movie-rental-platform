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
        ratingCell.innerHTML = "&star;&nbsp;" + resultData[i]["rating"];
        row.appendChild(ratingCell);

        // appendChild(buttonForCart)

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

