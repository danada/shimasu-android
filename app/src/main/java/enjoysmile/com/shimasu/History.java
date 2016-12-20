package enjoysmile.com.shimasu;

/**
 * Created by Daniel on 12/7/2016.
 */

class History {
    int id;
    Activity activity;
    int quantity;
    long date;
    int points;

    History(int id, Activity activity, int quantity, long date, int points) {
        this.id = id;
        this.activity = activity;
        this.quantity = quantity;
        this.date = date;
        this.points = points;
    }
}
