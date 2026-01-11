package Server.TCP;

import Server.Common.Trace;
import Server.Interface.*;
import Server.Middleware.Middleware;
import Server.TCP.Stubs.Stub;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPMiddleware {
    private static final int PORT = 1146;
    private static final String DEFAULT_SERVER_NAME = "Server";

    private final RPCServer<IResourceManager> rpcServer;

    public TCPMiddleware(IResourceManager resourceManager) {
        this.rpcServer = new RPCServer<>(resourceManager);
    }

    public static void main(String[] args) throws IOException {
        String flightManagerHost = args[0];
        String carManagerHost = args[1];
        String roomManagerHost = args[2];
        String customerManagerHost = args[3];
        String serverName = args.length > 4 ? args[4] : DEFAULT_SERVER_NAME;

        // Generate stubs
        Trace.info("Generating stubs...");
        IFlightManager flightManagerStub = Stub.generateStub(IFlightManager.class, flightManagerHost, TCPResourceManager.PORT);
        ICarManager carManagerStub = Stub.generateStub(ICarManager.class, carManagerHost, TCPResourceManager.PORT);
        IRoomManager roomManagerStub = Stub.generateStub(IRoomManager.class, roomManagerHost, TCPResourceManager.PORT);
        ICustomerManager customerManagerStub = Stub.generateStub(ICustomerManager.class, customerManagerHost, TCPResourceManager.PORT);

        // Set up middleware
        Trace.info("Setting up middleware...");
        IResourceManager middleware = new Middleware(serverName, flightManagerStub, carManagerStub, roomManagerStub, customerManagerStub);

        // Create server
        Trace.info("Creating server...");
        TCPMiddleware middlewareServer = new TCPMiddleware(middleware);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                middlewareServer.stop();
                System.out.println("'" + serverName + "' TCP middleware server stopped");
            } catch (Exception e) {
                System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
                e.printStackTrace();
            }
        }));

        // Start server and wait to serve requests
        Trace.info("Starting server...");
        middlewareServer.start(PORT);
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
