document.addEventListener('DOMContentLoaded', function() {
    const params = new URLSearchParams(window.location.search);
    const flightId = params.get('flightId');
    const flightDetailsDiv = document.getElementById('flightDetails');
    const confirmBookingForm = document.getElementById('confirmBookingForm');
    const statusMessage = document.getElementById('statusMessage');

    if (!flightId) {
        flightDetailsDiv.innerHTML = '<p class="error">Error: No flight selected.</p>';
        return;
    }

    const flightUrl = `http://localhost:8080/st.cbse.logisticscenter.server/rest-api/flights/${flightId}`;

    // Fetch flight details
    fetch(flightUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch flight details. Status: ' + response.status);
            }
            return response.json();
        })
        .then(flight => {
            flightDetailsDiv.innerHTML = `
                <p><b>Flight ID:</b> ${flight.flightId}</p>
                <p><b>From:</b> ${flight.origin}</p>
                <p><b>To:</b> ${flight.destination}</p>
                <p><b>Departure:</b> ${new Date(flight.departureTime).toLocaleString()}</p>
                <p><b>Price per Ticket:</b> $${flight.price.toFixed(2)}</p>
                <p><b>Available Seats:</b> ${flight.availableSeats}</p>
            `;
            confirmBookingForm.style.display = 'block';
        })
        .catch(error => {
            flightDetailsDiv.innerHTML = `<p class="message error">Error: ${error.message}</p>`;
        });

    // Handle form submission
    confirmBookingForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const passengerName = document.getElementById('passengerName').value;
        const numberOfTickets = document.getElementById('numberOfTickets').value;
        
        // This line is the only action required here: redirect to payment.html
        window.location.href = `payment.html?flightId=${flightId}&passengerName=${encodeURIComponent(passengerName)}&numberOfTickets=${numberOfTickets}`;
    });
});