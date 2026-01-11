package Server.Middleware;

import Server.Common.ReservedItem;
import Server.Common.Trace;
import Server.Interface.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Middleware implements IResourceManager {
    private final String name;
    private final IFlightManager flightManager;
    private final ICarManager carManager;
    private final IRoomManager roomManager;
    private final ICustomerManager customerManager;

    public Middleware(String name, IFlightManager flightManager, ICarManager carManager, IRoomManager roomManager, ICustomerManager customerManager) {
        this.name = name;
        this.flightManager = flightManager;
        this.carManager = carManager;
        this.roomManager = roomManager;
        this.customerManager = customerManager;
    }

    @Override
    public boolean addFlight(int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return flightManager.addFlight(flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(String location, int numCars, int price) throws RemoteException {
        return carManager.addCars(location, numCars, price);
    }

    @Override
    public boolean addRooms(String location, int numRooms, int price) throws RemoteException {
        return roomManager.addRooms(location, numRooms, price);
    }

    @Override
    public int newCustomer() throws RemoteException {
        return customerManager.newCustomer();
    }

    @Override
    public boolean newCustomer(int customerID) throws RemoteException {
        return customerManager.newCustomer(customerID);
    }

    @Override
    public boolean deleteFlight(int flightNum) throws RemoteException {
        return flightManager.deleteFlight(flightNum);
    }

    @Override
    public boolean deleteCars(String location) throws RemoteException {
        return carManager.deleteCars(location);
    }

    @Override
    public boolean deleteRooms(String location) throws RemoteException {
        return roomManager.deleteRooms(location);
    }

    @Override
    public boolean deleteCustomer(int customerID) throws RemoteException {
        if (!customerManager.customerExists(customerID)) {
            Trace.warn("Middleware::deleteCustomer(" + customerID + ") failed--customer doesn't exist");
            return false;
        }
        flightManager.releaseAllReservations(customerID);
        carManager.releaseAllReservations(customerID);
        roomManager.releaseAllReservations(customerID);
        return customerManager.deleteCustomer(customerID);
    }

    @Override
    public int queryFlight(int flightNumber) throws RemoteException {
        return flightManager.queryFlight(flightNumber);
    }

    @Override
    public int queryCars(String location) throws RemoteException {
        return carManager.queryCars(location);
    }

    @Override
    public int queryRooms(String location) throws RemoteException {
        return roomManager.queryRooms(location);
    }

    @Override
    public String queryCustomerInfo(int customerID) throws RemoteException {
        if (!customerManager.customerExists(customerID)) {
            Trace.warn("Middleware::queryCustomerInfo(" + customerID + ") failed--customer doesn't exist");
            return "Customer does not exist.";
        }
        List<ReservedItem> reservedItems = new ArrayList<>();
        reservedItems.addAll(flightManager.getReservations(customerID));
        reservedItems.addAll(carManager.getReservations(customerID));
        reservedItems.addAll(roomManager.getReservations(customerID));
        return getBill(customerID, reservedItems);
    }

    @Override
    public int queryFlightPrice(int flightNumber) throws RemoteException {
        return flightManager.queryFlightPrice(flightNumber);
    }

    @Override
    public int queryCarsPrice(String location) throws RemoteException {
        return carManager.queryCarsPrice(location);
    }

    @Override
    public int queryRoomsPrice(String location) throws RemoteException {
        return roomManager.queryRoomsPrice(location);
    }

    @Override
    public boolean reserveFlight(int customerID, int flightNumber) throws RemoteException {
        if (!customerManager.customerExists(customerID)) {
            Trace.warn("Middleware::reserveFlight(" + customerID + ", " + flightNumber + ") failed--customer doesn't exist");
            return false;
        }
        return flightManager.reserveFlight(customerID, flightNumber);
    }

    @Override
    public boolean reserveCar(int customerID, String location) throws RemoteException {
        if (!customerManager.customerExists(customerID)) {
            Trace.warn("Middleware::customerExists(" + customerID + ", " + location + ") failed--customer doesn't exist");
            return false;
        }
        return carManager.reserveCar(customerID, location);
    }

    @Override
    public boolean reserveRoom(int customerID, String location) throws RemoteException {
        if (!customerManager.customerExists(customerID)) {
            Trace.warn("Middleware::reserveRoom(" + customerID + ", " + location + ") failed--customer doesn't exist");
            return false;
        }
        return roomManager.reserveRoom(customerID, location);
    }

    @Override
    public boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        if (!customerManager.customerExists(customerID)) {
            Trace.warn("Middleware::bundle(" + customerID + ", " + flightNumbers + ", " + location + ", " + car + ", " + room + ") failed--customer doesn't exist");
            return false;
        }

        boolean error = false;

        List<Integer> flightsReserved = new ArrayList<>();
        for (String flightNumber : flightNumbers) {
            boolean success = flightManager.reserveFlight(customerID, Integer.parseInt(flightNumber));
            if (success) {
                flightsReserved.add(Integer.parseInt(flightNumber));
            } else {
                error = true;
                break;
            }
        }

        boolean carSuccessfulReserved = false;
        if (car && !error) {
            boolean success = carManager.reserveCar(customerID, location);
            if (success) {
                carSuccessfulReserved = true;
            } else {
                error = true;
            }
        }

        if (room && !error) {
            boolean success = roomManager.reserveRoom(customerID, location);
            if (!success) {
                error = true;
            }
        }

        if (error) {
            // Perform rollback
            boolean rollbackSuccess = true;
            for (int flightNumber : flightsReserved) {
                // Cancel flight reservation
                rollbackSuccess &= flightManager.cancelFlightReservation(customerID, flightNumber);
            }
            if (carSuccessfulReserved) {
                rollbackSuccess &= carManager.cancelCarReservation(customerID, location);
            }
            // Note: If room was successfully reserved, then there would be no error, so no need to check it.
            if (!rollbackSuccess) {
                Trace.error("Middleware::bundle(" + customerID + ", " + flightNumbers + ", " + location + ", " + car + ", " + room + ") rollback failed");
            }
            return false;
        }

        return true;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    private String getBill(int customerID, List<ReservedItem> reservedItems) {
        StringBuilder s = new StringBuilder("Bill for customer " + customerID + "\n");
        for (ReservedItem item : reservedItems) {
            s.append(item.getCount()).append(" ").append(item.getReservableItemKey()).append(" $").append(item.getPrice()).append("\n");
        }
        return s.toString();
    }
}
