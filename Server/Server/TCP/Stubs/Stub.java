package Server.TCP.Stubs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class Stub {
    @SuppressWarnings("unchecked")
    public static <T> T generateStub(Class<T> interfaceClass, String host, int port) throws IOException {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new Handler(host, port)
        );
    }

    private static class Handler implements InvocationHandler {
        private final String host;
        private final int port;

        private Handler(String host, int port) throws IOException {
            this.host = host;
            this.port = port;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws IOException, ClassNotFoundException {
            try (Socket socket = new Socket(host, port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                out.writeObject(new RPCRequest(method.getName(), method.getParameterTypes(), args));
                RPCResponse response = (RPCResponse) in.readObject();
                if (response.exception() != null) {
                    throw new RPCException(response.exception());
                }
                return response.returnValue();
            }
        }
    }
}
