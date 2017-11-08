package enjoysmile.com.shimasu;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/*
 * Copyright (c) 2017 enjoy|smile. All Rights Reserved.
 */
public class User extends RealmObject {
  @PrimaryKey private String id;
  private int points;

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
}
