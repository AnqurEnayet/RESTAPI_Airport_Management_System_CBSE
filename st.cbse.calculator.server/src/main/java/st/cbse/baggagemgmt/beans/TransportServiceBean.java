package st.cbse.baggagemgmt.beans;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageStatus;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;
import st.cbse.baggagemgmt.interfaces.TransportServiceLocal;

import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
public class TransportServiceBean implements TransportServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(TransportServiceBean.class.getName());
    private static final long TRANSPORT_DELAY_MS = 5000; // 5 seconds delay for transport

    @EJB
    private BaggageManagementLocal processControl; // To notify ProcessControl of arrival

    @Asynchronous // This method will run in a separate thread, not blocking the caller
    @Override
    public Future<Void> startTransport(Baggage baggage, BaggageStatus destinationStatus) {
        String baggageNumber = baggage.getBaggageNumber();
        LOGGER.info("Baggage " + baggageNumber + " is now in transport to " + destinationStatus.getDescription());

        // Simulate transport delay
        try {
            Thread.sleep(TRANSPORT_DELAY_MS);
        } catch (InterruptedException e) {
            LOGGER.warning("Transport for baggage " + baggageNumber + " interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.ERROR.toString()); // Notify error
            return new AsyncResult<>(null);
        }

        // Notify ProcessControl that baggage has arrived at its destination
        LOGGER.info("Baggage " + baggageNumber + " arrived at " + destinationStatus.getDescription());
        processControl.notifyBaggageArrival(baggageNumber, destinationStatus.name());

        return new AsyncResult<>(null);
    }
}