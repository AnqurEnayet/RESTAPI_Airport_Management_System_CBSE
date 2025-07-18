package st.cbse.baggagemgmt.data;

public enum BaggageStatus {
    AT_DROP_OFF("At Baggage Drop-off"),
    IN_TRANSPORT_TO_SECURITY("In transport to Security Check"),
    AT_SECURITY_CHECK("At Security Check"),
    SECURITY_CLEARED("Security Check Completed"),
    IN_TRANSPORT_TO_SORTING("In transport to Sorting Station"),
    AT_SORTING_STATION("At Sorting Station"),
    SORTING_COMPLETED("Sorting Completed"),
    IN_TRANSPORT_TO_CBR("In transport to CBR (Customs Border Review)"),
    AT_CBR_STATION("At CBR Station"),
    CBR_CLEARED("CBR Completed"),
    IN_TRANSPORT_TO_LOADING("In transport to Loading Station"),
    AT_LOADING_STATION("At Loading Station"),
    LOADED_ON_PLANE("Loaded onto Plane"),
    COMPLETED("Process Completed"), // Final state for a fully processed bag
    ERROR("Error in processing");

    private final String description;

    BaggageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}