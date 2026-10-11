package st.cbse.administratormgmt.web;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import st.cbse.administratormgmt.interfaces.BaggageManagerRemote;
import st.cbse.administratormgmt.interfaces.BusinessManagerRemote;
import st.cbse.flightmgmt.data.FlightDTO;
import st.cbse.passengermgmt.data.BookingDTO;
import st.cbse.baggagemgmt.data.BaggageDTO; // For baggage operations

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless // REST resources can be Stateless EJBs
@Path("/admin") // Base path for all administrator-related endpoints
@Produces(MediaType.APPLICATION_JSON) // All methods produce JSON responses by default
@Consumes(MediaType.APPLICATION_JSON)  // All methods consume JSON requests by default
public class AdministratorManagementRest {

    private static final Logger LOGGER = Logger.getLogger(AdministratorManagementRest.class.getName());

    @EJB
    private BusinessManagerRemote businessManager; // Inject the Business Manager EJB

    @EJB
    private BaggageManagerRemote baggageManager;   // Inject the Baggage Manager EJB

    // --- Flight Management Endpoints ---

    @GET
    @Path("/flights")
    public Response getAllFlights() {
        LOGGER.log(Level.INFO, "REST: Request to get all flights for admin.");
        try {
            List<FlightDTO> flights = businessManager.getAllFlights();
            if (flights.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content
            }
            return Response.ok(flights).build(); // 200 OK
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all flights: {0}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to retrieve flights: " + e.getMessage() + "\"}")
                           .build(); // 500 Internal Server Error
        }
    }

    @PUT // Using PUT for updates (idempotent)
    @Path("/flights")
    public Response updateFlight(FlightDTO flightDTO) {
        LOGGER.log(Level.INFO, "REST: Request to update flight with ID: {0}", flightDTO != null ? flightDTO.getFlightId() : "null");
        if (flightDTO == null || flightDTO.getFlightId() == null) {
            return Response.status(Response.Status.BAD_REQUEST) // 400 Bad Request
                           .entity("{\"error\": \"Flight data or Flight ID cannot be null for update.\"}")
                           .build();
        }
        try {
            FlightDTO updatedFlight = businessManager.updateFlight(flightDTO);
            if (updatedFlight == null) {
                return Response.status(Response.Status.NOT_FOUND) // 404 Not Found if flight doesn't exist
                               .entity("{\"error\": \"Flight with ID " + flightDTO.getFlightId() + " not found for update.\"}")
                               .build();
            }
            return Response.ok(updatedFlight).build(); // 200 OK
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating flight ID {0}: {1}", new Object[]{flightDTO.getFlightId(), e.getMessage()});
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to update flight: " + e.getMessage() + "\"}")
                           .build();
        }
    }

    @DELETE
    @Path("/flights/{flightId}")
    public Response deleteFlight(@PathParam("flightId") Long flightId) {
        LOGGER.log(Level.INFO, "REST: Request to delete flight with ID: {0}", flightId);
        if (flightId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"Flight ID cannot be null for deletion.\"}")
                           .build();
        }
        try {
            businessManager.deleteFlight(flightId);
            return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content (successful deletion)
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting flight ID {0}: {1}", new Object[]{flightId, e.getMessage()});
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to delete flight: " + e.getMessage() + "\"}")
                           .build();
        }
    }

    // --- Booking Management Endpoints ---

    @GET
    @Path("/bookings")
    public Response getAllBookings() {
        LOGGER.log(Level.INFO, "REST: Request to get all bookings for admin.");
        try {
            List<BookingDTO> bookings = businessManager.getAllBookings();
            if (bookings.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            return Response.ok(bookings).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all bookings: {0}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to retrieve bookings: " + e.getMessage() + "\"}")
                           .build();
        }
    }

    @PUT
    @Path("/bookings")
    public Response updateBooking(BookingDTO bookingDTO) {
        LOGGER.log(Level.INFO, "REST: Request to update booking with ID: {0}", bookingDTO != null ? bookingDTO.getBookingId() : "null");
        if (bookingDTO == null || bookingDTO.getBookingId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"Booking data or Booking ID cannot be null for update.\"}")
                           .build();
        }
        try {
            BookingDTO updatedBooking = businessManager.updateBooking(bookingDTO);
            if (updatedBooking == null) {
                 return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\": \"Booking with ID " + bookingDTO.getBookingId() + " not found for update.\"}")
                               .build();
            }
            return Response.ok(updatedBooking).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating booking ID {0}: {1}", new Object[]{bookingDTO.getBookingId(), e.getMessage()});
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to update booking: " + e.getMessage() + "\"}")
                           .build();
        }
    }

    // --- Baggage Management Endpoints ---

    @GET
    @Path("/baggage")
    public Response getAllBaggageStatuses() {
        LOGGER.log(Level.INFO, "REST: Request to get all baggage statuses for admin.");
        try {
            List<BaggageDTO> baggageList = baggageManager.getAllBaggageStatuses();
            if (baggageList.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            return Response.ok(baggageList).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all baggage statuses: {0}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to retrieve baggage statuses: " + e.getMessage() + "\"}")
                           .build();
        }
    }

    @GET
    @Path("/baggage/search")
    public Response searchBaggage(
            @QueryParam("flight") @DefaultValue("") String flightNumber,
            @QueryParam("passenger") @DefaultValue("") String passengerName) {
        LOGGER.log(Level.INFO, "REST: Request to search baggage with Flight: {0}, Passenger: {1}", new Object[]{flightNumber, passengerName});
        try {
            List<BaggageDTO> searchResults = baggageManager.searchBaggage(flightNumber, passengerName);
            if (searchResults.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            return Response.ok(searchResults).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching baggage: {0}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to search baggage: " + e.getMessage() + "\"}")
                           .build();
        }
    }

    @POST // Using POST for an action like "callForInspection"
    @Path("/baggage/{baggageNumber}/inspect")
    public Response callForInspection(@PathParam("baggageNumber") String baggageNumber) {
        LOGGER.log(Level.INFO, "REST: Request to call for inspection for baggage: {0}", baggageNumber);
        if (baggageNumber == null || baggageNumber.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\": \"Baggage number cannot be empty for inspection.\"}")
                           .build();
        }
        try {
            baggageManager.callForInspection(baggageNumber);
            return Response.ok("{\"message\": \"Call for inspection initiated for baggage " + baggageNumber + " (dummy action).\"}").build(); // 200 OK
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calling for inspection for baggage {0}: {1}", new Object[]{baggageNumber, e.getMessage()});
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\": \"Failed to initiate inspection: " + e.getMessage() + "\"}")
                           .build();
        }
    }
}