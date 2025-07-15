// st/cbse/flightmgmt/data/FlightDTO.java
package st.cbse.flightmgmt.data;

import java.io.Serializable;
import java.time.LocalDateTime;

// This DTO encapsulates flight data for transfer between layers (e.g., EJB to REST, or to client).
public class FlightDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long flightId; // Corresponds to the entity's ID
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int availableSeats;
    private double price;

    // Default constructor for (de)serialization
    public FlightDTO() {
    }

    // Constructor for creating DTOs from entities or new data
    public FlightDTO(Long flightId, String flightNumber, String origin, String destination,
                     LocalDateTime departureTime, LocalDateTime arrivalTime, int availableSeats, double price) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // Getters and Setters
    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "FlightDTO{" +
               "flightId=" + flightId +
               ", flightNumber='" + flightNumber + '\'' +
               ", origin='" + origin + '\'' +
               ", destination='" + destination + '\'' +
               ", departureTime=" + departureTime +
               ", arrivalTime=" + arrivalTime +
               ", availableSeats=" + availableSeats +
               ", price=" + price +
               '}';
    }
}