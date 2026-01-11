#Usage: ./run_server.sh <rm_type> [<rm_name>]

java -Djava.rmi.server.codebase=file:"$(pwd)"/ Server.TCP.TCPResourceManager "$1" ${2:+"$2"}