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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

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