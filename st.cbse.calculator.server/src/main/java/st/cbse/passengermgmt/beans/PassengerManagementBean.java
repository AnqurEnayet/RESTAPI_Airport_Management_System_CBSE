package st.cbse.passengermgmt.beans;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import st.cbse.flightmgmt.data.FlightDTO;
import st.cbse.flightmgmt.interfaces.FlightManagementRemote;
import st.cbse.passengermgmt.data.Booking;
import st.cbse.passengermgmt.data.BookingDTO;
import st.cbse.passengermgmt.interfaces.PassengerManagementRemote;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class PassengerManagementBean implements PassengerManagementRemote, Serializable {

    // Using default access for consistency with other components.
    // Note: Best practice is to use 'private' for encapsulation.
    @PersistenceContext
    EntityManager em;

    // Inject the FlightManagement EJB to access and update flight data
    @EJB
    private FlightManagementRemote flightManagement;

    @Override
    public Booking createBooking(Long flightId, String passengerName, int numberOfTickets) {
        // Find the flight to check seat availability
        FlightDTO flight = flightManagement.getFlightById(flightId);

        if (flight == null) {
            throw new IllegalArgumentException("Flight not found.");
        }

        if (flight.getAvailableSeats() < numberOfTickets) {
            throw new IllegalStateException("Not enough seats available for this flight.");
        }

        // Create the new booking entity
        Booking newBooking = new Booking();
        newBooking.setFlightId(flightId);
        newBooking.setPassengerName(passengerName);
        newBooking.setNumberOfTickets(numberOfTickets);
        newBooking.setBaggageNumber(UUID.randomUUID().toString().substring(0, 8)); // Generate a random baggage number
        newBooking.setBookingDate(LocalDateTime.now());

        // Persist the booking to the database
        em.persist(newBooking);

        // --- TEMPORARILY COMMENTED OUT FOR NOW ---
        // The FlightManagementRemote interface needs to be updated to include the
        // updateFlightSeats method before this line will compile.
        // flightManagement.updateFlightSeats(flightId, flight.getAvailableSeats() - numberOfTickets);

        return newBooking;
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return em.createQuery("SELECT b FROM Booking b", Booking.class)
                .getResultList()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setFlightId(booking.getFlightId());
        dto.setPassengerName(booking.getPassengerName());
        dto.setNumberOfTickets(booking.getNumberOfTickets());
        dto.setBaggageNumber(booking.getBaggageNumber());

        // Fetch flight details and populate the DTO
        FlightDTO flight = flightManagement.getFlightById(booking.getFlightId());
        if (flight != null) {
            dto.setOrigin(flight.getOrigin());
            dto.setDestination(flight.getDestination());
            dto.setStartTime(flight.getDepartureTime()); // CHANGED: getDepartureTime()
            dto.setEndTime(flight.getArrivalTime());     // CHANGED: getArrivalTime()
            
            // Calculate the final price. The FlightDTO's price is a double, so we need to convert it.
            BigDecimal finalPrice = BigDecimal.valueOf(flight.getPrice()).multiply(BigDecimal.valueOf(booking.getNumberOfTickets()));
            dto.setFinalPrice(finalPrice);
        }

        return dto;
    }
}