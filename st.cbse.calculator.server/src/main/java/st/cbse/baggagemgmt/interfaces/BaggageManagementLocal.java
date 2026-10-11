package st.cbse.baggagemgmt.interfaces;

import jakarta.ejb.Local;
import st.cbse.baggagemgmt.data.Baggage;
import java.util.List; // ADDED: Import List

@Local // Keep this annotation!
public interface BaggageManagementLocal {
    String startBaggageProcess(String baggageNumber, String flightNumber, String passengerName);
    Baggage getBaggageStatus(String baggageNumber);
    void notifyBaggageArrival(String baggageNumber, String stationName);
    void notifyBaggageCompletion(String baggageNumber, String stationName);

    // ADDED: New methods to support BaggageManagerBean's requirements
    /**
     * Retrieves all active baggage items in the system.
     * @return A list of Baggage objects.
     */
    List<Baggage> getAllBaggage();

    /**
     * Searches for baggage items by flight number and/or passenger name.
     * @param flightNumber The flight number to search by (can be null/empty).
     * @param passengerName The passenger name to search by (can be null/empty).
     * @return A list of matching Baggage objects.
     */
    List<Baggage> searchBaggageByFlightAndPassenger(String flightNumber, String passengerName);
}