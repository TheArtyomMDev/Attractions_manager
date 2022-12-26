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

class EmployeesViewModel(private val employeeRepo: EmployeeRepository): ViewModel() {

    var employeesLD = MutableLiveData<List<Employee>>().apply { value = listOf() }

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()


    init {
        viewModelScope.launch {
            employeesLD.value = employeeRepo.getAllEmployees()
        }
    }

    fun createUser(email: String, password: String, auth: FirebaseAuth): String {

        runBlocking {
            auth.createUserWithEmailAndPassword(email, password).await()
            employeeRepo.addEmployee(Employee(email))
        }

        return  "Успешно добавлен"
    }
}