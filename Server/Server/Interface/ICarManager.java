package Server.Interface;

import java.rmi.RemoteException;

public interface ICarManager extends IReservableResourceManager {
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
     * Cancel a (single) car reservation for a customer.
     *
     * @return Success
     */
    boolean cancelCarReservation(int customerID, String location) throws RemoteException;
}
