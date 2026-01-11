package Server.Interface;

import java.rmi.RemoteException;

public interface ICustomerManager extends IBaseResourceManager {
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
    boolean newCustomer(int cid)
            throws RemoteException;

    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    boolean deleteCustomer(int customerID)
            throws RemoteException;

    /**
     * Check if a customer with the given id exists.
     *
     * @return Whether the customer exists or not
     */
    boolean customerExists(int customerID) throws RemoteException;
}
