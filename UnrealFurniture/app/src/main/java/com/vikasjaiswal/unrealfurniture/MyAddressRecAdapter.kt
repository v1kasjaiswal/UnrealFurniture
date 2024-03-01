package com.vikasjaiswal.unrealfurniture

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyAddressRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<MyAddressRecAdapter.ViewHolder>() {

    var names = listOf<String>()
    var phones = listOf<String>()
    var houses = listOf<String>()
    var landmarks = listOf<String>()
    var regions = listOf<String>()

    var db = FirebaseFirestore.getInstance()

    var auth = FirebaseAuth.getInstance()

    init {
        updateData()
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


        init {
            name = itemView.findViewById(R.id.name)
            phone = itemView.findViewById(R.id.contact)
            address = itemView.findViewById(R.id.address)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.myaddress_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        Log.d("TAG111", "getItemCount: $names")
        return names.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = names[position]
        holder.phone.text = "+91 "+phones[position]
        holder.address.text = "${houses[position]}, ${landmarks[position]}, ${regions[position]}"
    }

    fun updateData(){

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
            } catch (e: Exception) {
                Log.d("TAG", "Error getting documents: ", e)
            }
        }
    }
}
