package st.cbse.baggagemgmt.beans;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageStatus;
import st.cbse.baggagemgmt.interfaces.BaggageManagementLocal;
import st.cbse.baggagemgmt.interfaces.CbrStationLocal;

import java.util.logging.Logger;

@Stateless
public class CbrStationBean implements CbrStationLocal {

    private static final Logger LOGGER = Logger.getLogger(CbrStationBean.class.getName());
    private static final long PROCESSING_DELAY_MS = 6000; // 6 seconds delay for CBR

    @EJB
    private BaggageManagementLocal processControl;

    @Asynchronous
    @Override
    public void processBaggage(Baggage baggage) {
        String baggageNumber = baggage.getBaggageNumber();
        LOGGER.info("Baggage " + baggageNumber + " is going through CBR Station.");

        try {
            // Simulate CBR processing
            Thread.sleep(PROCESSING_DELAY_MS);
        } catch (InterruptedException e) {
            LOGGER.warning("CBR check for baggage " + baggageNumber + " interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.ERROR.toString());
            return;
        }

        LOGGER.info("Baggage " + baggageNumber + " completed CBR. Notifying ProcessControl.");
        processControl.notifyBaggageCompletion(baggageNumber, BaggageStatus.CBR_CLEARED.name());
    }
}