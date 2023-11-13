
function handleMetadata(resultData) {
    console.log("handleResult: populating metadata info from resultData");

    let metadataElement = jQuery("#metadata");


    resultData.forEach(function(table) {
        metadataElement.append('<h3>' + table.tableName + '</h3>');

        var tableHTML = '<table><thead><tr><th>Column Name</th><th>Column Type</th><th>Column Size</th></tr></thead><tbody>';

        table.columns.forEach(function(column) {
            tableHTML += '<tr><td>' + column.columnName + '</td><td>' + column.columnType + '</td><td>' + column.columnSize + '</td></tr>';
        });

        tableHTML += '</tbody></table>';

        metadataElement.append(tableHTML);
    });
}


jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "../api/index",
    success: (resultData) => handleMetadata(resultData)
});