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

import com.raywenderlich.android.petrealm.pets.models.Pet
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PetDatabaseOperations @Inject constructor(
  private val config: RealmConfiguration
) {

  /**
  1. Get an instance of Realm, using RealmConfiguration.
  2. Use executeTransactionAwait() with Dispatchers.IO to execute a transaction in a
     background thread and wait until it finishes.
  3. Create a new PetRealm object.
  4. Insert the new PetRealm object.
   */

  suspend fun insertPet(
    name: String,
    age: Int,
    type: String,
    image: Int?
  ) {
    val realm = Realm.getInstance(config)   // 1.

    //Use executeTransactionAwait() with Dispatchers.IO to execute a transaction in a
    // background thread and wait until it finishes.
    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->   // 2.
      val pet = PetRealm(name = name, age = age, petType = type, image = image)   // 3.
      realmTransaction.insert(pet)    // 4.
    }
  }

  /**
  To retrieve pets up for adoption:
  1. Get the Realm instance.
  2. Use executeTransactionAwait().
  3. Use where(PetRealm::class.java) to retrieve PetRealm objects.
  4. findAll() executes the query and returns every PetRealm object.
  5. Map PetRealm to Pet objects.
   */
  suspend fun retrievePetsToAdopt(): List<Pet> {
    val realm = Realm.getInstance(config)   // 1.
    val petsToAdopt = mutableListOf<Pet>()

    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->   // 2.
      petsToAdopt.addAll(realmTransaction
        .where(PetRealm::class.java)    // 3.
        .equalTo("isAdopted", false)
        .findAll()    // 4.
        // 5.
        .map {
          mapPet(it)
        }
      )
    }

    return petsToAdopt
  }

  suspend fun retrieveAdoptedPets(): List<Pet> {
    val realm = Realm.getInstance(config)
    val adoptedPets = mutableListOf<Pet>()

    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      adoptedPets.addAll(realmTransaction
        .where(PetRealm::class.java)
        .equalTo("isAdopted", true)
        .findAll()
        .map {
          mapPet(it)
        }
      )
    }

    return adoptedPets
  }

  suspend fun removePet(petId: String) {

  }

  /**
  This code executes a transaction to retrieve PetRealm objects. The filtering works the following way:

  1. Condition to filter PetRealm objects that have isAdopted as false.
  2. and() is a logical operator that indicates the result should meet both conditions.
  Here you can find a list of all the logical operators Realm supports.
  3. Condition to filter PetRealm objects that have their petType field with the provided value.
   */
  suspend fun retrieveFilteredPets(petType: String): List<Pet> {
    val realm = Realm.getInstance(config)
    val filteredPets = mutableListOf<Pet>()

    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      filteredPets.addAll(realmTransaction
        .where(PetRealm::class.java)
        .equalTo("isAdopted", false)  // 1.
        .and()  // 2.
        .beginsWith("petType", petType) // 3.
        .findAll()
        .map {
          mapPet(it)
        }
      )
    }

    return filteredPets
  }

  /**
  Realm provides a query engine that allows you to find, filter and sort objects. Each query
  result is a live object. This means it contains the latest data and, if you decide to modify
  the result, it will modify the stored object.
   */
  private fun mapPet(pet: PetRealm): Pet {
    return Pet(
      name = pet.name,
      age = pet.age,
      image = pet.image,
      petType = pet.petType,
      isAdopted = pet.isAdopted,
      id = pet.id
    )
  }
}