# Usage: ./run_client.sh [<server_hostname>]

java -cp ../Server/RPCInterface.jar:. Client.TCPClient $1
