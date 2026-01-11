package Server.Common;

import Server.Interface.ICustomerManager;

import java.rmi.RemoteException;
import java.util.Calendar;

public class CustomerManager extends AbstractResourceManager implements ICustomerManager {
    public CustomerManager(String name) {
        super(name);
    }

    public int newCustomer() throws RemoteException {
        try {
            lock.writeLock().lock();
            Trace.info("RM::newCustomer() called");
            // Generate a globally unique ID for the new customer; if it generates duplicates for you, then adjust
            int cid = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                    String.valueOf(Math.round(Math.random() * 100 + 1)));
            Customer customer = new Customer(cid);
            writeData(customer.getKey(), customer);
            Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
            return cid;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean newCustomer(int customerID) throws RemoteException {
        try {
            lock.writeLock().lock();
            Trace.info("RM::newCustomer(" + customerID + ") called");
            Customer customer = (Customer) readData(Customer.getKey(customerID));
            if (customer == null) {
                customer = new Customer(customerID);
                writeData(customer.getKey(), customer);
                Trace.info("RM::newCustomer(" + customerID + ") created a new customer");
                return true;
            } else {
                Trace.info("INFO: RM::newCustomer(" + customerID + ") failed--customer already exists");
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteCustomer(int customerID) throws RemoteException {
        try {
            lock.writeLock().lock();
            Trace.info("RM::deleteCustomer(" + customerID + ") called");
            Customer customer = (Customer) readData(Customer.getKey(customerID));
            if (customer == null) {
                Trace.warn("RM::deleteCustomer(" + customerID + ") failed--customer doesn't exist");
                return false;
            } else {
                // Remove the customer from the storage
                removeData(customer.getKey());
                Trace.info("RM::deleteCustomer(" + customerID + ") succeeded");
                return true;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean customerExists(int customerID) {
        try {
            lock.readLock().lock();
            return data.containsKey(Customer.getKey(customerID));
        } finally {
            lock.readLock().unlock();
        }
    }
}
