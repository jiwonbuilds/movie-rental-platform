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
                '<td>' +
                    '<button class="dec_quantity">-</button>' + mquantity + '<button class="inc_quantity">+</button>' +
                '</td>' +
                '<td>' + mprice * mquantity + '</td>' +
                '<td>' + '<button class="remove">Remove</button>' + '</td>' +
            '</tr>';
        let $itemRow = $(rowHTML);
        $("#cart_table tbody").append($itemRow);

        $itemRow.find(".dec_quantity").on("click", function() {
            updateQuantity(movieId, -1);
            window.location.reload();
        });
        $itemRow.find(".inc_quantity").on("click", function() {
            updateQuantity(movieId, 1);
            window.location.reload();
        });
        $itemRow.find(".remove").on("click", function() {
            deleteItem(movieId);
            window.location.reload();
        });
        totalPrice += mprice * mquantity;
    }
    $("#total-price").text(totalPrice.toFixed(2));
    $("#checkout-button").on("click", function() {
        const totalPrice = $("#total-price").text();
        window.location.href = "payment.html?price_total=" + totalPrice;
    })
}

function updateQuantity(mid, amount) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/shopping-cart?"
            + "action=update"
            + "&mid=" + mid
            + "&amount=" + amount, // Setting request url, which is mapped by MoviesServlet in BrowseServlet.java
        success: function(response) { location.reload(); } // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function deleteItem(mid) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/shopping-cart?"
            + "action=delete"
            + "&mid=" + mid, // Setting request url, which is mapped by MoviesServlet in BrowseServlet.java
        success: function(response) { location.reload(); } // Setting callback function to handle data returned successfully by the StarsServlet
    });
}


jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "POST", // Setting request method
    url: "api/shopping-cart?"
        + "action=show", // Setting request url, which is mapped by MoviesServlet in BrowseServlet.java
    success: (resultData) => showCartItems(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});