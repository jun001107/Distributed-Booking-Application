all: compile-client

../Server/RPCInterface.jar: $(wildcard ../Server/Server/Interface/*.java) $(wildcard ../Server/Server/TCP/Stubs/*.java)
	make -C ../Server/ RPCInterface.jar

compile-client: ../Server/RPCInterface.jar $(wildcard Client/*.java)
	javac -cp ../Server/RPCInterface.jar Client/*.java

clean:
	rm -f Client/*.class
	rm -f ../Server/RPCInterface.jar
