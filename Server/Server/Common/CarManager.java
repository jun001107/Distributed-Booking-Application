package Server.Common;

import Server.Interface.ICarManager;

import java.rmi.RemoteException;

public class CarManager extends AbstractReservableResourceManager implements ICarManager {
    public CarManager(String name) {
        super(name);
    }

    // Create a new car location or add cars to an existing location
    // NOTE: if price <= 0 and the location already exists, it maintains its current price
    public boolean addCars(String location, int count, int price) throws RemoteException {
        try {
            lock.writeLock().lock();
            Trace.info("RM::addCars(" + location + ", " + count + ", $" + price + ") called");
            Car curObj = (Car) readData(Car.getKey(location));
            if (curObj == null) {
                // Car location doesn't exist yet, add it
                Car newObj = new Car(location, count, price);
                writeData(newObj.getKey(), newObj);
                Trace.info("RM::addCars() created new location " + location + ", count=" + count + ", price=$" + price);
            } else {
                // Add count to existing car location and update price if greater than zero
                curObj.setCount(curObj.getCount() + count);
                if (price > 0) {
                    curObj.setPrice(price);
                }
                writeData(curObj.getKey(), curObj);
                Trace.info("RM::addCars() modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Delete cars at a location
    public boolean deleteCars(String location) throws RemoteException {
        return deleteItem(Car.getKey(location));
    }

    // Returns the number of cars available at a location
    public int queryCars(String location) throws RemoteException {
        return queryNum(Car.getKey(location));
    }

    // Returns price of cars at this location
    public int queryCarsPrice(String location) throws RemoteException {
        return queryPrice(Car.getKey(location));
    }

    // Adds car reservation to this customer
    public boolean reserveCar(int customerID, String location) throws RemoteException {
        return reserveItem(customerID, Car.getKey(location), location);
    }

    // Cancel a car reservation of this customer
    public boolean cancelCarReservation(int customerID, String location) throws RemoteException {
        return unreserveItem(customerID, Car.getKey(location), location);
    }
}
