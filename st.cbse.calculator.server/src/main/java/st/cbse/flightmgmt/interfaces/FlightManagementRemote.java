// st/cbse/flightmgmt/interfaces/FlightManagementRemote.java
package st.cbse.flightmgmt.interfaces;

import jakarta.ejb.Remote;
import st.cbse.flightmgmt.data.FlightDTO; // Import the DTO from its new package

import java.time.LocalDateTime;
import java.util.List;

@Remote // Marks this as a remote EJB interface, accessible from other JVMs/applications
public interface FlightManagementRemote {
    FlightDTO createFlight(FlightDTO flightDTO);
    FlightDTO getFlightById(Long id);
    List<FlightDTO> getAllFlights();
    FlightDTO updateFlight(FlightDTO flightDTO);
    void deleteFlight(Long id);
    // Search method allows flexible queries
    List<FlightDTO> searchFlights(String origin, String destination, LocalDateTime departureDate);
}