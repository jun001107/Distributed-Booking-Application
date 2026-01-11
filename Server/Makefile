all: compile-server

RPCInterface.jar: $(wildcard Server/Interface/*.java) $(wildcard ../Server/Server/TCP/Stubs/*.java)
	@echo "Compiling RMI server interface"
	javac Server/Interface/*.java
	jar cvf RPCInterface.jar Server/Interface/*.class Server/TCP/Stubs/*.class

compile-server: $(wildcard Server/RMI/*.java) $(wildcard Server/Interface/*.java) $(wildcard Server/Common/*.java) $(wildcard Server/Middleware/*.java) $(wildcard Server/TCP/*.java)
	javac Server/RMI/*.java Server/Interface/*.java Server/Common/*.java Server/Middleware/*.java Server/TCP/*.java

clean:
	rm -f Server/Interface/*.class Server/Common/*.class Server/RMI/*.class Server/Middleware/*.class Server/TCP/*.class Server/TCP/Stubs/*.class
	rm -f RPCInterface.jar
