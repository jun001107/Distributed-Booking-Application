#Usage: ./run_middleware.sh <flight_manager_host> <car_manager_host> <room_manager_host> <customer_manager_host> [<rmi_name>]

./run_rmi.sh > /dev/null
java -Djava.rmi.server.codebase=file:"$(pwd)"/ Server.RMI.RMIMiddleware "$1" "$2" "$3" "$4" ${5:+"$5"}
