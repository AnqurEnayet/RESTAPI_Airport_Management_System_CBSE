document.addEventListener('DOMContentLoaded', function() {
    const baggageDropForm = document.getElementById('baggageDropForm');
    const statusMessageDiv = document.getElementById('statusMessage');

    baggageDropForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent default form submission

        const baggageNumber = document.getElementById('baggageNumber').value.trim();
        const flightNumber = document.getElementById('flightNumber').value.trim();
        const passengerName = document.getElementById('passengerName').value.trim();

        // Basic validation
        if (!baggageNumber || !flightNumber || !passengerName) {
            displayMessage('Please fill in all fields.', 'error');
            return;
        }

        const dropOffData = {
            baggageNumber: baggageNumber,
            flightNumber: flightNumber,
            passengerName: passengerName
        };

        // The URL for your new BaggageManagement REST endpoint
        const dropOffUrl = 'http://localhost:8080/st.cbse.logisticscenter.server/rest-api/baggage/dropoff';

        displayMessage('Processing baggage drop-off...', ''); // Show processing message

        fetch(dropOffUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(dropOffData),
        })
        .then(response => {
            if (response.status === 409) { // Conflict - baggage already in process
                return response.json().then(data => { throw new Error(data.message || 'Baggage already in process.') });
            }
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text || 'Failed to drop off baggage.') });
            }
            return response.json();
        })
        .then(data => {
            displayMessage(data.message || 'Baggage dropped off successfully!', 'success');
            // Redirect to passengermenu.html after a short delay for the user to read the message
            setTimeout(() => {
                window.location.href = 'passengermenu.html';
            }, 2000); // 2 seconds delay
        })
        .catch(error => {
            console.error('Error:', error);
            displayMessage('Error: ' + error.message, 'error');
        });
    });

    function displayMessage(message, type) {
        statusMessageDiv.textContent = message;
        statusMessageDiv.className = `message ${type}`;
        statusMessageDiv.style.display = 'block';
    }
});