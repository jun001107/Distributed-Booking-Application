package Server.TCP.Stubs;

import java.io.Serializable;

public record RPCResponse(Object returnValue, Exception exception) implements Serializable {

}
