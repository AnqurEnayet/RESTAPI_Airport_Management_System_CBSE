package st.cbse.administratormgmt.interfaces;

import jakarta.ejb.Remote;
import st.cbse.flightmgmt.data.FlightDTO;
import st.cbse.passengermgmt.data.BookingDTO;

import java.util.List; // Assuming getAllFlights and getAllBookings might be useful for display

@Remote
public interface BusinessManagerRemote {

    // --- Flight Management ---
    FlightDTO updateFlight(FlightDTO flightDTO);
    void deleteFlight(Long flightId);
    List<FlightDTO> getAllFlights(); // Adding for easier display in admin panel

    // --- Passenger Booking Management ---
    BookingDTO updateBooking(BookingDTO bookingDTO);
    List<BookingDTO> getAllBookings(); // Adding for easier display in admin panel
}