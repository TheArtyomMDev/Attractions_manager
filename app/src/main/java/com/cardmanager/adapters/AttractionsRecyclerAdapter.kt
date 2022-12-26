package com.cardmanager.adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.cardmanager.R
import com.cardmanager.data.Attraction
import com.cardmanager.databinding.AttractionItemBinding

class AttractionsRecyclerAdapter(
    lifecycle: LifecycleOwner,
    private val attractionsLD: LiveData<List<Attraction>>,
    private val chosenAttractionLD: MutableLiveData<Attraction>,
    private val context: Context,
    private var isChoosingRequired: Boolean = true,
    private val deleteAttraction: (attraction: Attraction) -> Unit = fun (_) {}) :

    RecyclerView.Adapter<AttractionsRecyclerAdapter.ListViewHolder>()  {

    private val primaryColour = TypedValue()
    private val surfaceColour = TypedValue()

    init {
        attractionsLD.observe(lifecycle) {
            println("observed new attractions")
            notifyDataSetChanged()
        }
        chosenAttractionLD.observe(lifecycle) {
            println("observed chosen attraction")
            notifyDataSetChanged()
        }
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, primaryColour, true)
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, surfaceColour, true)
    }

    class ListViewHolder(val binding: AttractionItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = AttractionItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        if(chosenAttractionLD.value!!.id == "") chosenAttractionLD.postValue(attractionsLD.value!![position])

        holder.binding.attraction = attractionsLD.value!![position]

        if(isChoosingRequired) {
            holder.binding.cardContainer.setOnClickListener {
                chosenAttractionLD.value = attractionsLD.value!![position]
                println("New value is ${chosenAttractionLD.value}")
            }

            if (chosenAttractionLD.value == attractionsLD.value!![position]) {
                holder.binding.cardContainer.setBackgroundColor(primaryColour.data)
            } else holder.binding.cardContainer.setBackgroundColor(surfaceColour.data)
        }

        holder.binding.delete.setOnClickListener {
            deleteAttraction(attractionsLD.value!![position])
        }

    }

    override fun getItemCount() = attractionsLD.value!!.size
}