package com.cardmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardmanager.data.*
import kotlinx.coroutines.launch

class HistoryViewModel(private val transactionRepo: TransactionRepository,
                       private val cardRepo: CardRepository,
                       private val attractionRepo: AttractionRepository) : ViewModel() {

    var transactionsLD = MutableLiveData<List<Transaction>>().apply { value = listOf() }
    var cardId = MutableLiveData<String>().apply { postValue("") }
    val balance = MutableLiveData<Double>()
    val isCardAttached = MutableLiveData<Boolean>().apply { postValue(false) }

    suspend fun updateTransactions() {
        transactionsLD.value = transactionRepo.getTransactionsFromCard(cardId.value!!)
    }

    suspend fun getAllAttractions(): List<Attraction> {
        return attractionRepo.getAllAttractions()
    }

    suspend fun getAttractionById(id: String): Attraction? {
        return attractionRepo.getAttractionById(id)
    }

    suspend fun checkCard(id: String): Boolean {
        val card = cardRepo.getCard(id)

        return if(card != null) {
            balance.postValue(card.balance.toString().toDouble())
            isCardAttached.postValue(true)
            true
        } else false
    }
}