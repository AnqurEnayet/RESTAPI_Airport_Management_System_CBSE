// Get the container where we will display the data
const flightsList = document.getElementById('flights-list');

// Your complete REST API URL for getting all flights
const API_URL = 'http://localhost:8080/st.cbse.logisticscenter.server/rest-api/flights/all';

/**
 * Fetches all flights from the REST API and displays them on the page.
 */
async function fetchFlights() {
    flightsList.innerHTML = '<p style="text-align: center;">Loading flights...</p>';

    try {
        // Use the Fetch API to make a GET request to the server
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        // Parse the JSON response
        const flights = await response.json();

        if (flights.length === 0) {
            flightsList.innerHTML = '<p style="text-align: center;">No flights found.</p>';
            return;
        }

        // Clear the container
        flightsList.innerHTML = '';

        // Loop through each flight and create an HTML element for it
        flights.forEach(flight => {
            const flightCard = document.createElement('div');
            flightCard.className = 'flight-card';
            
            flightCard.innerHTML = `
                <h4>Flight: ${flight.flightNumber}</h4>
                <p><span>Origin:</span> ${flight.origin}</p>
                <p><span>Destination:</span> ${flight.destination}</p>
                <p><span>Departure:</span> ${new Date(flight.departureTime).toLocaleString()}</p>
                <p><span>Arrival:</span> ${new Date(flight.arrivalTime).toLocaleString()}</p>
                <p><span>Available Seats:</span> ${flight.availableSeats}</p>
                <p><span>Price:</span> $${flight.price.toFixed(2)}</p>
            `;
            
            flightsList.appendChild(flightCard);
        });

    } catch (error) {
        console.error('Fetch error:', error);
        flightsList.innerHTML = `<p style="color: red; text-align: center;">Error: Could not connect to the API. Make sure the server is running and check for CORS issues.</p>`;
    }
}

// Call the function immediately on page load to fetch the flights
document.addEventListener('DOMContentLoaded', fetchFlights);