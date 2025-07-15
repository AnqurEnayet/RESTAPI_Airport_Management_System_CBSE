const createForm = document.getElementById('createFlightForm');
const statusMessage = document.getElementById('status-message');

const API_URL = 'http://localhost:8080/st.cbse.logisticscenter.server/rest-api/flights/create';

createForm.addEventListener('submit', async (event) => {
    event.preventDefault(); // Prevent the form from submitting normally

    statusMessage.innerHTML = '<p>Creating flight...</p>';

    const flightData = {
        flightNumber: document.getElementById('flightNumber').value,
        origin: document.getElementById('origin').value,
        destination: document.getElementById('destination').value,
        departureTime: document.getElementById('departureTime').value,
        arrivalTime: document.getElementById('arrivalTime').value,
        availableSeats: parseInt(document.getElementById('availableSeats').value),
        price: parseFloat(document.getElementById('price').value)
    };

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(flightData)
        });

        const result = await response.json();

        if (response.ok) {
            // --- NEW CODE ADDED HERE ---
            statusMessage.innerHTML = `<p style="color: green;">Flight created successfully! Redirecting in 3 seconds...</p>`;
            
            // Wait for 3 seconds, then redirect to the main menu
            setTimeout(() => {
                window.location.href = 'airline.html';
            }, 3000); // 3000 milliseconds = 3 seconds
            // --- END OF NEW CODE ---
        } else {
            statusMessage.innerHTML = `<p style="color: red;">Error: ${result.error || 'Failed to create flight.'}</p>`;
        }
    } catch (error) {
        console.error('Error creating flight:', error);
        statusMessage.innerHTML = `<p style="color: red;">Error: Could not connect to the API. Make sure the server is running.</p>`;
    }
});