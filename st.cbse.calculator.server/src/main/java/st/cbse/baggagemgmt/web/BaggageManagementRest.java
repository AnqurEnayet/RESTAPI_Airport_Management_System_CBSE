package st.cbse.baggagemgmt.web;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageDTO;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;

import java.util.logging.Logger;

@Path("baggage")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BaggageManagementRest {

    private static final Logger LOGGER = Logger.getLogger(BaggageManagementRest.class.getName());

    @EJB
    private BaggageManagementLocal baggageManagement;

    /**
     * Endpoint for a passenger to drop off baggage.
     * Initiates the baggage processing flow.
     * Expected JSON input:
     * {
     * "baggageNumber": "BAG12345",
     * "flightNumber": "LH456",
     * "passengerName": "John Doe"
     * }
     */
    @POST
    @Path("/dropoff")
    public Response dropOffBaggage(JsonObject baggageInfo) {
        String baggageNumber = baggageInfo.getString("baggageNumber", null);
        String flightNumber = baggageInfo.getString("flightNumber", null);
        String passengerName = baggageInfo.getString("passengerName", null);

        if (baggageNumber == null || flightNumber == null || passengerName == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Json.createObjectBuilder()
                                   .add("error", "Missing required fields: baggageNumber, flightNumber, passengerName")
                                   .build())
                           .build();
        }

        LOGGER.info("Received baggage drop-off request for Baggage: " + baggageNumber + ", Flight: " + flightNumber);
        String result = baggageManagement.startBaggageProcess(baggageNumber, flightNumber, passengerName);

        // Check if the process was actually started or if baggage was already in process
        if (result.contains("already being processed")) {
            return Response.status(Response.Status.CONFLICT) // 409 Conflict
                           .entity(Json.createObjectBuilder().add("message", result).build())
                           .build();
        } else {
            return Response.status(Response.Status.CREATED) // 201 Created
                           .entity(Json.createObjectBuilder().add("message", result).build())
                           .build();
        }
    }

    /**
     * Endpoint to get the current status of a baggage item.
     */
    @GET
    @Path("/status/{baggageNumber}")
    public Response getBaggageStatus(@PathParam("baggageNumber") String baggageNumber) {
        Baggage baggage = baggageManagement.getBaggageStatus(baggageNumber);

        if (baggage == null) {
            return Response.status(Response.Status.NOT_FOUND) // 404 Not Found
                           .entity(Json.createObjectBuilder()
                                   .add("error", "Baggage with number " + baggageNumber + " not found or already completed/removed.")
                                   .build())
                           .build();
        } else {
            // Convert Baggage entity to BaggageDTO for cleaner API response
            BaggageDTO dto = new BaggageDTO(baggage);
            return Response.ok(dto).build(); // 200 OK
        }
    }
}