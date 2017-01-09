package enjoysmile.com.shimasu;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Daniel on 1/9/2017.
 */

public class User extends RealmObject {
    @PrimaryKey
    private String id;
    private int points;
    private long lastUpdated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

