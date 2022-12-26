package com.cardmanager.viewmodels

import android.nfc.NfcAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardmanager.data.Attraction
import com.cardmanager.data.AttractionRepository
import com.cardmanager.data.Card
import com.cardmanager.data.CardRepository
import com.cardmanager.utils.CardUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Attr

class HomeViewModel(private val db: FirebaseFirestore,
                    private val attractionRepo: AttractionRepository,
                    private val cardRepo: CardRepository): ViewModel() {

    var attractionsLD = MutableLiveData<List<Attraction>>().apply { value = listOf() }

    val cardId = MutableLiveData<String>().apply { postValue("Приложите карту") }
    val balance = MutableLiveData<Double>()
    val isCardAttached = MutableLiveData<Boolean>().apply { postValue(false) }
    val chosenAttraction = MutableLiveData<Attraction>().apply { postValue(Attraction(id="")) }

    init {
        viewModelScope.launch {
            attractionsLD.value = attractionRepo.getAllAttractions()
        }
    }

    suspend fun checkCard(id: String): Boolean {
        val card = cardRepo.getCard(id)

        return if(card != null) {
            balance.postValue(card.balance.toString().toDouble())
            isCardAttached.postValue(true)
            true
        } else false
    }

    suspend fun spendMoney(attraction: Attraction): Boolean {
        return if(balance.value!! - attraction.price >= 0)
            cardRepo.spendMoney(cardId.value!!, attraction)
        else false
    }

    suspend fun addMoney(amount: Double): Boolean {
        return cardRepo.addMoney(cardId.value!!, amount)
    }

    suspend fun getCard(id: String): Card {
        return cardRepo.getCard(id)!!
    }

}