package enjoysmile.com.shimasu.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/** Created by Daniel on 11/27/2016. */
public class Activity extends RealmObject {
  @PrimaryKey private long id;
  private String name;
  private String description;
  private int type;
  private int points;
  private boolean repeatable;
  private boolean deleted;

  public Activity() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public boolean isRepeatable() {
    return repeatable;
  }

  public void setRepeatable(boolean repeatable) {
    this.repeatable = repeatable;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
