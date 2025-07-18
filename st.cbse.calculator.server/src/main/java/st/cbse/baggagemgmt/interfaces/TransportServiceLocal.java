package st.cbse.baggagemgmt.interfaces;

import jakarta.ejb.Local;
import st.cbse.baggagemgmt.data.Baggage;
import st.cbse.baggagemgmt.data.BaggageStatus;

import java.util.concurrent.Future; // ADDED THIS IMPORT

@Local
public interface TransportServiceLocal {
    // CHANGED return type from 'void' to 'Future<Void>'
    Future<Void> startTransport(Baggage baggage, BaggageStatus destinationStatus);
}