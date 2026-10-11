package st.cbse.administratormgmt.interfaces;

import jakarta.ejb.Remote;
import st.cbse.baggagemgmt.data.BaggageDTO; // Assuming you have a BaggageDTO for transfer

import java.util.List;

@Remote
public interface BaggageManagerRemote {

    /**
     * Retrieves a list of all baggage and their current status information.
     * @return A list of BaggageDTOs.
     */
    List<BaggageDTO> getAllBaggageStatuses();

    /**
     * Searches for specific baggage items based on flight number and passenger name.
     * @param flightNumber The flight number to search by (can be null/empty for no filter).
     * @param passengerName The passenger name to search by (can be null/empty for no filter).
     * @return A list of matching BaggageDTOs.
     */
    List<BaggageDTO> searchBaggage(String flightNumber, String passengerName);

    /**
     * Initiates a dummy "Call for Inspection" action for a given baggage number.
     * This is a placeholder for future logic.
     * @param baggageNumber The baggage number to call for inspection.
     */
    void callForInspection(String baggageNumber);
}