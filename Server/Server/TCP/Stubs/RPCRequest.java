package Server.TCP.Stubs;

import java.io.Serializable;

public record RPCRequest(String methodName, Class<?>[] parameterTypes, Object[] arguments) implements Serializable {

}
