package st.cbse.passengermgmt.interfaces;

import jakarta.ejb.Remote;
import st.cbse.passengermgmt.data.Booking;    // Keep this if you use Booking in other places (e.g., for method parameters)
import st.cbse.passengermgmt.data.BookingDTO; // Make sure this is imported

import java.util.List;

@Remote
public interface PassengerManagementRemote {

    // MODIFIED: Changed return type from 'Booking' to 'BookingDTO' to match the implementation in PassengerManagementBean
    BookingDTO createBooking(Long flightId, String passengerName, int numberOfTickets);

    List<BookingDTO> getAllBookings();

    BookingDTO updateBooking(BookingDTO bookingDTO);

    // ADDED (assuming this method exists in your PassengerManagementBean and you want it exposed remotely)
    BookingDTO getBookingById(Long bookingId);
}