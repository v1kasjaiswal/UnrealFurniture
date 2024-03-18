package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RatingReviewsRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<RatingReviewsRecAdapter.ViewHolder>() {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    var uid = auth.currentUser?.uid.toString()

    var userIds = listOf<String>()
    var userNames = listOf<String>()
    var userImages = listOf<String>()
    var ratings = listOf<String>()
    var reviews = listOf<String>()
    var reviewDates = listOf<String>()

    var prodId = ""

    init {
        Log.d("ProdIDInit", prodId)
        if (prodId != "") {
            updateData(prodId)
        }
    }

    fun setData(data: List<String>) {
        userIds = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var ratingReviewName: TextView
        lateinit var ratingReviewImage: ImageView
        lateinit var ratingReviewRatingBar: RatingBar
        lateinit var ratingReviewText: TextView
        lateinit var ratingReviewDate: TextView

        init{
            ratingReviewName = itemView.findViewById(R.id.ratingReviewName)
            ratingReviewImage = itemView.findViewById(R.id.ratingReviewImage)
            ratingReviewRatingBar = itemView.findViewById(R.id.ratingReviewRatingBar)
            ratingReviewText = itemView.findViewById(R.id.ratingReviewText)
            ratingReviewDate = itemView.findViewById(R.id.ratingReviewDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.ratingreview_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ratingReviewName.text = userNames[position]
        holder.ratingReviewText.text = reviews[position]
        holder.ratingReviewDate.text = reviewDates[position]
        holder.ratingReviewRatingBar.rating = ratings[position].toFloat()

        Picasso
            .get()
            .load(userImages[position])
            .placeholder(R.drawable.user)
            .into(holder.ratingReviewImage)
    }

    fun updateData(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch rating reviews
                val ratingReviewsSnapshot = db.collection("products").document(id).collection("RatingReviews").get().await()

                // Extracting data from the rating reviews snapshot
                userIds = ratingReviewsSnapshot.documents.map { it.id }
                ratings = ratingReviewsSnapshot.documents.map { it.getDouble("rating").toString() }
                reviews = ratingReviewsSnapshot.documents.map { it.getString("review").toString() }
                reviewDates = ratingReviewsSnapshot.documents.map { it.getString("timestamp").toString() }

                // Fetching users
                val usersSnapshot = db.collection("users").get().await()

                // Extracting user information
                val userNamesMap = usersSnapshot.documents.associateBy({ it.id }, { it.getString("username") })
                val userImagesMap = usersSnapshot.documents.associateBy({ it.id }, { it.getString("userimage") })

                // Mapping user IDs to their respective names and images
                userNames = userIds.map { userNamesMap[it].toString() }
                userImages = userIds.map { userImagesMap[it].toString() }

                // Logging retrieved data for debugging
                Log.d("RatingReviews", userIds.toString())
                Log.d("RatingReviews", ratings.toString())
                Log.d("RatingReviews", reviews.toString())
                Log.d("RatingReviews", reviewDates.toString())
                Log.d("RatingReviews", userNames.toString())
                Log.d("RatingReviews", userImages.toString())

                // Notify data change on the main thread
                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }
            } catch (e: Exception) {
                // Handle any exceptions
                Log.e("RatingReviews", "Error fetching data: ${e.message}", e)
            }
        }
    }
}