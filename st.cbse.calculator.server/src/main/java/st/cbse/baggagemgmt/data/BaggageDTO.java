package st.cbse.baggagemgmt.data;

import java.io.Serializable;
import java.time.LocalDateTime;

// DTO for external representation or specific data transfer needs
public class BaggageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String baggageNumber;
    private String flightNumber;
    private String passengerName;
    private BaggageStatus currentStatus;
    private String lastMessage;
    private LocalDateTime lastUpdateTime;

    public BaggageDTO() {}

    // Constructor to convert from Baggage entity to BaggageDTO
    public BaggageDTO(Baggage baggage) {
        if (baggage != null) {
            this.baggageNumber = baggage.getBaggageNumber();
            this.flightNumber = baggage.getFlightNumber();
            this.passengerName = baggage.getPassengerName();
            this.currentStatus = baggage.getCurrentStatus();
            this.lastMessage = baggage.getLastMessage();
            this.lastUpdateTime = baggage.getLastUpdateTime();
        }
    }

    // Getters and Setters
    public String getBaggageNumber() { return baggageNumber; }
    public void setBaggageNumber(String baggageNumber) { this.baggageNumber = baggageNumber; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public BaggageStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(BaggageStatus currentStatus) { this.currentStatus = currentStatus; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
}