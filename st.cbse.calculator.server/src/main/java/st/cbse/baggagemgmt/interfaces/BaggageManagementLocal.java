package st.cbse.baggagemgmt.interfaces;

import jakarta.ejb.Local;
import st.cbse.baggagemgmt.data.Baggage;

@Local // Keep this annotation!
public interface BaggageManagementLocal {
    String startBaggageProcess(String baggageNumber, String flightNumber, String passengerName);
    Baggage getBaggageStatus(String baggageNumber);
    void notifyBaggageArrival(String baggageNumber, String stationName);
    void notifyBaggageCompletion(String baggageNumber, String stationName);
}