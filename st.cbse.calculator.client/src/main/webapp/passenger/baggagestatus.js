document.addEventListener('DOMContentLoaded', function() {
    const baggageStatusForm = document.getElementById('baggageStatusForm');
    const statusMessageDiv = document.getElementById('statusMessage');
    const baggageDetailsDiv = document.getElementById('baggageDetails');

    const displayBaggageNumber = document.getElementById('displayBaggageNumber');
    const displayFlightNumber = document.getElementById('displayFlightNumber');
    const displayPassengerName = document.getElementById('displayPassengerName');
    const displayCurrentStatus = document.getElementById('displayCurrentStatus');
    const displayLastMessage = document.getElementById('displayLastMessage');
    const displayLastUpdateTime = document.getElementById('displayLastUpdateTime');

    baggageStatusForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent default form submission

        const baggageNumber = document.getElementById('baggageNumber').value.trim();

        if (!baggageNumber) {
            displayMessage('Please enter a baggage number.', 'error');
            hideBaggageDetails();
            return;
        }

        const statusUrl = `http://localhost:8080/st.cbse.logisticscenter.server/rest-api/baggage/status/${baggageNumber}`;

        displayMessage('Fetching baggage status...', ''); // Show processing message
        hideBaggageDetails(); // Hide previous details

        fetch(statusUrl)
        .then(response => {
            if (!response.ok) {
                // If response is not OK, try to read the error message from the body
                return response.json().then(errorData => {
                    throw new Error(errorData.error || `Error: ${response.status} ${response.statusText}`);
                });
            }
            return response.json();
        })
        .then(data => {
            if (data) {
                displayBaggageNumber.textContent = data.baggageNumber;
                displayFlightNumber.textContent = data.flightNumber;
                displayPassengerName.textContent = data.passengerName;
                displayCurrentStatus.textContent = data.currentStatusDescription;
                displayLastMessage.textContent = data.lastMessage;
                displayLastUpdateTime.textContent = new Date(data.lastUpdateTime).toLocaleString(); // Format date for display

                baggageDetailsDiv.style.display = 'block';
                statusMessageDiv.style.display = 'none'; // Hide general message
            } else {
                displayMessage('Baggage status not available.', 'error');
                hideBaggageDetails();
            }
        })
        .catch(error => {
            console.error('Error fetching baggage status:', error);
            displayMessage('Error: ' + error.message, 'error');
            hideBaggageDetails();
        });
    });

    function displayMessage(message, type) {
        statusMessageDiv.textContent = message;
        statusMessageDiv.className = `message ${type}`;
        statusMessageDiv.style.display = 'block';
    }

    function hideBaggageDetails() {
        baggageDetailsDiv.style.display = 'none';
    }
});