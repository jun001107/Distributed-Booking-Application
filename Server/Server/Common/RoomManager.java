package Server.Common;

import Server.Interface.IRoomManager;

import java.rmi.RemoteException;

public class RoomManager extends AbstractReservableResourceManager implements IRoomManager {
    public RoomManager(String name) {
        super(name);
    }

    // Create a new room location or add rooms to an existing location
    // NOTE: if price <= 0 and the room location already exists, it maintains its current price
    public boolean addRooms(String location, int count, int price) throws RemoteException {
        try {
            lock.writeLock().lock();
            Trace.info("RM::addRooms(" + location + ", " + count + ", $" + price + ") called");
            Room curObj = (Room) readData(Room.getKey(location));
            if (curObj == null) {
                // Room location doesn't exist yet, add it
                Room newObj = new Room(location, count, price);
                writeData(newObj.getKey(), newObj);
                Trace.info("RM::addRooms() created new room location " + location + ", count=" + count + ", price=$" + price);
            } else {
                // Add count to existing object and update price if greater than zero
                curObj.setCount(curObj.getCount() + count);
                if (price > 0) {
                    curObj.setPrice(price);
                }
                writeData(curObj.getKey(), curObj);
                Trace.info("RM::addRooms() modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Delete rooms at a location
    public boolean deleteRooms(String location) throws RemoteException {
        return deleteItem(Room.getKey(location));
    }

    // Returns the amount of rooms available at a location
    public int queryRooms(String location) throws RemoteException {
        return queryNum(Room.getKey(location));
    }

    // Returns room price at this location
    public int queryRoomsPrice(String location) throws RemoteException {
        return queryPrice(Room.getKey(location));
    }

    // Adds room reservation to this customer
    public boolean reserveRoom(int customerID, String location) throws RemoteException {
        return reserveItem(customerID, Room.getKey(location), location);
    }

    // Cancel a room reservation of this customer
    public boolean cancelRoomReservation(int customerID, String location) throws RemoteException {
        return unreserveItem(customerID, Room.getKey(location), location);
    }
}
