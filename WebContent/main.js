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