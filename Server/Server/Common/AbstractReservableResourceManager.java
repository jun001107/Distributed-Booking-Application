package Server.Common;


import Server.Interface.IReservableResourceManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractReservableResourceManager extends AbstractResourceManager implements IReservableResourceManager {
    protected final Map<Integer, RMHashMap> reservations = new ConcurrentHashMap<>();

    protected AbstractReservableResourceManager(String name) {
        super(name);
    }

    // Deletes the specified item
    protected boolean deleteItem(String key) {
        try {
            lock.writeLock().lock();
            Trace.info("RM::deleteItem(" + key + ") called");
            ReservableItem curObj = (ReservableItem) readData(key);
            // Check if there is such an item in the storage
            if (curObj == null) {
                Trace.warn("RM::deleteItem(" + key + ") failed--item doesn't exist");
                return false;
            } else {
                if (curObj.getReserved() == 0) {
                    removeData(curObj.getKey());
                    Trace.info("RM::deleteItem(" + key + ") item deleted");
                    return true;
                } else {
                    Trace.info("RM::deleteItem(" + key + ") item can't be deleted because some customers have reserved it");
                    return false;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Query the number of available seats/rooms/cars
    protected int queryNum(String key) {
        try {
            lock.readLock().lock();
            Trace.info("RM::queryNum(" + key + ") called");
            ReservableItem curObj = (ReservableItem) readData(key);
            int value = 0;
            if (curObj != null) {
                value = curObj.getCount();
            }
            Trace.info("RM::queryNum(" + key + ") returns count=" + value);
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    // Query the price of an item
    protected int queryPrice(String key) {
        try {
            lock.readLock().lock();
            Trace.info("RM::queryPrice(" + key + ") called");
            ReservableItem curObj = (ReservableItem) readData(key);
            int value = 0;
            if (curObj != null) {
                value = curObj.getPrice();
            }
            Trace.info("RM::queryPrice(" + key + ") returns cost=$" + value);
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    // Reserve an item
    protected boolean reserveItem(int customerID, String key, String location) {
        try {
            lock.writeLock().lock();
            Trace.info("RM::reserveItem(customer=" + customerID + ", " + key + ", " + location + ") called");
            ReservableItem item = (ReservableItem) readData(key);
            if (item == null) {
                Trace.warn("RM::reserveItem(" + customerID + ", " + key + ", " + location + ") failed--item doesn't exist");
                return false;
            } else if (item.getCount() == 0) {
                Trace.warn("RM::reserveItem(" + customerID + ", " + key + ", " + location + ") failed--No more items");
                return false;
            } else {
                // Decrease the number of available items in the storage
                item.setCount(item.getCount() - 1);
                item.setReserved(item.getReserved() + 1);
                writeData(item.getKey(), item);

                // Add to customer's reservations
                reserve(customerID, key, location, item.getPrice());

                Trace.info("RM::reserveItem(" + customerID + ", " + key + ", " + location + ") succeeded");
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Remove a reservation
    protected boolean unreserveItem(int customerID, String key, String location) {
        try {
            lock.writeLock().lock();
            Trace.info("RM::unreserveItem(customer=" + customerID + ", " + key + ", " + location + ") called");
            ReservableItem item = (ReservableItem) readData(key);
            if (item == null) {
                Trace.warn("RM::unreserveItem(" + customerID + ", " + key + ", " + location + ") failed--item doesn't exist");
                return false;
            } else {
                if (reservations.containsKey(customerID) && reservations.get(customerID).containsKey(key)) {
                    // Remove from customer's reservations
                    ReservedItem reservedItem = (ReservedItem) reservations.get(customerID).get(key);
                    if (reservedItem.getCount() == 1) {
                        reservations.get(customerID).remove(key);
                    } else {
                        reservedItem.setCount(reservedItem.getCount() - 1);
                        reservations.get(customerID).put(reservedItem.getKey(), reservedItem);
                    }
                } else {
                    Trace.warn("RM::unreserveItem(" + customerID + ", " + key + ", " + location + ") failed--not reserved by customer");
                    return false;
                }

                // Increase the number of available items in the storage
                item.setCount(item.getCount() + 1);
                item.setReserved(item.getReserved() - 1);
                writeData(item.getKey(), item);

                Trace.info("RM::unreserveItem(" + customerID + ", " + key + ", " + location + ") succeeded");
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Get all the items a customer has reserved
    public List<ReservedItem> getReservations(int customerID) {
        try {
            lock.readLock().lock();
            return reservations.getOrDefault(customerID, new RMHashMap()).values().stream().map(item -> (ReservedItem) item).toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void releaseAllReservations(int customerID) {
        try {
            lock.writeLock().lock();
            Trace.info("RM::releaseAllReservations() called");
            List<ReservedItem> reservedItems = getReservations(customerID);
            for (ReservedItem reservedItem : reservedItems) {
                ReservableItem item = (ReservableItem) readData(reservedItem.getKey());
                Trace.info("RM::deleteCustomer(" + customerID + ") has reserved " + reservedItem.getKey() + " which is reserved " + item.getReserved() + " times and is still available " + item.getCount() + " times");
                item.setReserved(item.getReserved() - reservedItem.getCount());
                item.setCount(item.getCount() + reservedItem.getCount());
                writeData(item.getKey(), item);
            }
            reservations.remove(customerID);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void reserve(int customerID, String key, String location, int price) {
        try {
            lock.writeLock().lock();
            reservations.putIfAbsent(customerID, new RMHashMap());
            ReservedItem reservedItem = (ReservedItem) reservations.get(customerID).get(key);
            if (reservedItem == null) {
                // Customer doesn't already have a reservation for this resource, so create a new one now
                reservedItem = new ReservedItem(key, location, 1, price);
            } else {
                reservedItem.setCount(reservedItem.getCount() + 1);
                // NOTE: latest price overrides existing price
                reservedItem.setPrice(price);
            }
            reservations.get(customerID).put(reservedItem.getKey(), reservedItem);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
