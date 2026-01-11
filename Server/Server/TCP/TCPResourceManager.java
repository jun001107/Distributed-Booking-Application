package Server.TCP;

import Server.Common.*;
import Server.Interface.IBaseResourceManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPResourceManager {
    public static final int PORT = 1146;
    private static final String DEFAULT_SERVER_NAME = "Server";

    private final RPCServer<IBaseResourceManager> rpcServer;

    public TCPResourceManager(IBaseResourceManager resourceManager) {
        this.rpcServer = new RPCServer<>(resourceManager);
    }

    public static void main(String[] args) throws IOException {
        String resourceManagerType = args[0];
        String serverName = args.length > 1 ? args[1] : DEFAULT_SERVER_NAME;

        IBaseResourceManager resourceManager = switch (resourceManagerType) {
            case "Flights" -> new FlightManager(serverName);
            case "Rooms" -> new RoomManager(serverName);
            case "Cars" -> new CarManager(serverName);
            case "Customers" -> new CustomerManager(serverName);
            default -> throw new IllegalStateException("Unexpected value: " + resourceManagerType);
        };

        Trace.info("Creating resource manager server...");
        TCPResourceManager resourceManagerServer = new TCPResourceManager(resourceManager);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                resourceManagerServer.stop();
                System.out.println("'" + serverName + "' TCP resource manager stopped");
            } catch (Exception e) {
                System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
                e.printStackTrace();
            }
        }));

        Trace.info("Starting server...");
        resourceManagerServer.start(PORT);
    }

    public void start(int port) throws IOException {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            this.rpcServer.serve(port, executorService);
        }
    }

    public void stop() {
        this.rpcServer.stop();
    }
}
