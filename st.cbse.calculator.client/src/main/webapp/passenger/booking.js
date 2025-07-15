document.addEventListener('DOMContentLoaded', function() {
    const tableBody = document.querySelector('#flightTable tbody');
    const statusMessage = document.getElementById('statusMessage');
    const table = document.getElementById('flightTable');
    const searchForm = document.getElementById('searchForm');

    const fetchAndDisplayFlights = () => {
        statusMessage.textContent = 'Loading flights...';
        statusMessage.style.display = 'block';
        table.style.display = 'none';
        
        // Corrected URL based on your server's endpoint
        const url = 'http://localhost:8080/st.cbse.logisticscenter.server/rest-api/flights/all';

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.json();
            })
            .then(allFlights => {
                const origin = document.getElementById('originInput').value.trim().toLowerCase();
                const destination = document.getElementById('destinationInput').value.trim().toLowerCase();
                
                const filteredFlights = allFlights.filter(flight => {
                    const matchOrigin = origin === '' || flight.origin.toLowerCase().includes(origin);
                    const matchDestination = destination === '' || flight.destination.toLowerCase().includes(destination);
                    return matchOrigin && matchDestination;
                });

                statusMessage.style.display = 'none';
                table.style.display = 'table';
                tableBody.innerHTML = ''; 
                
                if (filteredFlights.length === 0) {
                    statusMessage.textContent = 'No matching flights found.';
                    statusMessage.style.display = 'block';
                    table.style.display = 'none';
                    return;
                }
                
                filteredFlights.forEach(flight => {
                    const row = tableBody.insertRow();
                    
                    row.insertCell(0).textContent = flight.flightNumber;
                    row.insertCell(1).textContent = flight.origin;
                    row.insertCell(2).textContent = flight.destination;
                    row.insertCell(3).textContent = new Date(flight.departureTime).toLocaleString();
                    row.insertCell(4).textContent = `$${flight.price.toFixed(2)}`;
                    row.insertCell(5).textContent = flight.availableSeats;
                    
                    const actionCell = row.insertCell(6);
                    const bookButton = document.createElement('button');
                    bookButton.textContent = 'Book';
                    bookButton.className = 'book-btn';
                    bookButton.onclick = () => {
                        window.location.href = `confirmbooking.html?flightId=${flight.flightId}`;
                    };
                    actionCell.appendChild(bookButton);
                });
            })
            .catch(error => {
                statusMessage.textContent = 'Error fetching flights: ' + error.message;
                statusMessage.className = 'message error';
                table.style.display = 'none';
            });
    };

    fetchAndDisplayFlights();

    searchForm.addEventListener('submit', function(event) {
        event.preventDefault();
        fetchAndDisplayFlights();
    });
});