package Server.Common;

import Server.Interface.IFlightManager;

import java.rmi.RemoteException;

public class FlightManager extends AbstractReservableResourceManager implements IFlightManager {
    public FlightManager(String name) {
        super(name);
    }

    // Create a new flight, or add seats to existing flight
    // NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
    public boolean addFlight(int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        try {
            lock.writeLock().lock();
            Trace.info("RM::addFlight(" + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
            Flight curObj = (Flight) readData(Flight.getKey(flightNum));
            if (curObj == null) {
                // Doesn't exist yet, add it
                Flight newObj = new Flight(flightNum, flightSeats, flightPrice);
                writeData(newObj.getKey(), newObj);
                Trace.info("RM::addFlight() created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
            } else {
                // Add seats to existing flight and update the price if greater than zero
                curObj.setCount(curObj.getCount() + flightSeats);
                if (flightPrice > 0) {
                    curObj.setPrice(flightPrice);
                }
                writeData(curObj.getKey(), curObj);
                Trace.info("RM::addFlight() modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Deletes flight
    public boolean deleteFlight(int flightNum) throws RemoteException {
        return deleteItem(Flight.getKey(flightNum));
    }

    // Returns the number of empty seats in this flight
    public int queryFlight(int flightNum) throws RemoteException {
        return queryNum(Flight.getKey(flightNum));
    }

    // Returns price of a seat in this flight
    public int queryFlightPrice(int flightNum) throws RemoteException {
        return queryPrice(Flight.getKey(flightNum));
    }

    // Adds flight reservation to this customer
    public boolean reserveFlight(int customerID, int flightNum) throws RemoteException {
        return reserveItem(customerID, Flight.getKey(flightNum), String.valueOf(flightNum));
    }

    // Cancel a flight reservation
    public boolean cancelFlightReservation(int customerID, int flightNumber) throws RemoteException {
        return unreserveItem(customerID, Flight.getKey(flightNumber), String.valueOf(flightNumber));
    }
}
