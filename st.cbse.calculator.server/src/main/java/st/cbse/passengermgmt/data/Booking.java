package st.cbse.passengermgmt.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Booking implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private Long flightId;
    private String passengerName;
    private int numberOfTickets;
    private String baggageNumber;
    private LocalDateTime bookingDate;

    // Default constructor for JPA
    public Booking() {
    }

    // Constructor for creating new bookings (without ID or bookingDate, which are generated)
    public Booking(Long flightId, String passengerName, int numberOfTickets, String baggageNumber) {
        this.flightId = flightId;
        this.passengerName = passengerName;
        this.numberOfTickets = numberOfTickets;
        this.baggageNumber = baggageNumber;
        this.bookingDate = LocalDateTime.now(); // Set upon creation
    }

    // Getters and Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public int getNumberOfTickets() { return numberOfTickets; }
    public void setNumberOfTickets(int numberOfTickets) { this.numberOfTickets = numberOfTickets; }
    public String getBaggageNumber() { return baggageNumber; }
    public void setBaggageNumber(String baggageNumber) { this.baggageNumber = baggageNumber; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
}