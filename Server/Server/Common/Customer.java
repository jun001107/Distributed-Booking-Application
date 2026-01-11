// -------------------------------
// Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;

public class Customer extends RMItem {
    private int m_ID;

    public Customer(int id) {
        super();
        m_ID = id;
    }

    public static String getKey(int customerID) {
        String s = "customer-" + customerID;
        return s.toLowerCase();
    }

    public int getID() {
        return m_ID;
    }

    public String toString() {
        String ret = "--- BEGIN CUSTOMER key='";
        ret += getKey() + "', id='" + getID() + "'\n";
        ret += "--- END CUSTOMER ---";
        return ret;
    }

    public String getKey() {
        return Customer.getKey(getID());
    }

    public Object clone() {
        Customer obj = (Customer) super.clone();
        obj.m_ID = m_ID;
        return obj;
    }
}

