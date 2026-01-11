package Server.Interface;

import java.rmi.RemoteException;

public interface IFlightManager extends IReservableResourceManager {
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
     * Cancel a flight reservation for a customer.
     *
     * @return Success
     */
    boolean cancelFlightReservation(int customerID, int flightNumber) throws RemoteException;
}
