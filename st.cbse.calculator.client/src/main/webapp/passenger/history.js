document.addEventListener('DOMContentLoaded', function() {
    const tableBody = document.querySelector('#bookingTable tbody');
    const statusMessage = document.getElementById('statusMessage');
    const table = document.getElementById('bookingTable');

    const url = 'http://localhost:8080/st.cbse.logisticscenter.server/rest-api/bookings/all';

    fetch(url)
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            return response.json();
        })
        .then(data => {
            statusMessage.style.display = 'none';
            table.style.display = 'table';
            
            if (data.length === 0) {
                statusMessage.textContent = 'No bookings found.';
                statusMessage.style.display = 'block';
                table.style.display = 'none';
                return;
            }
            
            data.forEach(booking => {
                const row = tableBody.insertRow();
                
                row.insertCell(0).textContent = booking.bookingId;
                row.insertCell(1).textContent = booking.passengerName;

                const flightCell = row.insertCell(2);
                const flightDetails = `
                    <p><b>From:</b> ${booking.origin}</p>
                    <p><b>To:</b> ${booking.destination}</p>
                    <p><b>Departure:</b> ${new Date(booking.startTime).toLocaleString()}</p>
                    <p><b>Arrival:</b> ${new Date(booking.endTime).toLocaleString()}</p>
                `;
                flightCell.innerHTML = flightDetails;

                row.insertCell(3).textContent = booking.numberOfTickets;
                row.insertCell(4).textContent = `$${booking.finalPrice.toFixed(2)}`;
                row.insertCell(5).textContent = booking.baggageNumber;
            });
        })
        .catch(error => {
            statusMessage.textContent = 'Error fetching booking history: ' + error.message;
            statusMessage.className = 'message error';
            table.style.display = 'none';
        });
});