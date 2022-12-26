package com.cardmanager.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

import java.util.*

class AttractionRepository(private val db: FirebaseFirestore) {
    lateinit var snapshotListener: ListenerRegistration

    fun setSnapshotListener(func: (Date) -> Unit, chosenTimeLD: MutableLiveData<Date>, email: String, isDataSynced: MutableLiveData<Boolean>) {
        snapshotListener = db
            .collection("measure")
            .document(email)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, _ ->
                func(chosenTimeLD.value!!)
                if(snapshot?.metadata?.hasPendingWrites() != null)
                    isDataSynced.value = !(snapshot.metadata.hasPendingWrites())

                println("isDataSynced: ${isDataSynced.value}")
            }
    }

    suspend fun getAllAttractions(): List<Attraction> {
        val data = db.collection("attractions").get().await()
        val attractions = mutableListOf<Attraction>()

        //println(data.documents[0])
        for(document in data.documents) {
            val attraction = document.toObject(Attraction::class.java)
            println(attraction)
            attractions.add(attraction!!)
        }

        return attractions.toList()
    }

    suspend fun getAttractionById(id: String): Attraction? {
        println("ID is $id")
        return if(id == "") null
        else  db.collection("attractions").document(id).get().await().toObject(Attraction::class.java)
    }

    suspend fun addAttraction(attraction: Attraction) {
        db.collection("attractions").document(attraction.id).set(attraction).await()
    }

    suspend fun deleteAttraction(attraction: Attraction) {
        db.collection("attractions").document(attraction.id).delete().await()
    }


}