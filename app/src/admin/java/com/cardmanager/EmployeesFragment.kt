package com.cardmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cardmanager.adapters.EmployeesRecyclerAdapter
import com.cardmanager.data.Employee
import com.cardmanager.databinding.FragmentEmployeesBinding
import com.cardmanager.viewmodels.EmployeesViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class EmployeesFragment : Fragment() {

    private var _binding: FragmentEmployeesBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by inject()
    lateinit var auth2: FirebaseAuth
    private val vm by viewModel<EmployeesViewModel>()


    private val firebaseOptions = FirebaseOptions.Builder()
        .setDatabaseUrl("attractions-123.appspot.com")
        .setApiKey("AIzaSyBzh91pGc1X6sUOnl0wUcSjm-lw0Yq2cLg")
        .setApplicationId("attractions-123").build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEmployeesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth2 = try {
            val myApp = FirebaseApp.initializeApp(requireContext(),
                firebaseOptions,
                "AnyAppName")
            FirebaseAuth.getInstance(myApp)
        } catch (e: IllegalStateException) {
            FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"))
        }

        println(auth.currentUser!!.email)

        binding.addEmployeeButton.setOnClickListener {
            val res = vm.createUser(vm.email.value!!, vm.password.value!!, auth2)
            Toast.makeText(requireContext(), res, Toast.LENGTH_SHORT).show()

            binding.emailEditText.setText("")
            binding.passwordEditText.setText("")
        }

        binding.emailEditText.addTextChangedListener(afterTextChanged = {
            vm.email.postValue(it.toString())
        })

        binding.passwordEditText.addTextChangedListener(afterTextChanged = {
            vm.password.postValue(it.toString())
        })

        vm.email.observe(viewLifecycleOwner) {
            binding.addEmployeeButton.isEnabled = checkEmailAndPassword(it, vm.password.value)
        }

        vm.password.observe(viewLifecycleOwner) {
            binding.addEmployeeButton.isEnabled = checkEmailAndPassword(vm.email.value, it)
        }

        binding.recView.adapter = EmployeesRecyclerAdapter(viewLifecycleOwner,
            vm.employeesLD, ::deleteEmployee)
        binding.recView.layoutManager = LinearLayoutManager(requireContext())


        return root
    }

    private fun deleteEmployee(employee: Employee) {

    }

    private fun checkEmailAndPassword(email: String?, password: String?): Boolean {
        if (password != null && email != null) {
            if(password.isEmpty() || email.isEmpty()) return false
        } else return false

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 6
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}