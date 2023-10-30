/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p><strong>" + resultData[0]["star_name"] + "</strong><br>" +
        "Date Of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let row = document.createElement("tr");
        let rowHTML =
            "<td>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>' +
            "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        row.innerHTML = rowHTML;

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
                data: { mid: resultData[i]['movie_id'],
                    mtitle: resultData[i]["movie_title"]},
                success: function() {
                    alert(resultData[i]["movie_title"] + " added to your cart!");
                }
            });
        });

        let cartCell = document.createElement("td");
        cartCell.appendChild(cartButton);
        row.appendChild(cartCell);

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(row);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});