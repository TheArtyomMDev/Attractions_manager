package com.cardmanager.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

import java.util.*

class CardRepository(private val db: FirebaseFirestore,
                     private val transactionRepo: TransactionRepository,
                     private val auth: FirebaseAuth) {

    suspend fun getCard(id: String): Card? {
        val cardData = db.collection("cards").document(id).get().await()

        return if(cardData.exists()) {
            cardData.toObject(Card::class.java)
        } else null
    }

    suspend fun addCard(id: String): Boolean {
        db.collection("cards").document(id).set(Card(id, 0.0)).await()
        return true
    }

    suspend fun addMoney(id: String, amount: Double): Boolean {
        val card = getCard(id)!!
        card.balance += amount

        db.collection("cards").document(id).set(card).await()

        transactionRepo.addNewTransaction(Transaction(
            amount = amount,
            cardId = id,
            type = TRANSACTION_TYPE.TOPUP,
            employeeId = auth.currentUser!!.email!!,
            timestamp = Calendar.getInstance().timeInMillis,
            attractionId = "0"
        ))

        return true
    }

    suspend fun refundMoney(id: String, amount: Double): Boolean {
        val card = getCard(id)!!
        card.balance -= amount

        db.collection("cards").document(id).set(card).await()

        transactionRepo.addNewTransaction(Transaction(
            amount = amount,
            cardId = id,
            type = TRANSACTION_TYPE.REFUND,
            employeeId = auth.currentUser!!.email!!,
            timestamp = Calendar.getInstance().timeInMillis,
            attractionId = "0"
        ))

        return true
    }

    suspend fun spendMoney(id: String, attraction: Attraction): Boolean {
        val card = getCard(id)!!
        card.balance -= attraction.price

        db.collection("cards").document(id).set(card).await()

        transactionRepo.addNewTransaction(Transaction(
            amount = attraction.price,
            cardId = id,
            type = TRANSACTION_TYPE.SPEND,
            employeeId = auth.currentUser!!.email!!,
            timestamp = Calendar.getInstance().timeInMillis,
            attractionId = attraction.id
        ))

        return true
    }

}