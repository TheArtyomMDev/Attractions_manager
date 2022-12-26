package com.cardmanager

import android.app.AlertDialog
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cardmanager.adapters.AttractionsRecyclerAdapter
import com.cardmanager.adapters.EmployeesRecyclerAdapter
import com.cardmanager.data.Attraction
import com.cardmanager.databinding.FragmentAttractionsBinding
import com.cardmanager.viewmodels.AttractionsViewModel
import com.cardmanager.viewmodels.EmployeesViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AttractionsFragment : Fragment() {

    private var _binding: FragmentAttractionsBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModel<AttractionsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAttractionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recView.adapter = AttractionsRecyclerAdapter(viewLifecycleOwner,
            vm.attractionsLD, vm.chosenAttraction, requireContext(), false, ::deleteAttraction)
        binding.recView.layoutManager = LinearLayoutManager(requireContext())

        binding.nameEditText.addTextChangedListener(afterTextChanged = {
            vm.name.postValue(it.toString())
        })

        binding.priceEditText.addTextChangedListener(afterTextChanged = {
            if(it.toString().isNotEmpty()) vm.price.postValue(it.toString().toDouble())
            else vm.price.postValue(0.0)
        })

        binding.addAttractionButton.setOnClickListener {
            lifecycleScope.launch {
                val res = vm.addAttraction(Attraction(vm.name.value!!, vm.price.value!!))
                Toast.makeText(requireContext(), res, Toast.LENGTH_SHORT).show()

                binding.nameEditText.setText("")
                binding.priceEditText.setText("")
            }
        }

        vm.name.observe(viewLifecycleOwner) {
            binding.addAttractionButton.isEnabled = checkAttraction(it, vm.price.value)
        }

        vm.price.observe(viewLifecycleOwner) {
            binding.addAttractionButton.isEnabled = checkAttraction(vm.name.value, it)
        }

        return root
    }

    private fun deleteAttraction(attraction: Attraction) {
        lifecycleScope.launch {
            vm.deleteAttraction(attraction)
            Toast.makeText(requireContext(), "Удаление успешно", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAttraction(name: String?, price: Double?): Boolean {
        if(name != null && price != null) {
            if(name.isEmpty() || price <= 0.0) return false
        } else return false

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}