// st.cbse.calculator.client/src/main/webapp/administrator/baggage_status.js

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
    const baggageTableBody = document.getElementById('baggageTableBody');
    const noBaggageMessage = document.getElementById('noBaggageMessage');
    const searchBaggageIdInput = document.getElementById('searchBaggageId');
    const searchBookingIdInput = document.getElementById('searchBookingId');
    const searchStatusInput = document.getElementById('searchStatus');
    const searchBaggageBtn = document.getElementById('searchBaggageBtn');
    const resetSearchBtn = document.getElementById('resetSearchBtn');
    const messageArea = document.getElementById('message-area');

    const BAGGAGE_API_URL = `${API_BASE_URL}/baggage`; // Assuming a /baggage endpoint exists

    function showMessage(message, type) {
        messageArea.textContent = message;
        messageArea.className = `message ${type}`;
        messageArea.style.display = 'block';
        setTimeout(() => {
            messageArea.style.display = 'none';
        }, 5000);
    }

    async function loadBaggage(baggageId = '', bookingId = '', status = '') {
        baggageTableBody.innerHTML = '';
        noBaggageMessage.style.display = 'none';

        let url = `${BAGGAGE_API_URL}/all`; // Assuming an /all endpoint for baggage
        const queryParams = [];
        if (baggageId) queryParams.push(`baggageId=${encodeURIComponent(baggageId)}`);
        if (bookingId) queryParams.push(`bookingId=${encodeURIComponent(bookingId)}`);
        if (status) queryParams.push(`status=${encodeURIComponent(status)}`);

        if (queryParams.length > 0) {
            url = `${BAGGAGE_API_URL}/search?${queryParams.join('&')}`; // Assuming a search endpoint
        }

        try {
            const baggageRecords = await fetchData(url);

            if (baggageRecords && baggageRecords.length > 0) {
                baggageRecords.forEach(baggage => {
                    const row = baggageTableBody.insertRow();
                    row.innerHTML = `
                        <td>${baggage.baggageId}</td>
                        <td>${baggage.bookingId || 'N/A'}</td>
                        <td>${baggage.weightKg || 'N/A'}</td>
                        <td>${baggage.status || 'N/A'}</td>
                        <td>${baggage.lastLocation || 'N/A'}</td>
                        <td class="action-buttons">
                            <button class="inspect-btn" data-id="${baggage.baggageId}" title="Call for Inspection"><i class="fas fa-search"></i> Inspect</button>
                        </td>
                    `;
                });
            } else {
                noBaggageMessage.style.display = 'block';
            }
        } catch (error) {
            console.error('Error loading baggage records:', error);
            showMessage('Failed to load baggage records: ' + error.message, 'error');
        }
    }

    searchBaggageBtn.addEventListener('click', () => {
        const baggageId = searchBaggageIdInput.value.trim();
        const bookingId = searchBookingIdInput.value.trim();
        const status = searchStatusInput.value.trim();
        loadBaggage(baggageId, bookingId, status);
    });

    resetSearchBtn.addEventListener('click', () => {
        searchBaggageIdInput.value = '';
        searchBookingIdInput.value = '';
        searchStatusInput.value = '';
        loadBaggage(); // Load all baggage records
    });

    baggageTableBody.addEventListener('click', async (event) => {
        const target = event.target.closest('.inspect-btn');
        if (!target) return;

        const baggageId = target.dataset.id;

        if (confirm(`Are you sure you want to call for inspection for Baggage ID: ${baggageId}? This will update its status.`)) {
            try {
                // Assuming an API endpoint to update baggage status to 'Inspecting' or similar
                // Example: PUT /baggage/{id}/status to update status
                // The backend API should accept a status string in the request body, e.g., { "status": "Inspecting" }
                // Or if it's a specific action, it might be a POST to an action endpoint: /baggage/{id}/call-for-inspection
                // For this example, let's assume a PUT to /baggage/{id} with the full baggage object, updating only the status.
                // First, fetch the current baggage details to ensure we send a complete object for PUT
                const currentBaggage = await fetchData(`${BAGGAGE_API_URL}/${baggageId}`);
                if (!currentBaggage) {
                    showMessage(`Baggage ID ${baggageId} not found.`, 'error');
                    return;
                }

                // Create the updated baggage object
                const updatedBaggageData = {
                    ...currentBaggage, // Keep existing fields
                    status: "Awaiting Inspection" // Set the new status
                };

                const response = await fetchData(`${BAGGAGE_API_URL}/${baggageId}`, 'PUT', updatedBaggageData);

                if (response) {
                    showMessage(`Baggage ID ${baggageId} status updated to "Awaiting Inspection".`, 'success');
                    loadBaggage(searchBaggageIdInput.value.trim(), searchBookingIdInput.value.trim(), searchStatusInput.value.trim()); // Refresh list
                } else {
                    showMessage(`Baggage ID ${baggageId} status update completed, but no data returned. Refreshing list.`, 'success');
                    loadBaggage(searchBaggageIdInput.value.trim(), searchBookingIdInput.value.trim(), searchStatusInput.value.trim()); // Refresh list
                }
            } catch (error) {
                console.error('Error calling for inspection:', error);
                showMessage('Failed to call for inspection: ' + error.message, 'error');
            }
        }
    });

    loadBaggage();
});