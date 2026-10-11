package st.cbse.baggagemgmt.beans;

import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageStatus;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;
import st.cbse.baggagemgmt.interfaces.TransportServiceLocal;
import st.cbse.baggagemgmt.interfaces.SecurityStationLocal;
import st.cbse.baggagemgmt.interfaces.SortingStationLocal;
import st.cbse.baggagemgmt.interfaces.CbrStationLocal;
import st.cbse.baggagemgmt.interfaces.LoadingStationLocal;

import java.time.LocalDateTime; // No change in import, but used in Baggage for timestamping
import java.util.ArrayList;   // ADDED: Import ArrayList for getAllBaggage
import java.util.Collections; // REMOVED: Not needed anymore for EnumSet
import java.util.EnumSet;     // REMOVED: Not needed anymore for EnumSet
import java.util.List;        // ADDED: Import List for new methods
import java.util.Map;
import java.util.Set;         // REMOVED: Not needed anymore for EnumSet
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors; // ADDED: For stream operations in search method

@Singleton
@Lock(LockType.READ) // Default to READ lock for general access
public class ProcessControlBean implements BaggageManagementLocal {

    private static final Logger LOGGER = Logger.getLogger(ProcessControlBean.class.getName());

    // Injected EJBs for our subcomponents
    @EJB
    private TransportServiceLocal transportService;
    @EJB
    private SecurityStationLocal securityStation;
    @EJB
    private SortingStationLocal sortingStation;
    @EJB
    private CbrStationLocal cbrStation;
    @EJB
    private LoadingStationLocal loadingStation;

    // Map to hold the current status of all active baggage items
    // Using ConcurrentHashMap for thread-safe access
    private final Map<String, Baggage> activeBaggage = new ConcurrentHashMap<>();

    // REMOVED: The REQUIRED_STATIONS set is no longer needed for this simplified completion check
    // private static final Set<BaggageStatus> REQUIRED_STATIONS = Collections.unmodifiableSet(EnumSet.of(
    //     BaggageStatus.SECURITY_CLEARED,
    //     BaggageStatus.SORTING_COMPLETED,
    //     BaggageStatus.CBR_CLEARED,
    //     BaggageStatus.LOADED_ON_PLANE
    // ));

    @Lock(LockType.WRITE) // Use WRITE lock for state-modifying operations
    @Override
    public String startBaggageProcess(String baggageNumber, String flightNumber, String passengerName) {
        if (activeBaggage.containsKey(baggageNumber)) {
            LOGGER.warning("Baggage " + baggageNumber + " already in process.");
            return "Baggage " + baggageNumber + " is already being processed.";
        }

        Baggage newBaggage = new Baggage(baggageNumber, flightNumber, passengerName);
        activeBaggage.put(baggageNumber, newBaggage);
        LOGGER.info("Baggage " + baggageNumber + " (Flight: " + flightNumber + ") dropped off. Starting process.");

        // Initiate the first transport to Security Station
        // Baggage's constructor already sets AT_DROP_OFF, and setCurrentStatus adds to history.
        newBaggage.setCurrentStatus(BaggageStatus.IN_TRANSPORT_TO_SECURITY);
        transportService.startTransport(newBaggage, BaggageStatus.AT_SECURITY_CHECK); // Tell transport where it's going next

        return "Baggage " + baggageNumber + " process started successfully.";
    }

    @Override
    public Baggage getBaggageStatus(String baggageNumber) {
        Baggage baggage = activeBaggage.get(baggageNumber);
        if (baggage == null) {
            LOGGER.info("Baggage " + baggageNumber + " not found or already completed/removed.");
        }
        return baggage;
    }

    // ADDED: Implementation for BaggageManagementLocal.getAllBaggage()
    @Override
    public List<Baggage> getAllBaggage() {
        // Return a new ArrayList to avoid ConcurrentModificationException if activeBaggage is modified
        // while iterating elsewhere, and to prevent external modification of the internal map.
        return new ArrayList<>(activeBaggage.values());
    }

    // ADDED: Implementation for BaggageManagementLocal.searchBaggageByFlightAndPassenger()
    @Override
    public List<Baggage> searchBaggageByFlightAndPassenger(String flightNumber, String passengerName) {
        return activeBaggage.values().stream()
                .filter(baggage -> {
                    // Check if flightNumber matches (case-insensitive, partial match) or if filter is empty
                    boolean matchesFlight = (flightNumber == null || flightNumber.isEmpty()) ||
                                            (baggage.getFlightNumber() != null && baggage.getFlightNumber().toLowerCase().contains(flightNumber.toLowerCase()));
                    // Check if passengerName matches (case-insensitive, partial match) or if filter is empty
                    boolean matchesPassenger = (passengerName == null || passengerName.isEmpty()) ||
                                               (baggage.getPassengerName() != null && baggage.getPassengerName().toLowerCase().contains(passengerName.toLowerCase()));
                    return matchesFlight && matchesPassenger;
                })
                .collect(Collectors.toList());
    }

    @Lock(LockType.WRITE)
    @Override
    public void notifyBaggageArrival(String baggageNumber, String stationName) {
        Baggage baggage = activeBaggage.get(baggageNumber);
        if (baggage == null) {
            LOGGER.warning("Arrival notification for unknown or completed baggage: " + baggageNumber);
            return;
        }

        LOGGER.info("Baggage " + baggageNumber + " arrived at " + stationName + ". Current status: " + baggage.getCurrentStatus());

        // Based on where it arrived, direct it to the next step
        // setCurrentStatus methods internally update the statusHistory
        switch (stationName) {
            case "AT_SECURITY_CHECK":
                baggage.setCurrentStatus(BaggageStatus.AT_SECURITY_CHECK);
                securityStation.processBaggage(baggage);
                break;
            case "AT_SORTING_STATION":
                baggage.setCurrentStatus(BaggageStatus.AT_SORTING_STATION);
                sortingStation.processBaggage(baggage);
                break;
            case "AT_CBR_STATION":
                baggage.setCurrentStatus(BaggageStatus.AT_CBR_STATION);
                cbrStation.processBaggage(baggage);
                break;
            case "AT_LOADING_STATION":
                baggage.setCurrentStatus(BaggageStatus.AT_LOADING_STATION);
                loadingStation.processBaggage(baggage);
                break;
            default:
                LOGGER.warning("Unknown station arrival notification: " + stationName + " for baggage " + baggageNumber);
                baggage.setCurrentStatus(BaggageStatus.ERROR, "Unknown station arrival: " + stationName);
                break;
        }
    }

    @Lock(LockType.WRITE)
    @Override
    public void notifyBaggageCompletion(String baggageNumber, String stationName) {
        Baggage baggage = activeBaggage.get(baggageNumber);
        if (baggage == null) {
            LOGGER.warning("Completion notification for unknown or completed baggage: " + baggageNumber);
            return;
        }

        LOGGER.info("Baggage " + baggageNumber + " completed processing at " + stationName + ".");

        // Based on which station completed, direct to next transport or final step
        // setCurrentStatus methods internally update the statusHistory
        switch (stationName) {
            case "SECURITY_CLEARED":
                baggage.setCurrentStatus(BaggageStatus.SECURITY_CLEARED);
                transportService.startTransport(baggage, BaggageStatus.AT_SORTING_STATION);
                break;
            case "SORTING_COMPLETED":
                baggage.setCurrentStatus(BaggageStatus.SORTING_COMPLETED);
                transportService.startTransport(baggage, BaggageStatus.AT_CBR_STATION);
                break;
            case "CBR_CLEARED":
                baggage.setCurrentStatus(BaggageStatus.CBR_CLEARED);
                transportService.startTransport(baggage, BaggageStatus.AT_LOADING_STATION);
                break;
            case "LOADED_ON_PLANE": // Final processing station's completion
                baggage.setCurrentStatus(BaggageStatus.LOADED_ON_PLANE);
                // For simplicity, we directly mark as COMPLETED after loading
                baggage.setCurrentStatus(BaggageStatus.COMPLETED, "Baggage process fully completed.");
                LOGGER.info("Baggage " + baggageNumber + " has completed its full journey.");
                // Optionally remove from activeBaggage map if no further tracking needed
                // activeBaggage.remove(baggageNumber);
                break;
            default:
                LOGGER.warning("Unknown station completion notification: " + stationName + " for baggage " + baggageNumber);
                baggage.setCurrentStatus(BaggageStatus.ERROR, "Unknown station completion: " + stationName);
                break;
        }
    }
}