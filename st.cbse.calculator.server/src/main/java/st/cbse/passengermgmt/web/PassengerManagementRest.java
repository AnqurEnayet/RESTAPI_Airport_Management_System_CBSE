package st.cbse.passengermgmt.web;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import st.cbse.passengermgmt.data.Booking;    // Keep if Booking is used elsewhere, or remove if not needed.
import st.cbse.passengermgmt.data.BookingDTO; // Make sure this is imported
import st.cbse.passengermgmt.interfaces.PassengerManagementRemote;

import java.util.List;

@Path("bookings")
public class PassengerManagementRest {

    @EJB
    private PassengerManagementRemote passengerManagement;

    @POST
    @Path("/book")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookFlight(BookingDTO bookingDto) {
        try {
            // MODIFIED: Changed the type of 'newBooking' from 'Booking' to 'BookingDTO'
            BookingDTO newBooking = passengerManagement.createBooking(
                bookingDto.getFlightId(),
                bookingDto.getPassengerName(),
                bookingDto.getNumberOfTickets()
            );
            // The .entity() method can accept a DTO, so this part is fine.
            return Response.status(Response.Status.CREATED).entity(newBooking).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings() {
        try {
            List<BookingDTO> bookings = passengerManagement.getAllBookings();
            return Response.ok(bookings).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error: " + e.getMessage()).build();
        }
    }
}