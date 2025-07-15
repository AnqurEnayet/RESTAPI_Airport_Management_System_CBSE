// st/cbse/flightmgmt/web/FlightManagementRest.java
package st.cbse.flightmgmt.web;

import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue; // Import JsonValue for explicit NULL handling
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import st.cbse.flightmgmt.data.FlightDTO;
import st.cbse.flightmgmt.interfaces.FlightManagementRemote;
import st.cbse.ApplicationConfiguration; // It works without this, but added for clarity and better maintainability

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Path("flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FlightManagementRest {

    @EJB
    FlightManagementRemote flightManagement;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    // Corrected Helper to convert FlightDTO to JsonObject
    private JsonObject toJsonObject(FlightDTO dto) {
        if (dto == null) {
            return Json.createObjectBuilder().build();
        }
        JsonObjectBuilder builder = Json.createObjectBuilder();

        // Handle nullable fields explicitly using if-else or JsonValue.NULL
        if (dto.getFlightId() != null) {
            builder.add("flightId", dto.getFlightId()); // 'add(String, long)' overload
        } else {
            builder.add("flightId", JsonValue.NULL); // 'add(String, JsonValue)' overload
        }

        if (dto.getFlightNumber() != null) {
            builder.add("flightNumber", dto.getFlightNumber());
        } else {
            builder.add("flightNumber", JsonValue.NULL);
        }

        if (dto.getOrigin() != null) {
            builder.add("origin", dto.getOrigin());
        } else {
            builder.add("origin", JsonValue.NULL);
        }

        if (dto.getDestination() != null) {
            builder.add("destination", dto.getDestination());
        } else {
            builder.add("destination", JsonValue.NULL);
        }

        if (dto.getDepartureTime() != null) {
            builder.add("departureTime", dto.getDepartureTime().format(DATE_TIME_FORMATTER));
        } else {
            builder.add("departureTime", JsonValue.NULL);
        }

        if (dto.getArrivalTime() != null) {
            builder.add("arrivalTime", dto.getArrivalTime().format(DATE_TIME_FORMATTER));
        } else {
            builder.add("arrivalTime", JsonValue.NULL);
        }

        // Non-nullable primitives can be added directly
        builder.add("availableSeats", dto.getAvailableSeats());
        builder.add("price", dto.getPrice());

        return builder.build();
    }

    // Helper to convert JsonObject to FlightDTO
    private FlightDTO toFlightDTO(JsonObject json) {
        FlightDTO dto = new FlightDTO();
        if (json.containsKey("flightId") && !json.isNull("flightId")) {
            dto.setFlightId(json.getJsonNumber("flightId").longValue());
        }
        if (json.containsKey("flightNumber") && !json.isNull("flightNumber")) {
            dto.setFlightNumber(json.getString("flightNumber"));
        }
        if (json.containsKey("origin") && !json.isNull("origin")) {
            dto.setOrigin(json.getString("origin"));
        }
        if (json.containsKey("destination") && !json.isNull("destination")) {
            dto.setDestination(json.getString("destination"));
        }
        if (json.containsKey("departureTime") && !json.isNull("departureTime")) {
            try {
                dto.setDepartureTime(LocalDateTime.parse(json.getString("departureTime"), DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid departureTime format. Use ISO_LOCAL_DATE_TIME (e.g., 'YYYY-MM-DDTHH:MM:SS').");
            }
        }
        if (json.containsKey("arrivalTime") && !json.isNull("arrivalTime")) {
            try {
                dto.setArrivalTime(LocalDateTime.parse(json.getString("arrivalTime"), DATE_TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid arrivalTime format. Use ISO_LOCAL_DATE_TIME (e.g., 'YYYY-MM-DDTHH:MM:SS').");
            }
        }
        if (json.containsKey("availableSeats") && !json.isNull("availableSeats")) {
            dto.setAvailableSeats(json.getInt("availableSeats"));
        }
        if (json.containsKey("price") && !json.isNull("price")) {
            dto.setPrice(json.getJsonNumber("price").doubleValue());
        }
        return dto;
    }

    @POST
    @Path("create")
    public Response createFlight(JsonObject flightJson) {
        try {
            FlightDTO flightDTO = toFlightDTO(flightJson);
            if (flightDTO.getFlightNumber() == null || flightDTO.getOrigin() == null || flightDTO.getDestination() == null ||
                flightDTO.getDepartureTime() == null || flightDTO.getArrivalTime() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("error", "Missing required flight details (flightNumber, origin, destination, departureTime, arrivalTime).").build()).build();
            }

            FlightDTO createdFlight = flightManagement.createFlight(flightDTO);
            return Response.status(Response.Status.CREATED).entity(toJsonObject(createdFlight)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("error", e.getMessage()).build()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Json.createObjectBuilder().add("error", "Internal server error: " + e.getMessage()).build()).build();
        }
    }

    @GET
    @Path("{id}")
    public Response getFlight(@PathParam("id") Long id) {
        FlightDTO flight = flightManagement.getFlightById(id);
        if (flight == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(Json.createObjectBuilder().add("error", "Flight with ID " + id + " not found.").build()).build();
        }
        return Response.ok(toJsonObject(flight)).build();
    }

    @GET
    @Path("all")
    public Response getAllFlights() {
        List<FlightDTO> flights = flightManagement.getAllFlights();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        flights.forEach(flight -> jsonArrayBuilder.add(toJsonObject(flight)));
        return Response.ok(jsonArrayBuilder.build()).build();
    }

    @PUT
    @Path("update")
    public Response updateFlight(JsonObject flightJson) {
        try {
            FlightDTO flightDTO = toFlightDTO(flightJson);
            if (flightDTO.getFlightId() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("error", "Flight ID is required for update.").build()).build();
            }
            FlightDTO updatedFlight = flightManagement.updateFlight(flightDTO);
            if (updatedFlight == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(Json.createObjectBuilder().add("error", "Flight not found for update with ID " + flightDTO.getFlightId() + ".").build()).build();
            }
            return Response.ok(toJsonObject(updatedFlight)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("error", e.getMessage()).build()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Json.createObjectBuilder().add("error", "Internal server error: " + e.getMessage()).build()).build();
        }
    }

    @DELETE
    @Path("delete/{id}")
    public Response deleteFlight(@PathParam("id") Long id) {
        FlightDTO existingFlight = flightManagement.getFlightById(id);
        if (existingFlight == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(Json.createObjectBuilder().add("error", "Flight with ID " + id + " not found for deletion.").build()).build();
        }
        flightManagement.deleteFlight(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("search")
    public Response searchFlights(
        @QueryParam("origin") String origin,
        @QueryParam("destination") String destination,
        @QueryParam("departureDate") String departureDateStr
    ) {
        LocalDateTime departureDate = null;
        if (departureDateStr != null && !departureDateStr.isEmpty()) {
            try {
                departureDate = LocalDateTime.parse(departureDateStr + "T00:00:00", DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(Json.createObjectBuilder().add("error", "Invalid departureDate format. Use YYYY-MM-DD.").build()).build();
            }
        }

        List<FlightDTO> flights = flightManagement.searchFlights(origin, destination, departureDate);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        flights.forEach(flight -> jsonArrayBuilder.add(toJsonObject(flight)));
        return Response.ok(jsonArrayBuilder.build()).build();
    }
}