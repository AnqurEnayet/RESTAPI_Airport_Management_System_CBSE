package st.cbse.passengermgmt.beans;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import st.cbse.flightmgmt.data.FlightDTO;
import st.cbse.flightmgmt.interfaces.FlightManagementRemote;
import st.cbse.passengermgmt.data.Booking;
import st.cbse.passengermgmt.data.BookingDTO;
import st.cbse.passengermgmt.interfaces.PassengerManagementRemote;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal; // Import the BaggageManagementLocal interface

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger; // Import Logger
import java.util.stream.Collectors;
import java.math.BigDecimal; // Ensure BigDecimal is imported

@Stateless
public class PassengerManagementBean implements PassengerManagementRemote { // Removed Serializable as it's not typically needed for stateless EJBs

    private static final Logger LOGGER = Logger.getLogger(PassengerManagementBean.class.getName()); // Added Logger

    @PersistenceContext(unitName = "JPAUnit") // MODIFIED: Explicitly specify the unitName
    EntityManager em; // Your EntityManager variable name is 'em'

    @EJB
    private FlightManagementRemote flightManagement;

    // Inject the BaggageManagement EJB
    @EJB
    private BaggageManagementLocal baggageManagement; // ADDED THIS INJECTION

    @Override
    public BookingDTO createBooking(Long flightId, String passengerName, int numberOfTickets) { // MODIFIED: Return BookingDTO
        LOGGER.log(Level.INFO, "Creating booking for Passenger: {0}, Flight ID: {1}, Tickets: {2}",
                   new Object[]{passengerName, flightId, numberOfTickets});

        // Find the flight to check seat availability
        FlightDTO flight = flightManagement.getFlightById(flightId);

        if (flight == null) {
            LOGGER.log(Level.WARNING, "Flight with ID {0} not found.", flightId);
            throw new IllegalArgumentException("Flight not found.");
        }

        if (flight.getAvailableSeats() < numberOfTickets) {
            LOGGER.log(Level.WARNING, "Not enough seats available for Flight ID {0}. Requested: {1}, Available: {2}",
                       new Object[]{flightId, numberOfTickets, flight.getAvailableSeats()});
            throw new IllegalStateException("Not enough seats available for this flight.");
        }

        // Create the new booking entity
        Booking newBooking = new Booking();
        newBooking.setFlightId(flightId);
        newBooking.setPassengerName(passengerName);
        newBooking.setNumberOfTickets(numberOfTickets);
        newBooking.setBaggageNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Generate a random baggage number
        newBooking.setBookingDate(LocalDateTime.now());

        // Persist the booking to the database
        try {
            em.persist(newBooking);
            em.flush(); // Ensure ID is generated and entity state is synched
            LOGGER.log(Level.INFO, "Booking created successfully with ID: {0}", newBooking.getBookingId());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to persist booking: {0}", e.getMessage());
            throw new RuntimeException("Error persisting booking", e);
        }

        /* // --- integrated this part in Baggage Management Rest File and Connected With Baggage Drop ---
         * // After successfully creating a booking and assigning a baggage number,
         * // initiate the baggage processing flow in the BaggageManagement component.
         * baggageManagement.startBaggageProcess(
         * newBooking.getBaggageNumber(),
         * flight.getFlightNumber(), // Use flightNumber from FlightDTO
         * newBooking.getPassengerName()
         * ); //ADDED THIS CALL
         */

        // --- TEMPORARILY COMMENTED OUT FOR NOW ---
        // The FlightManagementRemote interface needs to be updated to include the
        // updateFlightSeats method before this line will compile.
        // flightManagement.updateFlightSeats(flightId, flight.getAvailableSeats() - numberOfTickets);

        return convertToDTO(newBooking); // MODIFIED: Return BookingDTO by converting the new Booking
    }

    @Override
    public BookingDTO updateBooking(BookingDTO bookingDTO) { // MODIFIED: Re-implemented for robustness
        if (bookingDTO == null || bookingDTO.getBookingId() == null) {
            LOGGER.log(Level.WARNING, "Attempted to update booking with null DTO or ID.");
            return null;
        }

        LOGGER.log(Level.INFO, "Attempting to update booking with ID: {0}", bookingDTO.getBookingId());
        try {
            Booking existingBooking = em.find(Booking.class, bookingDTO.getBookingId()); // Using 'em'

            if (existingBooking == null) {
                LOGGER.log(Level.WARNING, "Booking with ID {0} not found for update.", bookingDTO.getBookingId());
                return null;
            }

            // Update fields from DTO
            existingBooking.setPassengerName(bookingDTO.getPassengerName());
            existingBooking.setFlightId(bookingDTO.getFlightId());
            existingBooking.setNumberOfTickets(bookingDTO.getNumberOfTickets());
            existingBooking.setBaggageNumber(bookingDTO.getBaggageNumber());

            Booking updatedBooking = em.merge(existingBooking); // Using 'em'
            LOGGER.log(Level.INFO, "Booking ID {0} updated successfully.", updatedBooking.getBookingId());

            return convertToDTO(updatedBooking); // Return updated DTO using convertToDTO
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to update booking ID {0}: {1}", new Object[]{bookingDTO.getBookingId(), e.getMessage()});
            throw new RuntimeException("Error updating booking", e);
        }
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        LOGGER.log(Level.INFO, "Retrieving all bookings.");
        try {
            TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking b", Booking.class); // Using 'em'
            List<Booking> bookings = query.getResultList();

            return bookings.stream()
                           .map(this::convertToDTO) // Use convertToDTO to get full DTOs
                           .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve all bookings: {0}", e.getMessage());
            throw new RuntimeException("Error retrieving bookings", e);
        }
    }

    @Override
    public BookingDTO getBookingById(Long bookingId) {
        LOGGER.log(Level.INFO, "Retrieving booking by ID: {0}", bookingId);
        try {
            Booking booking = em.find(Booking.class, bookingId); // Using 'em'
            if (booking == null) {
                LOGGER.log(Level.INFO, "Booking with ID {0} not found.", bookingId);
                return null;
            }
            return convertToDTO(booking); // Use convertToDTO for full DTO
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve booking by ID {0}: {1}", new Object[]{bookingId, e.getMessage()});
            throw new RuntimeException("Error retrieving booking by ID", e);
        }
    }

    // Helper method to convert Booking entity to BookingDTO, fetching flight details
    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO(booking); // Use the new DTO constructor

        // Fetch flight details and populate the DTO
        FlightDTO flight = flightManagement.getFlightById(booking.getFlightId());
        if (flight != null) {
            dto.setOrigin(flight.getOrigin());
            dto.setDestination(flight.getDestination());
            dto.setStartTime(flight.getDepartureTime()); // Assuming getDepartureTime()
            dto.setEndTime(flight.getArrivalTime());     // Assuming getArrivalTime()

            // Calculate the final price. The FlightDTO's price is a double, so we need to convert it.
            BigDecimal finalPrice = BigDecimal.valueOf(flight.getPrice()).multiply(BigDecimal.valueOf(booking.getNumberOfTickets()));
            dto.setFinalPrice(finalPrice);
        } else {
            LOGGER.log(Level.WARNING, "Flight details not found for Booking ID {0}, Flight ID {1}",
                       new Object[]{booking.getBookingId(), booking.getFlightId()});
            // Optionally, set default/null values or throw an exception if flight details are mandatory
        }

        return dto;
    }
}