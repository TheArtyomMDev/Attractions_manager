package com.cardmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cardmanager.adapters.OperatorsReportsRecyclerAdapter
import com.cardmanager.databinding.FragmentOperatorsReportsBinding
import com.cardmanager.viewmodels.OperatorsReportsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class OperatorsReportsFragment : Fragment() {

    private var _binding: FragmentOperatorsReportsBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModel<OperatorsReportsViewModel>()
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOperatorsReportsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        vm.chosenTimeLD.observe(viewLifecycleOwner) { time ->
            calendar.time = time
            binding.datePickerText.text =
                "${calendar[Calendar.DATE]}.${calendar[Calendar.MONTH] + 1}.${calendar[Calendar.YEAR]}"

            println("Time changed to $time")
            vm.updateSums(time)
        }

        vm.totalSum.observe(viewLifecycleOwner) {
            binding.totalSumText.text = "Итого: $it"
        }

        binding.datePickerText.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            val supportFragmentManager = childFragmentManager

            datePicker.addOnPositiveButtonClickListener {
                println("date picked")
                vm.chosenTimeLD.value = Date(it)
            }

            datePicker.show(supportFragmentManager, "tag")
        }

        binding.recView.adapter = OperatorsReportsRecyclerAdapter(
            viewLifecycleOwner, vm.employeesLD, vm.sums, vm.employeesAttractions)
        binding.recView.layoutManager = LinearLayoutManager(requireContext())

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}