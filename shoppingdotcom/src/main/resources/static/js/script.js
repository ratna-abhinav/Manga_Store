function validateForm() {
    const paymentType = document.getElementById('paymentType').value;
    console.log("JS SCRIPT....");
    if (paymentType === "") {
        alert("Please select a payment method.");
        return false;
    }
    return true;
}