# Usage: ./run_client.sh [<server_hostname>]sh [<server_rmiobject>]

java -cp ../Server/RMIInterface.jar:. Client.RMIClient $1 $2
