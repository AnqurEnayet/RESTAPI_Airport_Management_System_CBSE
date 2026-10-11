package st.cbse.baggagemgmt.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList; // ADDED: Import ArrayList for statusHistory
import java.util.List;      // ADDED: Import List for statusHistory
import java.util.Objects;

public class Baggage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String baggageNumber;
    private String flightNumber;
    private String passengerName;
    private BaggageStatus currentStatus;
    private String lastMessage;
    private LocalDateTime lastUpdateTime;
    private List<String> statusHistory; // ADDED: Field to store the history of status messages

    public Baggage() {
        this.statusHistory = new ArrayList<>(); // ADDED: Initialize history list in default constructor
    }

    public Baggage(String baggageNumber, String flightNumber, String passengerName) {
        this.baggageNumber = baggageNumber;
        this.flightNumber = flightNumber;
        this.passengerName = passengerName;
        this.currentStatus = BaggageStatus.AT_DROP_OFF;
        this.lastMessage = BaggageStatus.AT_DROP_OFF.getDescription();
        this.lastUpdateTime = LocalDateTime.now();
        this.statusHistory = new ArrayList<>(); // ADDED: Initialize history list
        // ADDED: Add the initial status to the history when Baggage object is created
        this.statusHistory.add(formatStatusEntry(BaggageStatus.AT_DROP_OFF.getDescription()));
    }

    // Getters and Setters
    public String getBaggageNumber() { return baggageNumber; }
    public void setBaggageNumber(String baggageNumber) { this.baggageNumber = baggageNumber; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public BaggageStatus getCurrentStatus() { return currentStatus; }

    // MODIFIED: This method now also adds the status update to the history list
    public void setCurrentStatus(BaggageStatus currentStatus) {
        this.currentStatus = currentStatus;
        String newStatusMessage = currentStatus.getDescription();
        this.lastMessage = newStatusMessage;
        this.lastUpdateTime = LocalDateTime.now();
        this.statusHistory.add(formatStatusEntry(newStatusMessage)); // ADDED: Add to history
    }

    // MODIFIED: This overloaded method now also adds the status update to the history list
    public void setCurrentStatus(BaggageStatus currentStatus, String customMessage) {
        this.currentStatus = currentStatus;
        this.lastMessage = customMessage;
        this.lastUpdateTime = LocalDateTime.now();
        this.statusHistory.add(formatStatusEntry(customMessage)); // ADDED: Add to history
    }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }

    // ADDED: Getter for the new statusHistory field
    public List<String> getStatusHistory() {
        return statusHistory;
    }

    // ADDED: Setter for statusHistory (can be useful for deserialization, but direct modification is via setCurrentStatus)
    public void setStatusHistory(List<String> statusHistory) {
        this.statusHistory = statusHistory;
    }

    // ADDED: Helper method to format status entries with a timestamp
    private String formatStatusEntry(String message) {
        return LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ": " + message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Baggage baggage = (Baggage) o;
        return Objects.equals(baggageNumber, baggage.baggageNumber);
    }

    @Override
    public int hashCode() { return Objects.hash(baggageNumber); }

    @Override
    public String toString() {
        return "Baggage{" +
               "baggageNumber='" + baggageNumber + '\'' +
               ", flightNumber='" + flightNumber + '\'' +
               ", passengerName='" + passengerName + '\'' +
               ", currentStatus=" + currentStatus +
               ", lastMessage='" + lastMessage + '\'' +
               ", lastUpdateTime=" + lastUpdateTime +
               ", statusHistory=" + statusHistory + // MODIFIED: Added statusHistory to toString
               '}';
    }
}