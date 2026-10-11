// st.cbse.calculator.client/src/main/webapp/administrator/bookings_admin.js

const API_BASE_URL = 'http://localhost:8080/st.cbse.logisticscenter.server/rest-api';

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
        showMessage(`Error: ${error.message}`, 'error');
        throw error;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const bookingForm = document.getElementById('bookingForm');
    const bookingIdInput = document.getElementById('bookingId');
    const submitBookingBtn = document.getElementById('submitBookingBtn');
    const clearFormBtn = document.getElementById('clearFormBtn');
    const bookingsTableBody = document.getElementById('bookingsTableBody');
    const noBookingsMessage = document.getElementById('noBookingsMessage');
    const messageArea = document.getElementById('message-area');

    const searchPassengerNameInput = document.getElementById('searchPassengerName');
    const searchFlightIdInput = document.getElementById('searchFlightId');
    const searchBookingsBtn = document.getElementById('searchBookingsBtn');
    const resetSearchBtn = document.getElementById('resetSearchBtn');


    const BOOKINGS_API_URL = `${API_BASE_URL}/bookings`;
    const FLIGHTS_API_URL = `${API_BASE_URL}/flights`; // Assuming this exists for flight details

    function showMessage(message, type) {
        messageArea.textContent = message;
        messageArea.className = `message ${type}`;
        messageArea.style.display = 'block';
        setTimeout(() => {
            messageArea.style.display = 'none';
        }, 5000);
    }

    function clearUpdateForm() {
        bookingForm.reset();
        bookingIdInput.value = '';
        submitBookingBtn.textContent = 'Update Booking';
        submitBookingBtn.style.backgroundColor = '#28a745';
    }

    function formatDatetime(isoString) {
        if (!isoString) return 'N/A';
        const date = new Date(isoString);
        return date.toLocaleString('en-US', {
            month: '2-digit', day: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit', hour12: true
        });
    }

    async function loadBookings(passengerName = '', flightId = '') {
        bookingsTableBody.innerHTML = '';
        noBookingsMessage.style.display = 'none';

        let url = `${BOOKINGS_API_URL}/all`; // Default to fetch all
        const queryParams = [];
        if (passengerName) queryParams.push(`passengerName=${encodeURIComponent(passengerName)}`);
        if (flightId) queryParams.push(`flightId=${encodeURIComponent(flightId)}`);

        if (queryParams.length > 0) {
            url = `${BOOKINGS_API_URL}/search?${queryParams.join('&')}`; // Assuming a search endpoint
        }

        try {
            const bookings = await fetchData(url);

            if (bookings && bookings.length > 0) {
                for (const booking of bookings) {
                    // Fetch flight details for each booking to display flight number, origin, destination, etc.
                    let flightDetails = {};
                    try {
                        flightDetails = await fetchData(`${FLIGHTS_API_URL}/${booking.flightId}`);
                    } catch (flightError) {
                        console.warn(`Could not fetch flight details for flightId ${booking.flightId}:`, flightError);
                        // Continue even if flight details fail
                    }

                    const row = bookingsTableBody.insertRow();
                    row.innerHTML = `
                        <td>${booking.bookingId}</td>
                        <td>${booking.passengerName}</td>
                        <td>${booking.flightId}</td>
                        <td>${flightDetails.flightNumber || 'N/A'}</td>
                        <td>${flightDetails.origin || 'N/A'}</td>
                        <td>${flightDetails.destination || 'N/A'}</td>
                        <td>${formatDatetime(flightDetails.departureTime)}</td>
                        <td>${formatDatetime(flightDetails.arrivalTime)}</td>
                        <td>${booking.numberOfTickets}</td>
                        <td>${booking.baggageNumber || 'N/A'}</td>
                        <td>$${booking.finalPrice ? booking.finalPrice.toFixed(2) : 'N/A'}</td>
                        <td class="action-buttons">
                            <button class="edit-btn" data-id="${booking.bookingId}" title="Edit Booking"><i class="fas fa-edit"></i></button>
                            <button class="delete-btn" data-id="${booking.bookingId}" title="Delete Booking"><i class="fas fa-trash-alt"></i></button>
                        </td>
                    `;
                }
            } else {
                noBookingsMessage.style.display = 'block';
            }
        } catch (error) {
            console.error('Error loading bookings:', error);
            showMessage('Failed to load bookings: ' + error.message, 'error');
        }
    }

    searchBookingsBtn.addEventListener('click', () => {
        const passengerName = searchPassengerNameInput.value.trim();
        const flightId = searchFlightIdInput.value.trim();
        loadBookings(passengerName, flightId);
    });

    resetSearchBtn.addEventListener('click', () => {
        searchPassengerNameInput.value = '';
        searchFlightIdInput.value = '';
        loadBookings(); // Load all bookings
    });

    bookingForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const bookingId = bookingIdInput.value;
        if (!bookingId) {
            showMessage('Please select a booking from the list to update by clicking the Edit button.', 'error');
            return;
        }

        const bookingData = {
            bookingId: parseInt(bookingId),
            passengerName: document.getElementById('passengerName').value.trim(),
            flightId: parseInt(document.getElementById('flightId').value),
            numberOfTickets: parseInt(document.getElementById('numberOfTickets').value),
            baggageNumber: document.getElementById('baggageNumber').value.trim() || null
        };

        if (!bookingData.passengerName || isNaN(bookingData.flightId) || isNaN(bookingData.numberOfTickets) || bookingData.numberOfTickets <= 0) {
            showMessage('Please fill in all required fields with valid data (Passenger Name, Flight ID, Number of Tickets).', 'error');
            return;
        }

        try {
            // Assuming PUT /bookings/{id} for update
            const response = await fetchData(`${BOOKINGS_API_URL}/${bookingId}`, 'PUT', bookingData);
            if (response) {
                showMessage('Booking updated successfully!', 'success');
                clearUpdateForm();
                loadBookings(searchPassengerNameInput.value.trim(), searchFlightIdInput.value.trim()); // Refresh list
            } else {
                showMessage('Booking update completed, but no data returned. Refreshing list.', 'success');
                clearUpdateForm();
                loadBookings(searchPassengerNameInput.value.trim(), searchFlightIdInput.value.trim()); // Refresh list
            }
        } catch (error) {
            console.error('Error updating booking:', error);
            showMessage('Failed to update booking: ' + error.message, 'error');
        }
    });

    bookingsTableBody.addEventListener('click', async (event) => {
        const target = event.target.closest('button');
        if (!target) return;

        const bookingId = target.dataset.id;

        if (target.classList.contains('edit-btn')) {
            try {
                const booking = await fetchData(`${BOOKINGS_API_URL}/${bookingId}`);
                if (booking) {
                    bookingIdInput.value = booking.bookingId;
                    document.getElementById('passengerName').value = booking.passengerName;
                    document.getElementById('flightId').value = booking.flightId;
                    document.getElementById('numberOfTickets').value = booking.numberOfTickets;
                    document.getElementById('baggageNumber').value = booking.baggageNumber || '';
                }
            } catch (error) {
                console.error('Error fetching booking for edit:', error);
                showMessage('Failed to load booking for editing: ' + error.message, 'error');
            }
        } else if (target.classList.contains('delete-btn')) {
            if (confirm(`Are you sure you want to delete Booking ID: ${bookingId}?`)) {
                try {
                    // Assuming DELETE /bookings/{id} for deletion
                    await fetchData(`${BOOKINGS_API_URL}/${bookingId}`, 'DELETE');
                    showMessage('Booking deleted successfully!', 'success');
                    loadBookings(searchPassengerNameInput.value.trim(), searchFlightIdInput.value.trim()); // Refresh list
                    clearUpdateForm();
                } catch (error) {
                    console.error('Error deleting booking:', error);
                    showMessage('Failed to delete booking: ' + error.message, 'error');
                }
            }
        }
    });

    clearFormBtn.addEventListener('click', clearUpdateForm);

    loadBookings();
});