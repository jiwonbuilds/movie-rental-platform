let add_form = $("#add_form");
function handleAddResult(resultData) {
    if (resultData === "failed") {
        $("#add_error_message").text("Failed to add. Please try again.");
    } else {
        $("#add_error_message").text(resultData);
    }
}

function submitAddForm(submitEvent) {
    submitEvent.preventDefault();
    $.ajax(
        "../api/add-movie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_form.serialize(),
            success: handleAddResult
        }
    );
}

add_form.submit(submitAddForm);
