package Server.Common;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractResourceManager {
    protected final String name;
    protected final RMHashMap data = new RMHashMap();
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected AbstractResourceManager(String name) {
        this.name = name;
    }

    // Reads a data item
    protected RMItem readData(String key) {
        synchronized (data) {
            RMItem item = data.get(key);
            if (item != null) {
                return (RMItem) item.clone();
            }
            return null;
        }
    }

    // Writes a data item
    protected void writeData(String key, RMItem value) {
        synchronized (data) {
            data.put(key, value);
        }
    }

    // Remove the item out of storage
    protected void removeData(String key) {
        synchronized (data) {
            data.remove(key);
        }
    }

    // Get the name of the resource manager
    public String getName() {
        return name;
    }
}
