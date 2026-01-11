package Server.Interface;

import Server.Common.ReservedItem;

import java.rmi.RemoteException;
import java.util.List;

public interface IReservableResourceManager extends IBaseResourceManager {
    /**
     * Get a list of reserved items for the customer.
     *
     * @return List of reserved items.
     */
    List<ReservedItem> getReservations(int customerID) throws RemoteException;

    /**
     * Release all flights reserved for the customer.
     */
    void releaseAllReservations(int customerID) throws RemoteException;
}
