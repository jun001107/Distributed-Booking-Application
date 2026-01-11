#Usage: ./run_server.sh <rm_type> [<rmi_name>]

./run_rmi.sh > /dev/null 2>&1
java -Djava.rmi.server.codebase=file:"$(pwd)"/ Server.RMI.RMIResourceManager "$1" ${2:+"$2"}
