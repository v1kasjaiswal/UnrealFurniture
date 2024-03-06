package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FeatProductsRecAdapter : RecyclerView.Adapter<FeatProductsRecAdapter.ViewHolder>() {

    var productIds = listOf<String>()
    var productMainImages = listOf<String>()
    var productNames = listOf<String>()
    var productRealPrices = listOf<String>()
    var productDiscounts = listOf<String>()
    var productDiscountedPrices = listOf<String>()
    var productRatings = listOf<String>()
    var productRatingCounts = listOf<String>()


    var db = FirebaseFirestore.getInstance()

    var auth = FirebaseAuth.getInstance()

    init {
        updateData()
    }

    fun setData(data: List<String>) {
        productIds = data
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var selectedProductCard : CardView

        lateinit var prodMainImage: ImageView
        lateinit var prodName: TextView
        lateinit var realPrice: TextView
        lateinit var discount: TextView
        lateinit var discountedPrice: TextView
        lateinit var rating: RatingBar
        lateinit var ratingCount: TextView

        lateinit var skeleton: Skeleton
        init {
            selectedProductCard = itemView.findViewById(R.id.selectedProductCard)

            prodMainImage = itemView.findViewById(R.id.featProdMainImage)
            prodName = itemView.findViewById(R.id.featProdName)
            realPrice = itemView.findViewById(R.id.featProdRealPrice)
            discount = itemView.findViewById(R.id.featProdDiscount)
            discountedPrice = itemView.findViewById(R.id.featProdDiscountedPrice)
            rating = itemView.findViewById(R.id.featProdRatingBar)
            ratingCount = itemView.findViewById(R.id.featProdRatingCounts)

            skeleton = itemView.findViewById(R.id.skeletonLayout)

            realPrice.paint.isStrikeThruText = true

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.featprod_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return productIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.skeleton.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            holder.skeleton.showOriginal()
        }, 1000)

        Picasso.get()
            .load(productMainImages[position])
            .placeholder(R.drawable.blank)
            .into(holder.prodMainImage)

        holder.prodName.text = productNames[position]
        holder.realPrice.text = "₹${productRealPrices[position]}"
        holder.discount.text = "${productDiscounts[position]}% ↓"
        holder.discountedPrice.text = "₹${productDiscountedPrices[position]}"
//        holder.rating.rating = productRatings[position].toFloat()
//        holder.ratingCount.text = "(${productRatingCounts[position]})"

        holder.selectedProductCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductActivity::class.java)
            intent.putExtra("productId", productIds[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun updateData(){
        CoroutineScope(Dispatchers.IO).launch {
            try{
            val result = db.collection("products").get().await()

                Log.d("TAG111", "updateData: ${result.documents}")

            for (document in result) {
                productIds += document.id
                Log.d("TAG111", "updateData: ${document.id}")
                productMainImages += document.getString("prodMainImage").toString()
                productNames += document.getString("productName").toString()
                Log.d("TAG111", "updateData: ${document.getString("productName").toString()}")
                productRealPrices += document.getLong("productPrice")?.toString()?: "0"
                productDiscounts += document.getLong("productDiscount")?.toString()?:"0"
                var discountedPrice = document.getLong("productPrice").toString().toInt() - (document.getLong("productPrice").toString().toInt() * document.getLong("productDiscount").toString().toFloat() / 100)
                productDiscountedPrices += discountedPrice.toString()
            }

            withContext(Dispatchers.Main){
                notifyDataSetChanged()
            }
            }catch (e: Exception){
                Log.d("TAG111", "updateData: ${e.message}")
            }
        }
    }


}
