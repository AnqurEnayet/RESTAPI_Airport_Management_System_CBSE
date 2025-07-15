//  src/main/java/st/cbse/flightmgmt/data/FlightDTO.java  (client)
package st.cbse.flightmgmt.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlightDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long flightId;            // unchanged
    private String flightNumber;
    private String origin;            // ← rename to match server
    private String destination;       // ← rename to match server
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int availableSeats;       // ← primitive, not Integer
    private double price;             // ← primitive, not Double

    public FlightDTO() {}

    public FlightDTO(Long flightId, String flightNumber, String origin,
                     String destination, LocalDateTime departureTime,
                     LocalDateTime arrivalTime, int availableSeats,
                     double price) {
        this.flightId       = flightId;
        this.flightNumber   = flightNumber;
        this.origin         = origin;
        this.destination    = destination;
        this.departureTime  = departureTime;
        this.arrivalTime    = arrivalTime;
        this.availableSeats = availableSeats;
        this.price          = price;
    }

    /* getters & setters generated verbatim from the server copy */

    public Long getFlightId()             { return flightId; }
    public void setFlightId(Long flightId){ this.flightId = flightId; }

    public String getFlightNumber()       { return flightNumber; }
    public void setFlightNumber(String f) { this.flightNumber = f; }

    public String getOrigin()             { return origin; }
    public void setOrigin(String origin)  { this.origin = origin; }

    public String getDestination()               { return destination; }
    public void setDestination(String destination){ this.destination = destination; }

    public LocalDateTime getDepartureTime()               { return departureTime; }
    public void setDepartureTime(LocalDateTime departure) { this.departureTime = departure; }

    public LocalDateTime getArrivalTime()               { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrival)   { this.arrivalTime = arrival; }

    public int getAvailableSeats()            { return availableSeats; }
    public void setAvailableSeats(int seats)  { this.availableSeats = seats; }

    public double getPrice()          { return price; }
    public void setPrice(double price){ this.price = price; }

    /* equals / hashCode on flightId only (like server side) */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightDTO dto)) return false;
        return Objects.equals(flightId, dto.flightId);
    }
    @Override public int hashCode() { return Objects.hash(flightId); }

    @Override public String toString() {
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
