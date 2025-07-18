package st.cbse.baggagemgmt.beans;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageStatus;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;
import st.cbse.baggagemgmt.interfaces.SortingStationLocal;

import java.util.logging.Logger;

@Stateless
public class SortingStationBean implements SortingStationLocal {

    private static final Logger LOGGER = Logger.getLogger(SortingStationBean.class.getName());
    private static final long PROCESSING_DELAY_MS = 7000; // 7 seconds delay for sorting

    @EJB
    private BaggageManagementLocal processControl;

    @Asynchronous
    @Override
    public void processBaggage(Baggage baggage) {
        String baggageNumber = baggage.getBaggageNumber();
        LOGGER.info("Baggage " + baggageNumber + " is being sorted at Sorting Station.");

        try {
            // Simulate sorting processing
            Thread.sleep(PROCESSING_DELAY_MS);
        } catch (InterruptedException e) {
            LOGGER.warning("Sorting for baggage " + baggageNumber + " interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.ERROR.toString());
            return;
        }

        LOGGER.info("Baggage " + baggageNumber + " completed Sorting. Notifying ProcessControl.");
        processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.SORTING_COMPLETED.name());
    }
}