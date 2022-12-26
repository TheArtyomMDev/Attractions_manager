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

class OperatorsReportsViewModel(private val employeeRepo: EmployeeRepository,
                                private val transactionRepo: TransactionRepository,
                                private val attractionRepo: AttractionRepository): ViewModel() {

    var employeesLD = MutableLiveData<List<Employee>>().apply { value = mutableListOf() }
    val sums = MutableLiveData<MutableMap<Employee, Double>>().apply { postValue(mutableMapOf()) }
    var chosenTimeLD = MutableLiveData<Date>().apply { value = Date() }
    var totalSum = MutableLiveData<Double>().apply { value = 0.0 }
    var employeesAttractions = MutableLiveData<MutableMap<Employee, String>>().apply { value = mutableMapOf() }

    fun updateSums(date: Date) {
        viewModelScope.launch {
            val operators = employeeRepo.getAllOperators()

            println("Date is ${date.time}")

            totalSum.value = 0.0

            for (elem in operators) {
                var sum = 0.0
                var attractionsOfEmployee = mutableMapOf<String, Double>()

                val transactions = transactionRepo.getTransactionsFromEmployeeAndDate(elem.id, date)
                for (transaction in transactions) {
                    sum += transaction.amount
                    if(transaction.attractionId in attractionsOfEmployee.keys)

                        attractionsOfEmployee[transaction.attractionId] =
                            attractionsOfEmployee[transaction.attractionId]!! + transaction.amount

                    else
                        attractionsOfEmployee[transaction.attractionId] = transaction.amount
                }

                var resStr = ""
                for (attractionId in attractionsOfEmployee.keys) {
                    val attraction = attractionRepo.getAttractionById(attractionId)
                    val totalSum = attractionsOfEmployee[attractionId]

                    resStr += "${attraction!!.name} : ${(totalSum!!/attraction.price).toInt()} * ${attraction.price} = $totalSum\n"
                }
                employeesAttractions.value!![elem] = resStr

                println("transactions are $transactions for $operators")

                totalSum.value = totalSum.value!!.plus(sum)
                sums.value!![elem] = sum
            }

            employeesLD.value = operators
            println("sums is ${sums.value}")
        }
    }

}