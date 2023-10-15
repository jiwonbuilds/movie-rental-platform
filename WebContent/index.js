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
        rowHTML += "<td>"
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
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet in MoviesServlet.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});