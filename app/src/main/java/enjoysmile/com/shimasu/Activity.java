package enjoysmile.com.shimasu;

/**
 * Created by Daniel on 11/27/2016.
 */

public class Activity {
    public int id;
    public String name;
    public String description;
    public int type;
    public int points;

    public Activity(int id, String name, String description, int type, int points) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.points = points;
    }
}
