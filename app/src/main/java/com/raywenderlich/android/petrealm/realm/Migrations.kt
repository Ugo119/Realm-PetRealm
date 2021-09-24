package com.raywenderlich.android.petrealm.realm

import io.realm.RealmMigration

/**
1. Create a val of type RealmMigration.
2. Define what to do for each version change. oldVersion will hold the value for the previous schema version.
3. Get each schema you need to modify.
4. ownerSchema needs a new RealmList field of type PetRealm. Use addRealmListField() with the name
   of the field and the schema type the field needs.
 */

// 1.
val migration = RealmMigration { realm, oldVersion, newVersion ->
    // 2.
    if (oldVersion == 1L) {
        // 3.
        val ownerSchema = realm.schema.get("OwnerRealm")
        val petSchema = realm.schema.get("PetRealm")

        // 4.
        petSchema?.let {
            ownerSchema?.addRealmListField("pets", it)
        }
    }
}