#Usage: ./run_middleware.sh <flight_manager_host> <car_manager_host> <room_manager_host> <customer_manager_host> [<rm_name>]

java -Djava.rmi.server.codebase=file:"$(pwd)"/ Server.TCP.TCPMiddleware "$1" "$2" "$3" "$4" ${5:+"$5"}
