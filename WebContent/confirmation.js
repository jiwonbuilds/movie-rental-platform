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