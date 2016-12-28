package enjoysmile.com.shimasu;

import io.realm.RealmObject;

/**
 * Created by Daniel on 12/7/2016.
 */

public class History extends RealmObject {
    int id;
    Activity activity;
    int quantity;
    long date;
    int points;

    public History() {

    }

    History(int id, Activity activity, int quantity, long date, int points) {
        this.id = id;
        this.activity = activity;
        this.quantity = quantity;
        this.date = date;
        this.points = points;
    }
}
