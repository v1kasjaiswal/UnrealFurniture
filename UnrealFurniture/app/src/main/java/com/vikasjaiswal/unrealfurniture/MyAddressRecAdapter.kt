package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyAddressRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<MyAddressRecAdapter.ViewHolder>() {

    private var docIds = mutableListOf<String>()
    private var names = mutableListOf<String>()
    private var phones = mutableListOf<String>()
    private var houses = mutableListOf<String>()
    private var landmarks = mutableListOf<String>()
    private var regions = mutableListOf<String>()

    private var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private var listener: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun startListening() {
        val query = db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .collection("addresses")

        listener = query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d("TAG", "Error fetching documents: ", exception)
                return@addSnapshotListener
            }

            snapshot?.let {
                docIds.clear()
                names.clear()
                phones.clear()
                houses.clear()
                landmarks.clear()
                regions.clear()

                for (document in snapshot.documents) {
                    docIds.add(document.id)
                    names.add(document.getString("name") ?: "")
                    phones.add(document.getString("phone") ?: "")
                    houses.add(document.getString("house") ?: "")
                    landmarks.add(document.getString("landmark") ?: "")
                    regions.add(document.getString("region") ?: "")
                }

                notifyDataSetChanged()
                onDataChanged.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.myaddress_recresource, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val phone: TextView = itemView.findViewById(R.id.contact)
        private val address: TextView = itemView.findViewById(R.id.address)
        private val addressOptions: ImageView = itemView.findViewById(R.id.addressOptions)

        fun bind(position: Int) {
            name.text = names[position]
            phone.text = "+91 ${phones[position]}"
            address.text = "${houses[position]}, ${landmarks[position]}, ${regions[position]}"

            addressOptions.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, addressOptions)
                popupMenu.inflate(R.menu.address_menu)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.editAddress -> {
                            val fragmentManager = itemView.context as MyAddressActivity
                            fragmentManager.supportFragmentManager.beginTransaction()
                                .add(R.id.fragmentContainerView, AddAddressFragment().apply {
                                    arguments = Bundle().apply {
                                        putString("type", "edit")
                                        putString("docId", docIds[position])
                                        putString("name", names[position])
                                        putString("phone", phones[position])
                                        putString("house", houses[position])
                                        putString("landmark", landmarks[position])
                                        putString("region", regions[position])
                                    }
                                })
                                .addToBackStack(null)
                                .commit()
                            true
                        }
                        R.id.removeAddress -> {
                            removeAddress(position)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    private fun removeAddress(position: Int) {
        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .collection("addresses")
            .document(docIds[position])
            .delete()
            .addOnSuccessListener {
                notifyDataSetChanged()
                onDataChanged.invoke()
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error deleting document: ", exception)
            }
    }

    fun stopListening() {
        listener?.remove()
    }
}
