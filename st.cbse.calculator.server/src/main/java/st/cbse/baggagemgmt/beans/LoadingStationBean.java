package st.cbse.baggagemgmt.beans;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageStatus;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;
import st.cbse.baggagemgmt.interfaces.LoadingStationLocal;

import java.util.logging.Logger;

@Stateless
public class LoadingStationBean implements LoadingStationLocal {

    private static final Logger LOGGER = Logger.getLogger(LoadingStationBean.class.getName());
    private static final long PROCESSING_DELAY_MS = 4000; // 4 seconds delay for loading

    @EJB
    private BaggageManagementLocal processControl;

    @Asynchronous
    @Override
    public void processBaggage(Baggage baggage) {
        String baggageNumber = baggage.getBaggageNumber();
        LOGGER.info("Baggage " + baggageNumber + " is being loaded onto the plane at Loading Station.");

        try {
            // Simulate loading processing
            Thread.sleep(PROCESSING_DELAY_MS);
        } catch (InterruptedException e) {
            LOGGER.warning("Loading for baggage " + baggageNumber + " interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.ERROR.toString());
            return;
        }

        LOGGER.info("Baggage " + baggageNumber + " completed Loading. Notifying ProcessControl.");
        processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.LOADED_ON_PLANE.name());
    }
}