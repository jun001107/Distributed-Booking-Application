// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;

public class Car extends ReservableItem {
    public Car(String location, int count, int price) {
        super(location, count, price);
    }

    public static String getKey(String location) {
        String s = "car-" + location;
        return s.toLowerCase();
    }

    public String getKey() {
        return Car.getKey(getLocation());
    }
}
