package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton

class FeatProductsRecAdapter : RecyclerView.Adapter<FeatProductsRecAdapter.ViewHolder>() {

    private var itemCountLimit = 4

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val strikeText: TextView = itemView.findViewById(R.id.featStrikeText)

        val selectedProductCard: CardView = itemView.findViewById(R.id.selectedProductCard)

        init{
            strikeText.paint.isStrikeThruText = true

            selectedProductCard.setOnClickListener {
                val intent = Intent(itemView.context, ProductActivity::class.java)
                itemView.context.startActivity(intent)
            }
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
