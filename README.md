# Distributed Travel Reservation System (COMP 512 - Programming Assignment 1)

Travel reservation service built for COMP 512. The system exposes flights, cars, rooms, and customers as distributed resource managers behind a middleware facade. It supports two transports:
- Java RMI (rmiregistry on port 1046 with the `group_46_` name prefix)
- A custom TCP RPC layer (port 1146) using dynamic proxies and reflection

All state is in-memory; restarting a process clears its data.

## Architecture
- **Resource managers**: Dedicated processes for Flights, Cars, Rooms, and Customers (`Server/Common`, `Server/Interface`). Each uses a read/write lock around an in-memory map.
- **Middleware**: Aggregates the four managers and exposes the full `IResourceManager` API. The `bundle` call performs best-effort rollback if any individual reservation fails.
- **Transports**: RMI servers (`Server/RMI`) and TCP servers (`Server/TCP`) wrap the middleware and resource managers. CLI clients exist for each transport.
- **Concurrency**: Read/write locks guard resource maps; reservations are tracked per customer and released on customer deletion.

## Repository Layout
- `Server/` – server code, interfaces, middleware, and run scripts
  - `Common/` – in-memory data structures and concrete resource managers
  - `Interface/` – RMI/TCP shared interfaces
  - `Middleware/` – coordination layer across managers
  - `RMI/` – RMI entrypoints
  - `TCP/` – custom RPC server/stubs
  - `run_*.sh` – helper scripts to start managers and middleware
- `Client/` – CLI clients (`RMIClient`, `TCPClient`) and command parser

## Prerequisites
- JDK 21+ (records and virtual threads are used)
- `make`
- `tmux` and `ssh` if you use the multi-host helper scripts
- Open ports: 1046 (RMI) and 1146 (TCP)

## Build
```bash
cd Server
make              # builds servers and RPCInterface.jar

cd ../Client
make              # builds clients against the generated jar
# make clean      # remove build artifacts
```

## Running (Java RMI)
1) Start resource managers (same host is fine; each binds to `group_46_<name>` on port 1046):
```bash
cd Server
./run_server.sh Flights Flights
./run_server.sh Cars Cars
./run_server.sh Rooms Rooms
./run_server.sh Customers Customers
```
2) Start middleware (binds to `group_46_<rmi_name>`, default `Server`):
```bash
./run_middleware.sh <flight_host> <car_host> <room_host> <customer_host> [<rmi_name>]
```
3) Start the client:
```bash
cd Client
./run_client.sh [<middleware_host> [<middleware_rmi_name>]]
```
4) Convenience launcher: `Server/run_servers.sh` spins up the full stack via tmux/ssh; update the `MACHINES` array before using it.

## Running (Custom TCP RPC)
> Each TCP process binds to port 1146; run managers on different hosts (or change the port in code) to avoid conflicts.

1) Start the resource managers:
```bash
cd Server
./run_tcp_server.sh Flights Flights   # host A
./run_tcp_server.sh Cars Cars         # host B
./run_tcp_server.sh Rooms Rooms       # host C
./run_tcp_server.sh Customers Customers # host D
```
2) Start middleware:
```bash
./run_tcp_middleware.sh <flight_host> <car_host> <room_host> <customer_host> [<name>]
```
3) Start the client:
```bash
cd Client
./run_tcp_client.sh [<middleware_host>]
```
4) Convenience launcher: `Server/run_tcp_servers.sh` uses tmux/ssh; set the `MACHINES` array to your hosts.

## Client Commands
Enter commands as comma-separated values; `help` and `help,<Command>` are built in.
- `AddFlight,<FlightNumber>,<NumSeats>,<Price>`
- `AddCars,<Location>,<NumCars>,<Price>`
- `AddRooms,<Location>,<NumRooms>,<Price>`
- `AddCustomer` | `AddCustomerID,<CustomerID>`
- `DeleteFlight,<FlightNumber>` | `DeleteCars,<Location>` | `DeleteRooms,<Location>` | `DeleteCustomer,<CustomerID>`
- `QueryFlight,<FlightNumber>` | `QueryCars,<Location>` | `QueryRooms,<Location>` | `QueryCustomer,<CustomerID>`
- `QueryFlightPrice,<FlightNumber>` | `QueryCarsPrice,<Location>` | `QueryRoomsPrice,<Location>`
- `ReserveFlight,<CustomerID>,<FlightNumber>` | `ReserveCar,<CustomerID>,<Location>` | `ReserveRoom,<CustomerID>,<Location>`
- `Bundle,<CustomerID>,<Flight1>...<FlightN>,<Location>,<Car-true/false>,<Room-true/false>`
- `Quit`

## Notes
- Object names are prefixed with `group_46_`; keep middleware and client names consistent.
- State is not persisted; restart wipes data.
- Logs print to stdout; errors include a red prefix for visibility.
