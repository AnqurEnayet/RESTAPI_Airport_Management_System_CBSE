package st.cbse.baggagemgmt.interfaces;

import jakarta.ejb.Local;
import st.cbse.baggagemgmt.data.Baggage;

@Local // Keep this annotation!
public interface SecurityStationLocal {
    void processBaggage(Baggage baggage);
}