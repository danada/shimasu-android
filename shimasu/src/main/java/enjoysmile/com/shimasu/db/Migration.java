package enjoysmile.com.shimasu.db;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import javax.annotation.Nonnull;

/*
 * Copyright (c) 2017 enjoy|smile. All Rights Reserved.
 */
public class Migration implements RealmMigration {
  @Override
  public void migrate(@Nonnull DynamicRealm realm, long oldVersion, long newVersion) {
    RealmSchema schema = realm.getSchema();

    // Migrate to version 1: Add a deleted field to Activity.
    if (oldVersion == 0) {
      RealmObjectSchema activitySchema = schema.get("Activity");
      if (activitySchema != null) {
        activitySchema.addField("deleted", boolean.class);
      }

      RealmObjectSchema userSchema = schema.get("User");
      if (userSchema != null) {
        userSchema.removeField("lastUpdated");
      }
    }
  }
}
