package com.vikasjaiswal.unrealfurniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DecorProdRecAdapter: RecyclerView.Adapter<DecorProdRecAdapter.ViewHolder>()
{
    lateinit var strikeText : TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecorProdRecAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.decorprod_recresource, parent, false)

        strikeText = v.findViewById(R.id.decorstrikeText)
        strikeText.paint.isStrikeThruText = true

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: DecorProdRecAdapter.ViewHolder, position: Int) {
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
    {

    }
}