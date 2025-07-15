package st.cbse.logisticscenter.client.manager;

import java.time.LocalDateTime;
import java.util.List;

import st.cbse.flightmgmt.data.FlightDTO;
import st.cbse.flightmgmt.interfaces.FlightManagementRemote;

public class FlightManagementClientManager {

    private final FlightManagementRemote flightBean;

    public FlightManagementClientManager(FlightManagementRemote flightBean) {
        this.flightBean = flightBean;
    }

    /* ---------------------------------------------------------------------- */

    public void displayAllFlights() {
        System.out.println("\n--- Displaying All Flights ---");
        try {
            List<FlightDTO> flights = flightBean.getAllFlights();
            if (flights.isEmpty()) {
                System.out.println("No flights found.");
            } else {
                flights.forEach(System.out::println);
            }
        } catch (Exception ex) {
            System.err.println("Error listing flights: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("--- End Flight List ---");
    }

    /* ---------------------------------------------------------------------- */

    public void runFlightManagementDemo() {
        System.out.println("\n--- Flight Management Demo ---");
        try {
            // 1. create
            FlightDTO flight = new FlightDTO(
                    null, "LH1234", "FRA", "MUC",
                    LocalDateTime.of(2025, 8, 1, 10, 0),
                    LocalDateTime.of(2025, 8, 1, 11, 0),
                    150, 99.99);
            flight = flightBean.createFlight(flight);
            System.out.println("Created → " + flight);

            // 2. list
            displayAllFlights();

            // 3. update
            flight.setPrice(129.50);
            flight.setAvailableSeats(120);
            flight = flightBean.updateFlight(flight);
            System.out.println("Updated → " + flight);

            // 4. search
            List<FlightDTO> search = flightBean.searchFlights("FRA", null, null);
            System.out.println("Search from FRA:");
            search.forEach(System.out::println);

            // 5. delete
            //flightBean.deleteFlight(flight.getFlightId());
            //System.out.println("Deleted flight ID " + flight.getFlightId());

        } catch (Exception ex) {
            System.err.println("Demo error: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("--- Demo finished ---");
    }
}
