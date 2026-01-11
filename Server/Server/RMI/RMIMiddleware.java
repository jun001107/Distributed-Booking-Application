package Server.RMI;

import Server.Interface.*;
import Server.Middleware.Middleware;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIMiddleware {
    public static final String RMI_PREFIX = "group_46_";
    public static final int SERVER_PORT = 1046;
    private static final String DEFAULT_SERVER_NAME = "Server";

    public static void main(String[] args) {
        String flightManagerHost = args[0];
        String carManagerHost = args[1];
        String roomManagerHost = args[2];
        String customerManagerHost = args[3];
        String serverName = args.length > 4 ? args[4] : DEFAULT_SERVER_NAME;

        // Create the RMI server entry
        try {
            Registry flightRegistry = LocateRegistry.getRegistry(flightManagerHost, RMIResourceManager.SERVER_PORT);
            Registry carRegistry = LocateRegistry.getRegistry(carManagerHost, RMIResourceManager.SERVER_PORT);
            Registry roomRegistry = LocateRegistry.getRegistry(roomManagerHost, RMIResourceManager.SERVER_PORT);
            Registry customerRegistry = LocateRegistry.getRegistry(customerManagerHost, RMIResourceManager.SERVER_PORT);

            IFlightManager flightManagerStub = (IFlightManager) flightRegistry.lookup(RMIResourceManager.RMI_PREFIX + "Flights");
            ICarManager carManagerStub = (ICarManager) carRegistry.lookup(RMIResourceManager.RMI_PREFIX + "Cars");
            IRoomManager roomManagerStub = (IRoomManager) roomRegistry.lookup(RMIResourceManager.RMI_PREFIX + "Rooms");
            ICustomerManager customerManagerStub = (ICustomerManager) customerRegistry.lookup(RMIResourceManager.RMI_PREFIX + "Customers");

            // Create a new Server object
            Middleware middlewareServer = new Middleware(serverName, flightManagerStub, carManagerStub, roomManagerStub, customerManagerStub);

            // Dynamically generate the stub (client proxy)
            IResourceManager middlewareStub = (IResourceManager) UnicastRemoteObject.exportObject(middlewareServer, 0);

            // Bind the remote object's stub in the registry; adjust port if appropriate
            Registry l_registry;
            try {
                l_registry = LocateRegistry.createRegistry(SERVER_PORT);
            } catch (RemoteException e) {
                l_registry = LocateRegistry.getRegistry(SERVER_PORT);
            }
            final Registry registry = l_registry;
            registry.rebind(RMI_PREFIX + serverName, middlewareStub);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    registry.unbind(RMI_PREFIX + serverName);
                    System.out.println("'" + serverName + "' middleware unbound");
                } catch (Exception e) {
                    System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
                    e.printStackTrace();
                }
            }));
            System.out.println("'" + serverName + "' middleware server ready and bound to '" + RMI_PREFIX + serverName + "'");
        } catch (Exception e) {
            System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
