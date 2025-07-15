// st/cbse/flightmgmt/beans/FlightManagementBean.java
package st.cbse.flightmgmt.beans;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import st.cbse.flightmgmt.data.Flight; // Import the Entity from its new package
import st.cbse.flightmgmt.data.FlightDTO; // Import the DTO from its new package
import st.cbse.flightmgmt.interfaces.FlightManagementRemote; // Import the Remote interface

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless // Marks this class as a Stateless Session Bean
public class FlightManagementBean implements FlightManagementRemote {

    @PersistenceContext // Injects the EntityManager for database operations
    EntityManager em;

    // Helper method to convert Entity to DTO
    private FlightDTO toDTO(Flight flight) {
        if (flight == null) {
            return null;
        }
        return new FlightDTO(
            flight.getId(), // Use getId() as the DTO's flightId
            flight.getFlightNumber(),
            flight.getOrigin(),
            flight.getDestination(),
            flight.getDepartureTime(),
            flight.getArrivalTime(),
            flight.getAvailableSeats(),
            flight.getPrice()
        );
    }

    // Helper method to convert DTO to Entity (for new flights or updates)
    // Note: When converting DTO to Entity for persistence, be careful with the ID.
    // For 'create', ID should be null. For 'update', ID should be set from DTO.
    private Flight toEntity(FlightDTO dto) {
        if (dto == null) {
            return null;
        }
        Flight flight = new Flight(
            dto.getFlightNumber(),
            dto.getOrigin(),
            dto.getDestination(),
            dto.getDepartureTime(),
            dto.getArrivalTime(),
            dto.getAvailableSeats(),
            dto.getPrice()
        );
        flight.setId(dto.getFlightId()); // Set ID if it's an existing entity update scenario
        return flight;
    }

    @Override
    public FlightDTO createFlight(FlightDTO flightDTO) {
        Flight flight = toEntity(flightDTO);
        flight.setId(null); // Ensure ID is null for new entity persistence
        em.persist(flight); // Persist the new flight entity
        em.flush(); // Ensure ID is generated and available immediately
        return toDTO(flight); // Return the DTO with the newly generated ID
    }

    @Override
    public FlightDTO getFlightById(Long id) {
        Flight flight = em.find(Flight.class, id);
        return toDTO(flight);
    }

    @Override
    public List<FlightDTO> getAllFlights() {
        TypedQuery<Flight> query = em.createQuery("SELECT f FROM Flight f", Flight.class);
        return query.getResultList().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FlightDTO updateFlight(FlightDTO flightDTO) {
        if (flightDTO.getFlightId() == null) {
            throw new IllegalArgumentException("Flight ID must be provided for update.");
        }
        Flight existingFlight = em.find(Flight.class, flightDTO.getFlightId());
        if (existingFlight == null) {
            return null; // Or throw a specific NotFoundException
        }
        // Update fields from DTO to existing entity
        existingFlight.setFlightNumber(flightDTO.getFlightNumber());
        existingFlight.setOrigin(flightDTO.getOrigin());
        existingFlight.setDestination(flightDTO.getDestination());
        existingFlight.setDepartureTime(flightDTO.getDepartureTime());
        existingFlight.setArrivalTime(flightDTO.getArrivalTime());
        existingFlight.setAvailableSeats(flightDTO.getAvailableSeats());
        existingFlight.setPrice(flightDTO.getPrice());

        // em.merge() returns the managed entity, which is existingFlight in this case
        // as existingFlight is a managed entity. However, calling merge explicitly
        // is good practice or if existingFlight was detached.
        Flight updatedFlight = em.merge(existingFlight);
        return toDTO(updatedFlight);
    }

    @Override
    public void deleteFlight(Long id) {
        Flight flight = em.find(Flight.class, id);
        if (flight != null) {
            em.remove(flight);
        }
    }

    @Override
    public List<FlightDTO> searchFlights(String origin, String destination, LocalDateTime departureDate) {
        StringBuilder jpql = new StringBuilder("SELECT f FROM Flight f WHERE 1=1"); // Start with a true condition

        if (origin != null && !origin.isEmpty()) {
            jpql.append(" AND f.origin = :origin");
        }
        if (destination != null && !destination.isEmpty()) {
            jpql.append(" AND f.destination = :destination");
        }
        if (departureDate != null) {
            // For searching by date, compare only the date part, ignoring time, or use a range.
            // This query searches for flights on the *same day* as departureDate.
            jpql.append(" AND f.departureTime >= :startOfDay AND f.departureTime < :endOfDay");
        }

        TypedQuery<Flight> query = em.createQuery(jpql.toString(), Flight.class);

        if (origin != null && !origin.isEmpty()) {
            query.setParameter("origin", origin);
        }
        if (destination != null && !destination.isEmpty()) {
            query.setParameter("destination", destination);
        }
        if (departureDate != null) {
            // Define the start and end of the specified day
            LocalDateTime startOfDay = departureDate.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = departureDate.toLocalDate().plusDays(1).atStartOfDay();
            query.setParameter("startOfDay", startOfDay);
            query.setParameter("endOfDay", endOfDay);
        }

        return query.getResultList().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}