package enjoysmile.com.shimasu;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Daniel on 11/27/2016.
 */

public class Activity extends RealmObject {
    @PrimaryKey
    public long id;
    public String name;
    public String description;
    public int type;
    public int points;
    public boolean repeatable;
    // TODO repeatable boolean

    public Activity() {

    }

    public Activity(long id, String name, String description, int type, int points, boolean repeatable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.points = points;
        this.repeatable = repeatable;
    }
}
