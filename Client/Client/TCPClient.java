package Client;

import Server.Interface.IResourceManager;
import Server.TCP.Stubs.Stub;

import java.io.IOException;

public class TCPClient extends Client {
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1146;

    private final String host;
    private final int port;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        String serverHost = args.length > 0 ? args[0] : DEFAULT_SERVER_HOST;
        if (args.length > 1) {
            System.err.println((char) 27 + "[31;1mClient exception: " + (char) 27 + "[0mUsage: java client.TCP [server_hostname]");
            System.exit(1);
        }

        Client tcpClient = new TCPClient(serverHost, SERVER_PORT);
        tcpClient.connectServer();
        tcpClient.start();
    }

    @Override
    public void connectServer() {
        try {
            m_resourceManager = Stub.generateStub(IResourceManager.class, host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
