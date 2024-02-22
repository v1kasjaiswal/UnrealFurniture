package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RatingReviewsRecAdapter : RecyclerView.Adapter<RatingReviewsRecAdapter.ViewHolder>() {

    private var itemCountLimit = 4

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.ratingreview_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemCountLimit
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load data for each item here if needed
    }
}