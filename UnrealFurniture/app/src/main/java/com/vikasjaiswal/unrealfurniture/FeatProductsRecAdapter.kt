package com.vikasjaiswal.unrealfurniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeatProductsRecAdapter : RecyclerView.Adapter<FeatProductsRecAdapter.ViewHolder>() {

    private var itemCountLimit = 4

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val strikeText: TextView = itemView.findViewById(R.id.featStrikeText)
        init{
            strikeText.paint.isStrikeThruText = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.featprod_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemCountLimit
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load data for each item here if needed
    }
}
