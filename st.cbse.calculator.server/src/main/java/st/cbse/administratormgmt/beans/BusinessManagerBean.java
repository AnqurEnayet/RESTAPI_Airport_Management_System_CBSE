package st.cbse.administratormgmt.beans;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import st.cbse.administratormgmt.interfaces.BusinessManagerRemote;
import st.cbse.flightmgmt.data.FlightDTO;
import st.cbse.flightmgmt.interfaces.FlightManagementRemote;
import st.cbse.passengermgmt.data.BookingDTO;
import st.cbse.passengermgmt.interfaces.PassengerManagementRemote;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class BusinessManagerBean implements BusinessManagerRemote {

    private static final Logger LOGGER = Logger.getLogger(BusinessManagerBean.class.getName());

    @EJB
    private FlightManagementRemote flightManagement; // Inject existing FlightManagement EJB

    @EJB
    private PassengerManagementRemote passengerManagement; // Inject existing PassengerManagement EJB

    // --- Flight Management ---

    @Override
    public FlightDTO updateFlight(FlightDTO flightDTO) {
        if (flightDTO == null || flightDTO.getFlightId() == null) {
            LOGGER.log(Level.WARNING, "Attempted to update flight with null DTO or ID.");
            return null;
        }
        LOGGER.log(Level.INFO, "Updating flight with ID: {0}", flightDTO.getFlightId());
        // Delegate to the existing FlightManagement EJB
        return flightManagement.updateFlight(flightDTO);
    }

    @Override
    public void deleteFlight(Long flightId) {
        if (flightId == null) {
            LOGGER.log(Level.WARNING, "Attempted to delete flight with null ID.");
            return;
        }
        LOGGER.log(Level.INFO, "Deleting flight with ID: {0}", flightId);
        // Delegate to the existing FlightManagement EJB
        flightManagement.deleteFlight(flightId);
    }

    @Override
    public List<FlightDTO> getAllFlights() {
        LOGGER.log(Level.INFO, "Retrieving all flights for Business Manager.");
        // Delegate to the existing FlightManagement EJB
        return flightManagement.getAllFlights();
    }

    // --- Passenger Booking Management ---

    @Override
    public BookingDTO updateBooking(BookingDTO bookingDTO) {
        if (bookingDTO == null || bookingDTO.getBookingId() == null) {
            LOGGER.log(Level.WARNING, "Attempted to update booking with null DTO or ID.");
            return null;
        }
        LOGGER.log(Level.INFO, "Updating booking with ID: {0}", bookingDTO.getBookingId());
        // Delegate to the existing PassengerManagement EJB
        // This line assumes 'updateBooking' method exists in PassengerManagementRemote.
        // If not, you'd need to add it there and its implementation in PassengerManagementBean.
        return passengerManagement.updateBooking(bookingDTO);
    } // <--- VERIFY THIS CLOSING BRACE IS PRESENT AND CORRECTLY PLACED IN YOUR FILE

    @Override
    public List<BookingDTO> getAllBookings() {
        LOGGER.log(Level.INFO, "Retrieving all bookings for Business Manager.");
        // Delegate to the existing PassengerManagement EJB
        return passengerManagement.getAllBookings();
    }
}