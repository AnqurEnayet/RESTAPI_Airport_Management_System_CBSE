package st.cbse.baggagemgmt.beans;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageStatus;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;
import st.cbse.baggagemgmt.interfaces.SecurityStationLocal;

import java.util.logging.Logger;

@Stateless
public class SecurityStationBean implements SecurityStationLocal {

    private static final Logger LOGGER = Logger.getLogger(SecurityStationBean.class.getName());
    private static final long PROCESSING_DELAY_MS = 5000; // 5 seconds delay for security check

    @EJB
    private BaggageManagementLocal processControl; // To notify ProcessControl of completion

    @Asynchronous // This method will run in a separate thread
    @Override
    public void processBaggage(Baggage baggage) {
        String baggageNumber = baggage.getBaggageNumber();
        LOGGER.info("Baggage " + baggageNumber + " is going through Security Station.");

        try {
            // Simulate security check processing
            Thread.sleep(PROCESSING_DELAY_MS);
        } catch (InterruptedException e) {
            LOGGER.warning("Security check for baggage " + baggageNumber + " interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.ERROR.toString()); // Notify error
            return;
        }

        LOGGER.info("Baggage " + baggageNumber + " completed Security Check. Notifying ProcessControl.");
        // Notify ProcessControl that security check is completed
        processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.SECURITY_CLEARED.name());
    }
}