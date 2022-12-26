package com.cardmanager.viewmodels

import android.nfc.NfcAdapter
import android.widget.Toast
import androidx.lifecycle.*
import com.cardmanager.data.*
import com.cardmanager.utils.CardUtils
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*

class AttractionsReportsViewModel(private val attractionRepo: AttractionRepository,
                                  private val transactionRepo: TransactionRepository): ViewModel() {

    var attractionsLD = MutableLiveData<List<Attraction>>().apply { value = mutableListOf() }
    val sums = MutableLiveData<MutableMap<Attraction, Double>>().apply { postValue(mutableMapOf()) }
    var chosenTimeLD = MutableLiveData<Date>().apply { value = Date() }
    var totalSum = MutableLiveData<Double>().apply { value = 0.0 }


    fun updateSums(date: Date) {
        viewModelScope.launch {
            val attractions = attractionRepo.getAllAttractions()
            println("Date is ${date.time}")

            totalSum.value = 0.0

            for (elem in attractions) {
                var sum = 0.0

                val transactions = transactionRepo.getTransactionsFromAttractionAndDate(elem.id, date)
                for (transaction in transactions) sum += transaction.amount

                println("transactions are $transactions for $attractions")

                totalSum.value = totalSum.value!!.plus(sum)
                sums.value!![elem] = sum
            }

            attractionsLD.value = attractions
            println("sums is ${sums.value}")
        }
    }

}