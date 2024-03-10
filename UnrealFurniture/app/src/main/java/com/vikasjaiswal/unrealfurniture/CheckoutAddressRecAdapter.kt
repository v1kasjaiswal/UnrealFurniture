package com.vikasjaiswal.unrealfurniture

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CheckoutAddressRecAdapter(private val onDataChanged: () -> Unit)  : RecyclerView.Adapter<CheckoutAddressRecAdapter.ViewHolder>() {

    var names = listOf<String>()
    var phones = listOf<String>()
    var houses = listOf<String>()
    var landmarks = listOf<String>()
    var regions = listOf<String>()

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    var selectedName = ""
    var selectedPhone = ""
    var selectedAddress = ""

    private var selectedPosition = -1

    init {
        updateData()
        if (names.isNotEmpty()) {

            selectedPosition = 0
            selectedName = names[0]
            selectedPhone = phones[0]
            selectedAddress = "${houses[0]}, ${landmarks[0]}, ${regions[0]}"
            notifyDataSetChanged()
            onDataChanged.invoke()
        }
    }

    fun setData(data: List<String>) {
        names = data
        notifyDataSetChanged()
        onDataChanged.invoke()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var name: TextView
        lateinit var phone: TextView
        lateinit var address : TextView

        lateinit var addressCardView : MaterialCardView

        init {
            name = itemView.findViewById(R.id.checkoutName)
            phone = itemView.findViewById(R.id.checkoutPhone)
            address = itemView.findViewById(R.id.checkoutAdress)

            addressCardView = itemView.findViewById(R.id.addressCardView)

            addressCardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectItemAtPosition(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkoutaddress_recresource, parent, false)


        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = names[position]
        holder.phone.text = "+91 "+phones[position]
        holder.address.text = "${houses[position]}, ${landmarks[position]}, ${regions[position]}"

        holder.addressCardView.isChecked = position == selectedPosition
    }

    private fun updateData(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("users")
                    .document(auth.currentUser?.uid.toString())
                    .collection("addresses")
                    .get()
                    .await()

                for (document in result) {
                    names = names.plus(document.data["name"].toString())
                    phones = phones.plus(document.data["phone"].toString())
                    houses = houses.plus(document.data["house"].toString())
                    landmarks = landmarks.plus(document.data["landmark"].toString())
                    regions = regions.plus(document.data["region"].toString())
                }

                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }

                if (selectedPosition == -1 && names.isNotEmpty()) {
                    selectItemAtPosition(0)
                }
            } catch (e: Exception) {
                Log.d("TAG", "Error getting documents: ", e)
            }
        }
    }

    private fun selectItemAtPosition(position: Int) {
        selectedPosition = position
        selectedName = names[position]
        selectedPhone = phones[position]
        selectedAddress = "${houses[position]}, ${landmarks[position]}, ${regions[position]}"
        notifyDataSetChanged()
        onDataChanged.invoke()
    }
}