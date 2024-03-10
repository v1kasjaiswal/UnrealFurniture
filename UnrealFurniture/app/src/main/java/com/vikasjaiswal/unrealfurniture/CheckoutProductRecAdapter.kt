package com.vikasjaiswal.unrealfurniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class CheckoutProductRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<CheckoutProductRecAdapter.ViewHolder>() {

    var prodIds = ArrayList<String>()
    var prodImages = ArrayList<String>()
    var prodNames = ArrayList<String>()
    var prodPrices = ArrayList<String>()
    var prodDiscounts = ArrayList<String>()
    var prodDiscountedPrices = ArrayList<String>()
    var prodQuantities = ArrayList<String>()
    var prodRatings = ArrayList<String>()
    var prodRatingCounts = ArrayList<String>()
    var overAllRealPrice = 0.0
    var overAllDiscountedPrice = 0.0
    var overAllDiscount = 0.0
    var overAllQuantity = 0

   fun setData(
       prodIds: ArrayList<String>,
       prodImages: ArrayList<String>,
       prodNames: ArrayList<String>,
       prodPrices: ArrayList<String>,
       prodDiscounts: ArrayList<String>,
       prodDiscountedPrices: ArrayList<String>,
       prodQuantities: ArrayList<String>,
       prodRatings: ArrayList<String>,
       prodRatingCounts: ArrayList<String>
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

        init {
            checkoutProdName = itemView.findViewById(R.id.checkoutProdName)
            checkoutProdMainImage = itemView.findViewById(R.id.checkoutProdMainImage)
            checkoutProdPrice = itemView.findViewById(R.id.checkoutProdRealPrice)
            checkoutProdDiscount = itemView.findViewById(R.id.checkoutProdDiscount)
            checkoutProdDiscountedPrice = itemView.findViewById(R.id.checkoutProdDiscountedPrice)
            checkoutProdQuantity = itemView.findViewById(R.id.checkoutProdQuantity)
            checkoutProdRating = itemView.findViewById(R.id.checkoutProdRatingBar)
            checkoutProdRatingCount = itemView.findViewById(R.id.checkoutProdRatingCounts)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.checkoutproduct_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return prodIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkoutProdName.text = prodNames[position]
        holder.checkoutProdPrice.text = "₹"+(prodPrices[position].toInt()*prodQuantities[position].toInt()).toString()
        holder.checkoutProdDiscount.text = prodDiscounts[position]+"% ↓"
        holder.checkoutProdDiscountedPrice.text = "₹"+(prodDiscountedPrices[position].toInt()*prodQuantities[position].toInt()).toString()
        holder.checkoutProdQuantity.text = "Qty: "+prodQuantities[position]
//        holder.checkoutProdRating.rating = prodRatings[position].toFloat()
//        holder.checkoutProdRatingCount.text = prodRatingCounts[position]

        Picasso.get().load(prodImages[position]).into(holder.checkoutProdMainImage)
    }
}