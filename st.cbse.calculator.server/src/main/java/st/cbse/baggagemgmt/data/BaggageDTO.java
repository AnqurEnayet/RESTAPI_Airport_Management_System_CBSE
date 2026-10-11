package st.cbse.baggagemgmt.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List; // ADDED: Import List for statusHistory
import java.util.stream.Collectors; // ADDED: Import Collectors for stream operations (joining history)

public class BaggageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String baggageNumber;
    private String flightNumber;
    private String passengerName;
    private String currentStatusDescription; // Using String for description
    private String lastMessage;
    private LocalDateTime lastUpdateTime;
    private List<String> statusHistory; // ADDED: List of historical messages
    private String fullStatusHistory; // ADDED: Concatenated history string for easy display

    public BaggageDTO() {}

    // MODIFIED: Constructor to convert from Baggage entity to BaggageDTO, now including history
    public BaggageDTO(Baggage baggage) {
        if (baggage != null) {
            this.baggageNumber = baggage.getBaggageNumber();
            this.flightNumber = baggage.getFlightNumber();
            this.passengerName = baggage.getPassengerName();
            // Using getDescription() to get the human-readable status
            this.currentStatusDescription = baggage.getCurrentStatus() != null ? baggage.getCurrentStatus().getDescription() : null;
            this.lastMessage = baggage.getLastMessage();
            this.lastUpdateTime = baggage.getLastUpdateTime();
            this.statusHistory = baggage.getStatusHistory(); // ADDED: Populate status history list
            // ADDED: Populate concatenated history string from the list
            this.fullStatusHistory = baggage.getStatusHistory() != null ?
                                     String.join("\n", baggage.getStatusHistory()) : "";
        }
    }

    // Getters and Setters
    public String getBaggageNumber() { return baggageNumber; }
    public void setBaggageNumber(String baggageNumber) { this.baggageNumber = baggageNumber; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getCurrentStatusDescription() { return currentStatusDescription; }
    public void setCurrentStatusDescription(String currentStatusDescription) { this.currentStatusDescription = currentStatusDescription; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }

    // ADDED: Getters and Setters for the new history fields
    public List<String> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<String> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public String getFullStatusHistory() {
        return fullStatusHistory;
    }

    public void setFullStatusHistory(String fullStatusHistory) {
        this.fullStatusHistory = fullStatusHistory;
    }

    @Override
    public String toString() {
        return "BaggageDTO{" +
               "baggageNumber='" + baggageNumber + '\'' +
               ", flightNumber='" + flightNumber + '\'' +
               ", passengerName='" + passengerName + '\'' +
               ", currentStatusDescription='" + currentStatusDescription + '\'' +
               ", lastMessage='" + lastMessage + '\'' +
               ", lastUpdateTime=" + lastUpdateTime +
               ", statusHistory=" + statusHistory +
               ", fullStatusHistory='" + fullStatusHistory + '\'' +
               '}';
    }
}