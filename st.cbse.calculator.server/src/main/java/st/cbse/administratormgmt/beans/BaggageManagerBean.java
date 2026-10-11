package st.cbse.administratormgmt.beans;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import st.cbse.administratormgmt.interfaces.BaggageManagerRemote;
import st.cbse.baggagemgmt.data.Baggage; // Assuming Baggage entity
import st.cbse.baggagemgmt.data.BaggageDTO;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors; // For DTO conversion

@Stateless
public class BaggageManagerBean implements BaggageManagerRemote {

    private static final Logger LOGGER = Logger.getLogger(BaggageManagerBean.class.getName());

    @EJB
    private BaggageManagementLocal baggageManagement; // Inject existing BaggageManagement EJB

    @Override
    public List<BaggageDTO> getAllBaggageStatuses() {
        LOGGER.log(Level.INFO, "Retrieving all baggage statuses for Baggage Manager.");
        // This assumes BaggageManagementLocal has a method to get all baggage.
        // If not, you might need to add one there.
        // For example: List<Baggage> getAllBaggage();
        List<Baggage> allBaggage = baggageManagement.getAllBaggage(); // <--- ASSUMPTION: This method exists or will be added

        if (allBaggage == null || allBaggage.isEmpty()) {
            return List.of(); // Return empty list if no baggage found
        }
        
        // Convert Baggage entities to DTOs for client consumption
        return allBaggage.stream()
                         .map(BaggageDTO::new) // Assumes BaggageDTO has a constructor that takes Baggage entity
                         .collect(Collectors.toList());
    }

    @Override
    public List<BaggageDTO> searchBaggage(String flightNumber, String passengerName) {
        LOGGER.log(Level.INFO, "Searching baggage with Flight: {0}, Passenger: {1}", new Object[]{flightNumber, passengerName});
        
        // This assumes BaggageManagementLocal has a method to search baggage by these criteria.
        // If not, you might need to add one there.
        // Example: List<Baggage> searchBaggageByFlightAndPassenger(String flightNumber, String passengerName);
        List<Baggage> searchResults = baggageManagement.searchBaggageByFlightAndPassenger(flightNumber, passengerName); // <--- ASSUMPTION: This method exists or will be added

        if (searchResults == null || searchResults.isEmpty()) {
            return List.of();
        }

        return searchResults.stream()
                            .map(BaggageDTO::new)
                            .collect(Collectors.toList());
    }

    @Override
    public void callForInspection(String baggageNumber) {
        LOGGER.log(Level.INFO, "Dummy action: Calling for inspection for baggage: {0}", baggageNumber);
        // This is a dummy implementation as requested.
        // In a real scenario, this would trigger an actual inspection process,
        // update baggage status, send notifications, etc.
        // For example, you might update the baggage status via baggageManagement.updateBaggageStatus(baggageNumber, "IN_INSPECTION");
    }
}