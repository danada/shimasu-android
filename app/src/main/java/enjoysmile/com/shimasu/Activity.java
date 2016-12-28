package enjoysmile.com.shimasu;

import io.realm.RealmObject;

/**
 * Created by Daniel on 11/27/2016.
 */

public class Activity extends RealmObject {
    public int id;
    public String name;
    public String description;
    public int type;
    public int points;

    public Activity() {

    }

    public Activity(int id, String name, String description, int type, int points) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.points = points;
    }
}
