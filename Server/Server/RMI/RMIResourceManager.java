// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.RMI;

import Server.Common.CarManager;
import Server.Common.CustomerManager;
import Server.Common.FlightManager;
import Server.Common.RoomManager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIResourceManager {
    public static final String RMI_PREFIX = "group_46_";
    public static final int SERVER_PORT = 1046;
    private static final String DEFAULT_SERVER_NAME = "Server";

    public static void main(String[] args) {
        String resourceManagerType = args[0];
        String serverName = args.length > 1 ? args[1] : DEFAULT_SERVER_NAME;

        // Create the RMI server entry
        try {
            // Create a new Server object
            Remote server = switch (resourceManagerType) {
                case "Flights" -> new FlightManager(serverName);
                case "Rooms" -> new RoomManager(serverName);
                case "Cars" -> new CarManager(serverName);
                case "Customers" -> new CustomerManager(serverName);
                default -> throw new IllegalStateException("Unexpected value: " + resourceManagerType);
            };

            // Dynamically generate the stub (client proxy)
            Remote resourceManager = UnicastRemoteObject.exportObject(server, 0);

            // Bind the remote object's stub in the registry; adjust port if appropriate
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(SERVER_PORT);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(SERVER_PORT);
            }
            final Registry registry = l_registry;
            registry.rebind(RMI_PREFIX + serverName, resourceManager);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    registry.unbind(RMI_PREFIX + serverName);
                    System.out.println("'" + serverName + "' resource manager unbound");
                } catch (Exception e) {
                    System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
                    e.printStackTrace();
                }
            }));
            System.out.println("'" + serverName + "' resource manager server ready and bound to '" + RMI_PREFIX + serverName + "'");
        } catch (Exception e) {
            System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
