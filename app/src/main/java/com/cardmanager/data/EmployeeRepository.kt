package com.cardmanager.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

import java.util.*

class EmployeeRepository(private val db: FirebaseFirestore,
                        private  val transactionRepo: TransactionRepository) {
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

    suspend fun getAllEmployees(): List<Employee> {
        val data = db.collection("employees").get().await()
        val employees = mutableListOf<Employee>()

        //println(data.documents[0])
        for(document in data.documents) {
            val employee = document.toObject(Employee::class.java)
            println(employee)
            employees.add(employee!!)
        }

        return employees.toList()
    }

    suspend fun addEmployee(employee: Employee): Boolean {
        db.collection("employees").document(employee.id).set(employee).await()
        return true
    }

    fun deleteEmployee(employee: Employee) {
        db.collection("employees").document(employee.id).delete()
    }

    suspend fun getAllOperators(): List<Employee> {
        val operators = mutableListOf<Employee>()

        val transactions = transactionRepo.getAllTransactions()
        for(transaction in transactions) {
            if(transaction.type == TRANSACTION_TYPE.SPEND &&
                Employee(transaction.employeeId) !in operators) operators.add(Employee(transaction.employeeId))
        }

        print("operators - $operators")

        return operators.toList()
    }

}