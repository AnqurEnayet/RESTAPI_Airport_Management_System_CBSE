package st.cbse.passengermgmt.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingDTO implements Serializable {
    private Long bookingId;
    private Long flightId;
    private String passengerName;
    private int numberOfTickets;
    private String baggageNumber;

    // ADDED: New fields for flight details
    private String origin;
    private String destination;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal finalPrice;

    // Getters and setters for existing fields
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public String getBaggageNumber() {
        return baggageNumber;
    }

    public void setBaggageNumber(String baggageNumber) {
        this.baggageNumber = baggageNumber;
    }

    // ADDED: Getters and setters for new fields
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
}