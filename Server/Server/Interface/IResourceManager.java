package Server.Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Simplified version from CSE 593 Univ. of Washington
 * <p>
 * Distributed  System in Java.
 * <p>
 * failure reporting is done using two pieces, exceptions and boolean
 * return values.  Exceptions are used for systemy things. Return
 * values are used for operations that would affect the consistency
 * <p>
 * If there is a boolean return value and you're not sure how it
 * would be used in your implementation, ignore it.  I used boolean
 * return values in the interface generously to allow flexibility in
 * implementation.  But don't forget to return true when the operation
 * has succeeded.
 */

public interface IResourceManager extends Remote {
    /**
     * Add seats to a flight.
     * <p>
     * In general this will be used to create a new
     * flight, but it should be possible to add seats to an existing flight.
     * Adding to an existing flight should overwrite the current price of the
     * available seats.
     *
     * @return Success
     */
    boolean addFlight(int flightNum, int flightSeats, int flightPrice)
            throws RemoteException;

    /**
     * Delete the flight.
     * <p>
     * deleteFlight implies whole deletion of the flight. If there is a
     * reservation on the flight, then the flight cannot be deleted
     *
     * @return Success
     */
    boolean deleteFlight(int flightNum)
            throws RemoteException;

    /**
     * Query the status of a flight.
     *
     * @return Number of empty seats
     */
    int queryFlight(int flightNumber)
            throws RemoteException;

    /**
     * Query the status of a flight.
     *
     * @return Price of a seat in this flight
     */
    int queryFlightPrice(int flightNumber)
            throws RemoteException;

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    boolean reserveFlight(int customerID, int flightNumber)
            throws RemoteException;

    /**
     * Add car at a location.
     * <p>
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    boolean addCars(String location, int numCars, int price)
            throws RemoteException;

    /**
     * Delete all cars at a location.
     * <p>
     * It may not succeed if there are reservations for this location
     *
     * @return Success
     */
    boolean deleteCars(String location)
            throws RemoteException;

    /**
     * Query the status of a car location.
     *
     * @return Number of available cars at this location
     */
    int queryCars(String location)
            throws RemoteException;

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    int queryCarsPrice(String location)
            throws RemoteException;

    /**
     * Reserve a car at this location.
     *
     * @return Success
     */
    boolean reserveCar(int customerID, String location)
            throws RemoteException;

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
     * Add customer.
     *
     * @return Unique customer identifier
     */
    int newCustomer()
            throws RemoteException;

    /**
     * Add customer with id.
     *
     * @return Success
     */
    boolean newCustomer(int customerID)
            throws RemoteException;

    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    boolean deleteCustomer(int customerID)
            throws RemoteException;

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    String queryCustomerInfo(int customerID)
            throws RemoteException;

    /**
     * Reserve a bundle for the trip.
     *
     * @return Success
     */
    boolean bundle(int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room)
            throws RemoteException;

    /**
     * Convenience for probing the resource manager.
     *
     * @return Name
     */
    String getName()
            throws RemoteException;
}
