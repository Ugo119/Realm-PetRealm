/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.petrealm.realm

import com.raywenderlich.android.petrealm.owners.models.Owner
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class OwnerDatabaseOperations @Inject constructor(
  private val config: RealmConfiguration
) {

  /**
   1. Get an instance of Realm.
   2. Use executeTransactionAwait().
   3. Create the new object.
   4. Insert it in the database.
   */
  suspend fun insertOwner(name: String, image: Int?) {
    val realm = Realm.getInstance(config)   // 1.

    realm.executeTransactionAwait (Dispatchers.IO) { realmTransaction ->    // 2.
      val owner = OwnerRealm(name = name, image = image)    // 3.
      realmTransaction.insert(owner)    // 4.
    }


  }

  /**
  To retrieve the owners, you:

  1. Get the Realm instance.
  2. Use executeTransactionAwait().
  3. Use where(OwnerRealm::class.java) to retrieve OwnerRealm objects.
  4. findAll() executes the query.
  5. Map OwnerRealm to Owner objects.
   */

  suspend fun retrieveOwners(): List<Owner> {
    // 1.
    val realm = Realm.getInstance(config)
    val owners = mutableListOf<Owner>()

    // 2.
    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      owners.addAll(realmTransaction
        // 3.
        .where(OwnerRealm::class.java)
        // 4.
        .findAll()
        .sort("name", Sort.ASCENDING)
        // 5.
        .map { owner ->
          Owner(
            name = owner.name,
            image = owner.image,
            id = owner.id,
            numberOfPets = getPetCount(realmTransaction, owner.id)
          )
        }
      )
    }

    return owners
  }

  suspend fun updatePets(petId: String, ownerId: String) {

  }

  suspend fun removeOwner(ownerId: String) {

  }

  /**
  1. Query the realm to get PetRealm objects.
  2. Use owner.id to filter by owner ID.
  3. Count the number of pets the owner has using .count()
   */
  private fun getPetCount(realm: Realm, ownerId: String): Long {
    return realm
      .where(PetRealm::class.java)  // 1.
      .equalTo("owner.id", ownerId)   // 2.
      .count()    // 3.
  }
}