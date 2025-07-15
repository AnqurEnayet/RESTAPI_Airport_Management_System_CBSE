// st/cbse/flightmgmt/data/Flight.java
package st.cbse.flightmgmt.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity // Marks this class as a JPA entity, mapped to a database table
public class Flight implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // Specifies the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates ID for new entities
    private Long id;

    @Column(nullable = false, unique = true) // Flight number cannot be null and must be unique
    private String flightNumber;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    private int availableSeats;
    private double price;

    public Flight() {
    }

    public Flight(String flightNumber, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, int availableSeats, double price) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "Flight{" +
               "id=" + id +
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