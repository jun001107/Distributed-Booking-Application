package Server.Interface;

import java.rmi.RemoteException;

public interface IRoomManager extends IReservableResourceManager {
    /**
     * Add room at a location.
     * <p>
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    boolean addRooms(String location, int numRooms, int price)
            throws RemoteException;

    /**
     * Delete all rooms at a location.
     * <p>
     * It may not succeed if there are reservations for this location.
     *
     * @return Success
     */
    boolean deleteRooms(String location)
            throws RemoteException;

    /**
     * Query the status of a room location.
     *
     * @return Number of available rooms at this location
     */
    int queryRooms(String location)
            throws RemoteException;

    /**
     * Query the status of a room location.
     *
     * @return Price of a room
     */
    int queryRoomsPrice(String location)
            throws RemoteException;

    /**
     * Reserve a room at this location.
     *
     * @return Success
     */
    boolean reserveRoom(int customerID, String location)
            throws RemoteException;

    /**
     * Cancel a (single) room reservation for a customer.
     *
     * @return Success
     */
    boolean cancelRoomReservation(int customerID, String location) throws RemoteException;
}
