package enjoysmile.com.shimasu;

/**
 * Created by Daniel on 12/7/2016.
 */

public class History {
    public int id;
    public Activity activity;
    public int quantity;
    public long date;
    public int points;

    public History(int id, Activity activity, int quantity, long date, int points) {
        this.id = id;
        this.activity = activity;
        this.quantity = quantity;
        this.date = date;
        this.points = points;
    }
}
