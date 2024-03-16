package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyOrdersRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<MyOrdersRecAdapter.ViewHolder>() {

    private var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private var orderIds = mutableListOf<String>()
    private var orderStatus = mutableListOf<String>()
    private var orderDates = mutableListOf<String>()
    private var orderExpectedDates = mutableListOf<String>()
    private var orderGrandTotals = mutableListOf<String>()
    private var listener: ListenerRegistration? = null

    init {
        updateData()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderId: TextView = itemView.findViewById(R.id.orderId)
        var orderDate: TextView = itemView.findViewById(R.id.orderDate)
        var orderExpectedDate: TextView = itemView.findViewById(R.id.orderExpectedDate)
        var orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        var orderTotal: TextView = itemView.findViewById(R.id.orderTotal)
        var myOrderCardView: CardView = itemView.findViewById(R.id.myOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.myorders_recresource, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orderIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderId.text = "Order ID: #${orderIds[position]}"
        holder.orderDate.text = "Order Date: ${orderDates[position]}"
        holder.orderExpectedDate.text = "Delivery Date: ${orderExpectedDates[position]}"
        holder.orderStatus.text = "Order Status: ${orderStatus[position]}"
        holder.orderTotal.text = "Order Price: ₹${orderGrandTotals[position]}"

        holder.myOrderCardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", orderIds[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun updateData() {
        val userId = auth.currentUser?.uid
        val orderRef = db.collection("orders")
        val query = orderRef.whereEqualTo("userId", userId)
        listener = query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.w("MyOrdersRecAdapter", "Listen failed", exception)
                return@addSnapshotListener
            }

            orderIds.clear()
            orderStatus.clear()
            orderDates.clear()
            orderExpectedDates.clear()
            orderGrandTotals.clear()

            if (snapshot != null) {
                for (document in snapshot.documents) {
                    orderIds.add(document.id)
                    orderStatus.add(document.getString("orderStatus") ?: "")
                    orderDates.add(document.getString("orderDate") ?: "")
                    orderExpectedDates.add(document.getString("expectedDeliveryDate")?.substring(0, 10) ?: "")
                    orderGrandTotals.add(document.getString("grandTotal") ?: "")
                }
            }

            notifyDataSetChanged()
            onDataChanged.invoke()
        }
    }

    fun stopListening() {
        listener?.remove()
    }
}

