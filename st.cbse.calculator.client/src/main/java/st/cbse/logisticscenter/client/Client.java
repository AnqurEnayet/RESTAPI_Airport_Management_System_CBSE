package st.cbse.logisticscenter.client;

import java.util.Properties;
import java.util.Scanner;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import st.cbse.flightmgmt.interfaces.FlightManagementRemote;
import st.cbse.logisticscenter.client.manager.FlightManagementClientManager;

public class Client {

    /* Remote JNDI entry that WildFly exported (no java: prefix) */
    private static final String JNDI_FLIGHT_MGMT =
            "st.cbse.logisticscenter.server/FlightManagementBean!"
          + "st.cbse.flightmgmt.interfaces.FlightManagementRemote";

    public static void main(String[] args) {

        Scanner scanner = null;
        InitialContext ctx = null;
        try {
            scanner = new Scanner(System.in);
            ctx     = createContext();
            System.out.println("JNDI context initialised.\n");

            int choice;
            do {
                showMainMenu();
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();        // consume newline

                switch (choice) {
                    case 1 -> runAirlineConsole(ctx, scanner);
                    case 0 -> System.out.println("Good-bye!");
                    default -> System.out.println("Invalid choice.");
                }
                System.out.println("\n-------------------------------------------------\n");
            } while (choice != 0);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (scanner != null) scanner.close();
            if (ctx != null)     try { ctx.close(); } catch (NamingException ignored) {}
        }
    }

    /* ------------------------------------------------------------------ */

    private static InitialContext createContext() throws NamingException {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY,
              "org.wildfly.naming.client.WildFlyInitialContextFactory");
        p.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        p.put("jboss.naming.client.ejb.context", "true");
        return new InitialContext(p);
    }

    @SuppressWarnings("unchecked")
    private static <T> T lookup(Context ctx, String name, Class<T> type)
            throws NamingException {
        System.out.println("Looking up EJB: " + name);
        return (T) ctx.lookup(name);
    }

    private static void showMainMenu() {
        System.out.println("--- Logistics Center Client ---");
        System.out.println("1. Enter as Airline");
        System.out.println("0. Exit");
    }

    /* ---------------- Airline workflow -------------------------------- */

    private static void runAirlineConsole(InitialContext ctx, Scanner sc) {
        System.out.println("\n--- Airline console ---");
        try {
            FlightManagementRemote bean =
                    lookup(ctx, JNDI_FLIGHT_MGMT, FlightManagementRemote.class);

            FlightManagementClientManager mgr =
                    new FlightManagementClientManager(bean);

            int opt;
            do {
                System.out.println("\n1. Display All Flights");
                System.out.println("2. Full Flight Operations Demo");
                System.out.println("0. Back");
                System.out.print("Choice: ");
                opt = sc.nextInt();
                sc.nextLine();

                switch (opt) {
                    case 1 -> mgr.displayAllFlights();
                    case 2 -> mgr.runFlightManagementDemo();
                    case 0 -> System.out.println("Returning to main menu.");
                    default -> System.out.println("Invalid choice.");
                }
            } while (opt != 0);

        } catch (Exception e) {
            System.err.println("Airline error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
