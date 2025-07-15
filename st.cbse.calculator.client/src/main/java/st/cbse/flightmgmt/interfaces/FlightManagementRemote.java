package st.cbse.flightmgmt.interfaces;

import java.time.LocalDateTime;
import jakarta.ejb.Remote;
import st.cbse.flightmgmt.data.FlightDTO;

import java.util.List;

@Remote
public interface FlightManagementRemote {

    // Create a new flight
    FlightDTO createFlight(FlightDTO flight);

    // Get a flight by its ID
    FlightDTO getFlightById(Long id);

    // Get all flights
    List<FlightDTO> getAllFlights();

    // Update an existing flight
    FlightDTO updateFlight(FlightDTO flight);

    // Delete a flight by its ID
    void deleteFlight(Long id);

    // Search for flights based on criteria (e.g., departure, arrival)
    List<FlightDTO> searchFlights(String departureAirport, String arrivalAirport, LocalDateTime departureTime);
}