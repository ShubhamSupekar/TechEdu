// Make an AJAX request when the form is submitted
document.querySelector('.input-container').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the default form submission

    // Show the loading screen
    document.getElementById("loadingScreen").style.display = "block";

    // Serialize the form data
    var formData = new FormData(this);

    // Make an AJAX request
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/chat');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function() {
        if (xhr.status === 200) {
            // Hide the loading screen when response is received
            document.getElementById("loadingScreen").style.display = "none";
            // Update the response text
            document.querySelector('.output-text').textContent = xhr.responseText;
        } else {
            console.log('Request failed.  Returned status of ' + xhr.status);
        }
    };
    xhr.send(new URLSearchParams(formData));
});
