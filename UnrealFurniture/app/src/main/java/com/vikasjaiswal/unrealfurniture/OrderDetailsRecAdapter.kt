package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat

class OrderDetailsRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<OrderDetailsRecAdapter.ViewHolder>() {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    var prodIds = ArrayList<String>()
    var prodImages = ArrayList<String>()
    var prodNames = ArrayList<String>()
    var prodPrices = ArrayList<Int>()
    var prodDiscounts = ArrayList<Int>()
    var prodDiscountedPrices = ArrayList<Int>()
    var prodQuantities = ArrayList<Int>()
    var prodRatings = ArrayList<String>()
    var prodRatingCounts = ArrayList<Int>()

    lateinit var status : String

    fun setData(
        prodIds: ArrayList<String>,
        prodImages: ArrayList<String>,
        prodNames: ArrayList<String>,
        prodPrices: ArrayList<Int>,
        prodDiscounts: ArrayList<Int>,
        prodDiscountedPrices: ArrayList<Int>,
        prodQuantities: ArrayList<Int>,
        prodRatings: ArrayList<String>,
        prodRatingCounts: ArrayList<Int>
    ) {
        this.prodIds = prodIds
        this.prodImages = prodImages
        this.prodNames = prodNames
        this.prodPrices = prodPrices
        this.prodDiscounts = prodDiscounts
        this.prodDiscountedPrices = prodDiscountedPrices
        this.prodQuantities = prodQuantities
        this.prodRatings = prodRatings
        this.prodRatingCounts = prodRatingCounts
        notifyDataSetChanged()
        onDataChanged.invoke()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var checkoutProdName : TextView
        lateinit var checkoutProdMainImage : ImageView
        lateinit var checkoutProdPrice : TextView
        lateinit var checkoutProdDiscount : TextView
        lateinit var checkoutProdDiscountedPrice : TextView
        lateinit var checkoutProdQuantity : TextView
        lateinit var checkoutProdRating : RatingBar
        lateinit var checkoutProdRatingCount : TextView

        lateinit var checkoutProdCard : CardView

        init {
            checkoutProdName = itemView.findViewById(R.id.checkoutProdName)
            checkoutProdMainImage = itemView.findViewById(R.id.checkoutProdMainImage)
            checkoutProdPrice = itemView.findViewById(R.id.checkoutProdRealPrice)
            checkoutProdDiscount = itemView.findViewById(R.id.checkoutProdDiscount)
            checkoutProdDiscountedPrice = itemView.findViewById(R.id.checkoutProdDiscountedPrice)
            checkoutProdQuantity = itemView.findViewById(R.id.checkoutProdQuantity)
            checkoutProdRating = itemView.findViewById(R.id.checkoutProdRatingBar)
            checkoutProdRatingCount = itemView.findViewById(R.id.checkoutProdRatingCounts)

            checkoutProdPrice.paint.isStrikeThruText = true

            checkoutProdCard = itemView.findViewById(R.id.checkoutProdCard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.checkoutproduct_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return prodIds.size
    }

    fun setOrderStatus(status : String){
        this.status = status
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkoutProdName.text = prodNames[position]
        holder.checkoutProdPrice.text = "₹"+prodPrices[position].toInt()*prodQuantities[position].toInt()
        holder.checkoutProdDiscount.text = prodDiscounts[position].toString()+"% ↓"
        holder.checkoutProdDiscountedPrice.text = "₹"+prodDiscountedPrices[position].toInt()*prodQuantities[position].toInt()
        holder.checkoutProdQuantity.text = "Qnty: "+prodQuantities[position]
        holder.checkoutProdRating.rating = prodRatings[position].toFloat()
        holder.checkoutProdRatingCount.text = "("+prodRatingCounts[position]+")"

        Picasso.get().load(prodImages[position]).into(holder.checkoutProdMainImage)

        holder.checkoutProdCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductActivity::class.java)
            intent.putExtra("productId", prodIds[position])
            holder.itemView.context.startActivity(intent)
        }

        holder.checkoutProdCard.setOnLongClickListener {

            Log.d("OrderDetailsRecAdapter", "onBindViewHolder: "+status)

            if (status.equals("Order Delivered") && auth.currentUser?.email!="unrealadmin@gmail.com"){
                var bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
                val bottomSheetView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.ratingreview_bottomsheet, null)
                bottomSheetDialog.setContentView(bottomSheetView)

                bottomSheetDialog.show()
                var mainImage = bottomSheetView.findViewById<ImageView>(R.id.wishMainImage)
                var prodName = bottomSheetView.findViewById<TextView>(R.id.wishName)
                var prodDiscount = bottomSheetView.findViewById<TextView>(R.id.wishDiscount)
                var prodRating = bottomSheetView.findViewById<RatingBar>(R.id.wishRating)
                var prodRatingCount = bottomSheetView.findViewById<TextView>(R.id.wishRatingCount)
                var prodPrice = bottomSheetView.findViewById<TextView>(R.id.wishPrice)
                var prodDiscountedPrice = bottomSheetView.findViewById<TextView>(R.id.wishDiscountedPrice)

                Picasso.get().load(prodImages[position]).into(mainImage)
                prodName.text = prodNames[position]
                prodDiscount.text = prodDiscounts[position].toString()+"% ↓"
                prodRating.rating = prodRatings[position].toFloat()
                prodRatingCount.text = "("+prodRatingCounts[position]+")"
                prodPrice.text = "₹"+prodPrices[position]
                prodDiscountedPrice.text = "₹"+prodDiscountedPrices[position]

                var rating = bottomSheetView.findViewById<RatingBar>(R.id.ratingBar)
                var review = bottomSheetView.findViewById<TextView>(R.id.reviewText)
                var submit = bottomSheetView.findViewById<TextView>(R.id.ratingReviewSubmit)

                db.collection("products").document(prodIds[position]).collection("RatingReviews").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                    if (it.exists()){
                        rating.rating = it.get("rating").toString().toFloat()
                        review.setText(it.get("review").toString())
                    }
                }

                submit.setOnClickListener{
                    if (rating.rating >=1){
                        if (review.text.toString().isNotEmpty() && review.text.toString().length >= 10){
                            var ratingValue = rating.rating.toFloat()
                            var reviewValue = review.text.toString()

                            db.collection("products").document(prodIds[position]).collection("RatingReviews").document(auth.currentUser?.uid.toString()).set(
                                mapOf(
                                    "rating" to ratingValue,
                                    "review" to reviewValue,
                                    "timestamp" to SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis())
                                )
                            ).addOnSuccessListener {
                                db.collection("products").document(prodIds[position]).collection("RatingReviews").get().addOnSuccessListener {
                                    var ratingSum = 0.0
                                    var ratingCount = 0
                                    for (document in it){
                                        ratingSum += document.get("rating").toString().toDouble()
                                        ratingCount++
                                    }
                                    var avgRating = ratingSum/ratingCount
                                    db.collection("products").document(prodIds[position]).update(
                                        mapOf(
                                            "prodRating" to avgRating,
                                            "prodRatingCount" to ratingCount
                                        )
                                    ).addOnSuccessListener {
                                        Toast.makeText(holder.itemView.context, "Rating and Review Submitted", Toast.LENGTH_SHORT).show()
                                        bottomSheetDialog.dismiss()
                                    }.addOnFailureListener {
                                        Toast.makeText(holder.itemView.context, "Rating and Review Submission Failed", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                Toast.makeText(holder.itemView.context, "Rating and Review Submitted", Toast.LENGTH_SHORT).show()
                                bottomSheetDialog.dismiss()
                            }.addOnFailureListener {
                                Toast.makeText(holder.itemView.context, "Rating and Review Submission Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(holder.itemView.context, "Review should be atleast 10 characters long", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(holder.itemView.context, "Rating must be minimum 1", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }
    }
}