package Server.TCP;

import Server.Common.Trace;
import Server.TCP.Stubs.RPCRequest;
import Server.TCP.Stubs.RPCResponse;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

public class RPCServer<T> {
    private final T implementation;
    private boolean stopped;

    public RPCServer(T implementation) {
        this.implementation = implementation;
    }

    public RPCResponse handleRequest(RPCRequest request) {
        try {
            String methodName = request.methodName();
            Method method = implementation.getClass().getMethod(methodName, request.parameterTypes());
            Object returnValue = method.invoke(implementation, request.arguments());
            return new RPCResponse(returnValue, null);
        } catch (Exception e) {
            return new RPCResponse(null, e);
        }
    }

    public void serve(int port, Executor executor) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!stopped) {
                Trace.info("Waiting for new connection on port " + port + "...");
                Socket clientSocket = serverSocket.accept();
                Trace.info("Accepted connection from " + clientSocket.getRemoteSocketAddress() + "...");
                executor.execute(() -> {
                    try {
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                        while (!stopped) {
                            RPCRequest request = (RPCRequest) in.readObject();
                            out.writeObject(handleRequest(request));
                        }
                    } catch (EOFException ignored) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public void stop() {
        stopped = true;
    }
}
