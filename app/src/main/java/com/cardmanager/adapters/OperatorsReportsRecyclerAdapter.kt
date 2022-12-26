package com.cardmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.cardmanager.data.*
import com.cardmanager.databinding.AttractionItemBinding
import com.cardmanager.databinding.EmployeeItemBinding
import com.cardmanager.databinding.OperatorsReportsItemBinding
import com.cardmanager.databinding.TransactionItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KSuspendFunction1

class OperatorsReportsRecyclerAdapter(
    lifecycle: LifecycleOwner,
    private val employeesLD: MutableLiveData<List<Employee>>,
    private val sums: MutableLiveData<MutableMap<Employee, Double>>,
    private val employeesAttractions: MutableLiveData<MutableMap<Employee, String>>,
) :
    RecyclerView.Adapter<OperatorsReportsRecyclerAdapter.ListViewHolder>()  {

    init {
        employeesLD.observe(lifecycle) {
            println("observed")
            notifyDataSetChanged()
        }
        sums.observe(lifecycle) {
            println("observed")
            notifyDataSetChanged()
        }
    }

    class ListViewHolder(val binding: OperatorsReportsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = OperatorsReportsItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val employee = employeesLD.value!![position]
        holder.binding.employee = employee

        holder.binding.attractionsText.text = employeesAttractions.value!![employee]

        println("employee sum is ${sums.value!![employee]}")
        holder.binding.amountText.text = "Итого: " + sums.value!![employee].toString()

    }

    override fun getItemCount() = employeesLD.value!!.size
}