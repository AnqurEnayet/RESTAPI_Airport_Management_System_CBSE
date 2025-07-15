package st.cbse.passengermgmt.interfaces;

import jakarta.ejb.Remote;
import st.cbse.passengermgmt.data.Booking;
import st.cbse.passengermgmt.data.BookingDTO;

import java.util.List;

@Remote
public interface PassengerManagementRemote {
    Booking createBooking(Long flightId, String passengerName, int numberOfTickets);
    List<BookingDTO> getAllBookings();
}