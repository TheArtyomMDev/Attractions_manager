package com.cardmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.cardmanager.data.Attraction
import com.cardmanager.data.AttractionRepository
import com.cardmanager.data.TRANSACTION_TYPE
import com.cardmanager.data.Transaction
import com.cardmanager.databinding.AttractionItemBinding
import com.cardmanager.databinding.TransactionItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KSuspendFunction1

class TransactionsRecyclerAdapter(
    lifecycle: LifecycleOwner,
    private val transactionsLD: LiveData<List<Transaction>>,
    private val attractions: Map<String, Attraction>,
) :
    RecyclerView.Adapter<TransactionsRecyclerAdapter.ListViewHolder>()  {

    init {
        transactionsLD.observe(lifecycle) {
            println("observed")
            notifyDataSetChanged()
        }
    }

    class ListViewHolder(val binding: TransactionItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = TransactionItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val transaction = transactionsLD.value!![position]
        holder.binding.transaction = transaction

        holder.binding.attractionNameText.text = "Загузка имени аттракциона"

        when (transaction.type) {
            TRANSACTION_TYPE.SPEND -> {
                holder.binding.attractionNameText.text = attractions[transaction.attractionId]?.name
            }
            TRANSACTION_TYPE.REFUND -> {
                holder.binding.attractionNameText.text = "Выдача средств клиенту"
            }
            TRANSACTION_TYPE.TOPUP -> holder.binding.attractionNameText.visibility = View.GONE
        }

    }

    override fun getItemCount() = transactionsLD.value!!.size
}