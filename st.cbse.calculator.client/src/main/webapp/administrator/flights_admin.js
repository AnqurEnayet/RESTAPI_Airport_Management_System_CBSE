// st.cbse.calculator.client/src/main/webapp/administrator/flights_admin.js

const API_BASE_URL = 'http://localhost:8080/st.cbse.calculator.server/api'; // ADJUST THIS URL!

/**
 * Generic function to make API requests.
 * @param {string} endpoint The specific API path.
 * @param {string} method HTTP method.
 * @param {object} data Optional data payload.
 * @returns {Promise<object|null>} The JSON response data, or null for 204 No Content.
 */
async function fetchData(endpoint, method = 'GET', data = null) {
    const url = `${API_BASE_URL}${endpoint}`;
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
    };

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(url, options);

        if (!response.ok) {
            let errorText = `HTTP error! Status: ${response.status}`;
            try {
                const errorBody = await response.json();
                errorText += `, Message: ${errorBody.message || JSON.stringify(errorBody)}`;
            } catch (e) {
                errorText += `, Body: ${await response.text()}`;
            }
            throw new Error(errorText);
        }

        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error(`Fetch error for ${method} ${url}:`, error);
        // Do not use alert in production, use a more user-friendly modal or message display
        showMessage(`Error: ${error.message}`, 'error'); // Use the page's message display
        throw error; // Re-throw to propagate error for further handling if needed
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const flightsTableBody = document.getElementById('flightsTableBody');
    const noFlightsMessage = document.getElementById('noFlightsMessage');
    const searchFlightNumberInput = document.getElementById('searchFlightNumber');
    const searchOriginInput = document.getElementById('searchOrigin');
    const searchDestinationInput = document.getElementById('searchDestination');
    const searchFlightsBtn = document.getElementById('searchFlightsBtn');
    const resetSearchBtn = document.getElementById('resetSearchBtn');
    const messageArea = document.getElementById('message-area');

    // Form elements for update
    const flightForm = document.getElementById('flightForm');
    const flightIdInput = document.getElementById('flightId');
    const updateFlightNumberInput = document.getElementById('updateFlightNumber');
    const updateOriginInput = document.getElementById('updateOrigin');
    const updateDestinationInput = document.getElementById('updateDestination');
    const updateDepartureTimeInput = document.getElementById('updateDepartureTime');
    const updateArrivalTimeInput = document.getElementById('updateArrivalTime');
    const updatePriceInput = document.getElementById('updatePrice');
    const updateTotalSeatsInput = document.getElementById('updateTotalSeats');
    const updateAvailableSeatsInput = document.getElementById('updateAvailableSeats');
    const submitFlightBtn = document.getElementById('submitFlightBtn');
    const clearFormBtn = document.getElementById('clearFormBtn');


    const FLIGHTS_API_URL = `${API_BASE_URL}/flights`;

    function showMessage(message, type) {
        messageArea.textContent = message;
        messageArea.className = `message ${type}`;
        messageArea.style.display = 'block';
        setTimeout(() => {
            messageArea.style.display = 'none';
        }, 5000);
    }

    function formatDatetimeForInput(isoString) {
        if (!isoString) return '';
        const date = new Date(isoString);
        // Format to YYYY-MM-DDTHH:MM for datetime-local input
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    function formatDatetimeForDisplay(isoString) {
        if (!isoString) return 'N/A';
        const date = new Date(isoString);
        return date.toLocaleString('en-US', {
            month: '2-digit', day: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit', hour12: true
        });
    }

    async function loadFlights(flightNumber = '', origin = '', destination = '') {
        flightsTableBody.innerHTML = '';
        noFlightsMessage.style.display = 'none';

        let url = `${FLIGHTS_API_URL}/all`; // Default to fetch all
        const queryParams = [];
        if (flightNumber) queryParams.push(`flightNumber=${encodeURIComponent(flightNumber)}`);
        if (origin) queryParams.push(`origin=${encodeURIComponent(origin)}`);
        if (destination) queryParams.push(`destination=${encodeURIComponent(destination)}`);

        if (queryParams.length > 0) {
            url = `${FLIGHTS_API_URL}/search?${queryParams.join('&')}`; // Assuming a search endpoint
        }

        try {
            const flights = await fetchData(url);

            if (flights && flights.length > 0) {
                flights.forEach(flight => {
                    const row = flightsTableBody.insertRow();
                    row.innerHTML = `
                        <td>${flight.flightId}</td>
                        <td>${flight.flightNumber}</td>
                        <td>${flight.origin}</td>
                        <td>${flight.destination}</td>
                        <td>${formatDatetimeForDisplay(flight.departureTime)}</td>
                        <td>${formatDatetimeForDisplay(flight.arrivalTime)}</td>
                        <td>$${flight.price ? flight.price.toFixed(2) : 'N/A'}</td>
                        <td>${flight.totalSeats}</td>
                        <td>${flight.availableSeats}</td>
                        <td class="action-buttons">
                            <button class="edit-btn" data-id="${flight.flightId}" title="Edit Flight"><i class="fas fa-edit"></i></button>
                            <button class="delete-btn" data-id="${flight.flightId}" title="Delete Flight"><i class="fas fa-trash-alt"></i></button>
                        </td>
                    `;
                });
            } else {
                noFlightsMessage.style.display = 'block';
            }
        } catch (error) {
            console.error('Error loading flights:', error);
            showMessage('Failed to load flights: ' + error.message, 'error');
        }
    }

    function clearUpdateForm() {
        flightForm.reset();
        flightIdInput.value = '';
        submitFlightBtn.textContent = 'Update Flight';
        submitFlightBtn.style.backgroundColor = '#28a745'; // Green for update
    }

    searchFlightsBtn.addEventListener('click', () => {
        const flightNumber = searchFlightNumberInput.value.trim();
        const origin = searchOriginInput.value.trim();
        const destination = searchDestinationInput.value.trim();
        loadFlights(flightNumber, origin, destination);
    });

    resetSearchBtn.addEventListener('click', () => {
        searchFlightNumberInput.value = '';
        searchOriginInput.value = '';
        searchDestinationInput.value = '';
        loadFlights(); // Load all flights
    });

    flightsTableBody.addEventListener('click', async (event) => {
        const target = event.target.closest('button');
        if (!target) return;

        const flightId = target.dataset.id;

        if (target.classList.contains('edit-btn')) {
            try {
                const flight = await fetchData(`${FLIGHTS_API_URL}/${flightId}`);
                if (flight) {
                    flightIdInput.value = flight.flightId;
                    updateFlightNumberInput.value = flight.flightNumber;
                    updateOriginInput.value = flight.origin;
                    updateDestinationInput.value = flight.destination;
                    updateDepartureTimeInput.value = formatDatetimeForInput(flight.departureTime);
                    updateArrivalTimeInput.value = formatDatetimeForInput(flight.arrivalTime);
                    updatePriceInput.value = flight.price;
                    updateTotalSeatsInput.value = flight.totalSeats;
                    updateAvailableSeatsInput.value = flight.availableSeats;
                    submitFlightBtn.textContent = 'Update Flight';
                    submitFlightBtn.style.backgroundColor = '#28a745'; // Green for update
                }
            } catch (error) {
                console.error('Error fetching flight for edit:', error);
                showMessage('Failed to load flight for editing: ' + error.message, 'error');
            }
        } else if (target.classList.contains('delete-btn')) {
            if (confirm(`Are you sure you want to delete Flight ID: ${flightId}?`)) {
                try {
                    // Assuming DELETE /flights/{id} for deletion
                    await fetchData(`${FLIGHTS_API_URL}/${flightId}`, 'DELETE');
                    showMessage('Flight deleted successfully!', 'success');
                    loadFlights(searchFlightNumberInput.value.trim(), searchOriginInput.value.trim(), searchDestinationInput.value.trim()); // Refresh list
                    clearUpdateForm();
                } catch (error) {
                    console.error('Error deleting flight:', error);
                    showMessage('Failed to delete flight: ' + error.message, 'error');
                }
            }
        }
    });

    flightForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const flightId = flightIdInput.value;
        if (!flightId) {
            showMessage('Please select a flight to update by clicking the Edit button.', 'error');
            return;
        }

        const flightData = {
            flightId: parseInt(flightId),
            flightNumber: updateFlightNumberInput.value.trim(),
            origin: updateOriginInput.value.trim(),
            destination: updateDestinationInput.value.trim(),
            // Ensure dates are sent in ISO string format
            departureTime: new Date(updateDepartureTimeInput.value).toISOString(),
            arrivalTime: new Date(updateArrivalTimeInput.value).toISOString(),
            price: parseFloat(updatePriceInput.value),
            totalSeats: parseInt(updateTotalSeatsInput.value),
            availableSeats: parseInt(updateAvailableSeatsInput.value)
        };

        // Basic validation
        if (!flightData.flightNumber || !flightData.origin || !flightData.destination ||
            isNaN(flightData.price) || isNaN(flightData.totalSeats) || isNaN(flightData.availableSeats) ||
            !updateDepartureTimeInput.value || !updateArrivalTimeInput.value) { // Check if datetime inputs have values
            showMessage('Please fill in all required fields in the update form with valid data.', 'error');
            return;
        }

        if (flightData.price < 0 || flightData.totalSeats < 1 || flightData.availableSeats < 0 || flightData.availableSeats > flightData.totalSeats) {
             showMessage('Please ensure price is non-negative, total seats are at least 1, and available seats are non-negative and not more than total seats.', 'error');
             return;
        }

        try {
            // Assuming PUT /flights/{id} for update
            const response = await fetchData(`${FLIGHTS_API_URL}/${flightId}`, 'PUT', flightData);
            if (response) {
                showMessage('Flight updated successfully!', 'success');
                clearUpdateForm();
                loadFlights(searchFlightNumberInput.value.trim(), searchOriginInput.value.trim(), searchDestinationInput.value.trim()); // Refresh list
            } else {
                showMessage('Flight update completed, but no data returned. Refreshing list.', 'success');
                clearUpdateForm();
                loadFlights(searchFlightNumberInput.value.trim(), searchOriginInput.value.trim(), searchDestinationInput.value.trim()); // Refresh list
            }
        } catch (error) {
            console.error('Error updating flight:', error);
            showMessage('Failed to update flight: ' + error.message, 'error');
        }
    });

    clearFormBtn.addEventListener('click', clearUpdateForm);

    // Initial load of flights
    loadFlights();
});