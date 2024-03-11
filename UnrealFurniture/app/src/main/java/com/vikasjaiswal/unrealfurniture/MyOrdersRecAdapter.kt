package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.media.Rating
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class MyOrdersRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<MyOrdersRecAdapter.ViewHolder>() {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    var orderIds = listOf<String>()
    var orderStatus = listOf<String>()
    var orderDates = listOf<String>()
    var orderExpectedDates = listOf<String>()
    var orderGrandTotals = listOf<String>()

    init {
        updateData()
    }

    fun setData(data : List<String>) {
        orderIds = data
        notifyDataSetChanged()
        onDataChanged.invoke()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var orderId : TextView
        lateinit var orderDate : TextView
        lateinit var orderExpectedDate : TextView
        lateinit var orderStatus : TextView
        lateinit var orderTotal : TextView

        lateinit var myOrderCardView : CardView

        init {
            orderId = itemView.findViewById(R.id.orderId)
            orderDate = itemView.findViewById(R.id.orderDate)
            orderExpectedDate = itemView.findViewById(R.id.orderExpectedDate)
            orderStatus = itemView.findViewById(R.id.orderStatus)
            orderTotal = itemView.findViewById(R.id.orderTotal)

            myOrderCardView = itemView.findViewById(R.id.myOrder)
        }
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
        holder.orderId.text = "Order ID: #"+orderIds[position]
        holder.orderDate.text = "Order Date: "+orderDates[position]
        holder.orderExpectedDate.text = "Delivery Date: "+orderExpectedDates[position]
        holder.orderStatus.text = "Order Status: "+orderStatus[position]
        holder.orderTotal.text = "Order Price: ₹"+orderGrandTotals[position]

        holder.myOrderCardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", orderIds[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun updateData(){
        CoroutineScope(Dispatchers.IO).launch {
            val userId = auth.currentUser?.uid
            val orderRef = db.collection("orders")
            val query = orderRef.whereEqualTo("userId", userId)
            query.get().addOnSuccessListener { documents ->
                if (documents != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${documents.documents}")
                    for (document in documents) {
                        orderIds += document.id
                        orderStatus += document.getString("orderStatus").toString()
                        orderDates += document.getString("orderDate").toString()
                        orderExpectedDates += document.getString("expectedDeliveryDate").toString()
                            .toString()
                        orderGrandTotals += document.getString("grandTotal").toString()

                    }
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }
            }
        }
    }
}
