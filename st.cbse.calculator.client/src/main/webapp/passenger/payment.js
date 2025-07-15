document.addEventListener('DOMContentLoaded', function() {
    // Get the booking details from the URL
    const params = new URLSearchParams(window.location.search);
    const flightId = params.get('flightId');
    const passengerName = params.get('passengerName');
    const numberOfTickets = params.get('numberOfTickets');

    const confirmPayBtn = document.getElementById('confirmPayBtn');
    const statusMessage = document.getElementById('statusMessage');

    // Check if details are missing, if so, display an error
    if (!flightId || !passengerName || !numberOfTickets) {
        statusMessage.textContent = 'Error: Missing booking details. Please go back and try again.';
        statusMessage.className = 'message error';
        confirmPayBtn.style.display = 'none'; // Hide the button if no data
        return;
    }

    // Add click event listener to the "Confirm Pay" button
    confirmPayBtn.addEventListener('click', function() {
        // Construct the booking data object to send to the server
        const bookingData = {
            flightId: parseInt(flightId, 10),
            passengerName: passengerName,
            numberOfTickets: parseInt(numberOfTickets, 10)
        };
        
        // Correct URL for the booking endpoint
        const bookingUrl = 'http://localhost:8080/st.cbse.logisticscenter.server/rest-api/bookings/book';

        statusMessage.textContent = 'Processing payment...';
        statusMessage.className = 'message';
        
        fetch(bookingUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bookingData),
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(data => {
            // Redirect to the final confirmation page on successful booking
            window.location.href = 'confirmation.html';
        })
        .catch(error => {
            statusMessage.textContent = 'Error: ' + error.message;
            statusMessage.className = 'message error';
        });
    });
});