package com.cardmanager.viewmodels

import android.nfc.NfcAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardmanager.data.*
import com.cardmanager.utils.CardUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class AttractionsViewModel(private val attractionRepo: AttractionRepository): ViewModel() {

    var attractionsLD = MutableLiveData<List<Attraction>>().apply { value = listOf() }

    val chosenAttraction = MutableLiveData<Attraction>().apply { postValue(Attraction(id="")) }
    val name = MutableLiveData<String>()
    val price = MutableLiveData<Double>().apply { postValue(0.0) }

    init {
        viewModelScope.launch {
            attractionsLD.value = attractionRepo.getAllAttractions()
        }
    }

    suspend fun addAttraction(attraction: Attraction): String {
        attractionRepo.addAttraction(attraction)
        return  "Успешно добавлен"
    }

    suspend fun deleteAttraction(attraction: Attraction): String {
        attractionRepo.deleteAttraction(attraction)
        return  "Успешно удалено"
    }

}