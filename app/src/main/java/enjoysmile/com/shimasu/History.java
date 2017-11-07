package enjoysmile.com.shimasu;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/*
 * Copyright (c) 2017 enjoy|smile. All Rights Reserved.
 */
public class History extends RealmObject {
  @PrimaryKey private String id;
  private Activity activity;
  private int quantity;
  private long date;
  private int points;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Activity getActivity() {
    return activity;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  int getQuantity() {
    return quantity;
  }

  void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  long getDate() {
    return date;
  }

  void setDate(long date) {
    this.date = date;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }
}
