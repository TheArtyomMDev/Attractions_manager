package com.cardmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.cardmanager.data.*
import com.cardmanager.databinding.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KSuspendFunction1

class AttractionsReportsRecyclerAdapter(
    lifecycle: LifecycleOwner,
    private val attractionsLD: MutableLiveData<List<Attraction>>,
    private val sums: MutableLiveData<MutableMap<Attraction, Double>>,
) :
    RecyclerView.Adapter<AttractionsReportsRecyclerAdapter.ListViewHolder>()  {

    init {
        attractionsLD.observe(lifecycle) {
            println("observed")
            notifyDataSetChanged()
        }
        sums.observe(lifecycle) {
            println("observed")
            notifyDataSetChanged()
        }
    }

    class ListViewHolder(val binding: AttractionsReportsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = AttractionsReportsItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val attraction = attractionsLD.value!![position]
        holder.binding.attraction = attraction

        println("attraction sum is ${sums.value!![attraction]}")
        val totalAmount = sums.value!![attraction]
        holder.binding.amountText.text =
            "${(totalAmount!!/attraction.price).toInt()} * ${attraction.price} = $totalAmount"

    }

    override fun getItemCount() = attractionsLD.value!!.size
}