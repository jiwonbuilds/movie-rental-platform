let payment_form = $("#payment_form");
function handlePaymentResult(resultData) {
    let paymentResult = JSON.parse(resultData);
    if (paymentResult["status"] === "success") {
        window.location.replace("confirmation.html");
    } else {
        $("#checkout_error_message").text(paymentResult["message"]);
    }
}

function confirmPayment(submitEvent) {
    submitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

payment_form.submit(confirmPayment);
