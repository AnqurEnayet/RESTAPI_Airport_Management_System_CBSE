package st.cbse.baggagemgmt.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Baggage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String baggageNumber;
    private String flightNumber;
    private String passengerName;
    private BaggageStatus currentStatus;
    private String lastMessage;
    private LocalDateTime lastUpdateTime;

    public Baggage() {}

    public Baggage(String baggageNumber, String flightNumber, String passengerName) {
        this.baggageNumber = baggageNumber;
        this.flightNumber = flightNumber;
        this.passengerName = passengerName;
        this.currentStatus = BaggageStatus.AT_DROP_OFF;
        this.lastMessage = BaggageStatus.AT_DROP_OFF.getDescription();
        this.lastUpdateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getBaggageNumber() { return baggageNumber; }
    public void setBaggageNumber(String baggageNumber) { this.baggageNumber = baggageNumber; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public BaggageStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(BaggageStatus currentStatus) {
        this.currentStatus = currentStatus;
        this.lastMessage = currentStatus.getDescription();
        this.lastUpdateTime = LocalDateTime.now();
    }
    public void setCurrentStatus(BaggageStatus currentStatus, String customMessage) {
        this.currentStatus = currentStatus;
        this.lastMessage = customMessage;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }

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
               '}';
    }
}