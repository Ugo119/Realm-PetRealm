package com.raywenderlich.android.petrealm.realm

import androidx.annotation.DrawableRes
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

/**
1. You need to declare every entity class as open and every field as var.
2. @PrimaryKey indicates the ID field is the primary key.
3. Realm provides ObjectId() to create unique IDs for each object. You’ll use its hex representation.
4. @Required indicates the field requires a value. Add this annotation to name and petType.
5. For fields that allow null values, you can use nullable types and assign null as its default value.
6. Each entity must be a RealmObject().
7. Add @LinkingObjects annotation, passing as parameter the name of the field you’re adding the
   relationship to. The field in OwnerRealm you want to link is pets.
8. The field should be val and of type RealmResults.
 */

// 1.
open class PetRealm(
    @PrimaryKey     // 2.
    var id: String = ObjectId().toHexString(), // 3.
    @Required       // 4.
    var name: String = "",
    @Required
    var petType: String = "",
    var age: Int = 0,
    var isAdopted: Boolean = false,
    @DrawableRes
    var image: Int? = null,      // 5.
    @LinkingObjects("pets") // 7.
    val owner: RealmResults<OwnerRealm>? = null // 8.
): RealmObject()        // 6.