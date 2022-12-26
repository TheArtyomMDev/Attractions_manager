package com.cardmanager.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

import java.util.*

class TransactionRepository(private val db: FirebaseFirestore) {

    //val transactionsComparator = Comparator {p1: Transaction, p2: Transaction -> (p1.timestamp - p2.timestamp).toInt() }

    suspend fun addNewTransaction(transaction: Transaction): Boolean {
        db.collection("transactions").document().set(transaction).await()
        return true
    }

    suspend fun getAllTransactions(): List<Transaction> {
        val data = db.collection("transactions").get().await()
        var transactions = mutableListOf<Transaction>()

        //println(data.documents[0])
        for(document in data.documents) {
            val transaction = document.toObject(Transaction::class.java)
            println(transaction)
            transactions.add(transaction!!)
        }

        transactions = transactions.sortedWith(compareBy { it.timestamp }).toMutableList()
        return transactions.toList().reversed()
    }

    suspend fun getTransactionsFromCard(id: String): List<Transaction> {
        val transactions = getAllTransactions()
        val ret = mutableListOf<Transaction>()

        for(transaction in transactions) {
            if(transaction.cardId == id) ret.add(transaction)
        }

        return ret.toList()
    }

    suspend fun getTransactionsFromEmployee(id: String): List<Transaction> {
        val transactions = getAllTransactions()
        val ret = mutableListOf<Transaction>()

        for(transaction in transactions) {
            if(transaction.employeeId == id) ret.add(transaction)
        }

        return ret.toList()
    }

    suspend fun getTransactionsFromAttraction(id: String): List<Transaction> {
        val transactions = getAllTransactions()
        val ret = mutableListOf<Transaction>()

        for(transaction in transactions) {
            if(transaction.attractionId == id) ret.add(transaction)
        }

        return ret.toList()
    }

    suspend fun getTransactionsFromEmployeeAndDate(id: String, date: Date): List<Transaction> {
        val calendar = Calendar.getInstance()
        val milliSecondsInOneDay = 24 * 60 * 60 * 1000

        calendar.time = date
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        val dayStartTime = calendar.time.time
        val dayEndTime = dayStartTime + milliSecondsInOneDay

        val transactions = getTransactionsFromEmployee(id)
        val ret = mutableListOf<Transaction>()

        for(transaction in transactions) {
            if(transaction.timestamp in dayStartTime until dayEndTime) ret.add(transaction)
        }

        return ret.toList()
    }

    suspend fun getTransactionsFromAttractionAndDate(id: String, date: Date): List<Transaction> {
        val calendar = Calendar.getInstance()
        val milliSecondsInOneDay = 24 * 60 * 60 * 1000

        calendar.time = date
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        val dayStartTime = calendar.time.time
        val dayEndTime = dayStartTime + milliSecondsInOneDay

        val transactions = getTransactionsFromAttraction(id)
        val ret = mutableListOf<Transaction>()

        for(transaction in transactions) {
            if(transaction.timestamp in dayStartTime until dayEndTime) ret.add(transaction)
        }

        return ret.toList()
    }

    suspend fun getMoneyFromAttractionEmployeeAndDate(attractionId: String, employeeId: String, date: Date): List<Transaction> {
        val transactions = getTransactionsFromAttractionAndDate(attractionId, date)
        val ret = mutableListOf<Transaction>()

        for(transaction in transactions) {
            if(transaction.employeeId == employeeId) ret.add(transaction)
        }

        return ret.toList()
    }

}