package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.regex.Pattern

class OrderDetailsActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    lateinit var detailOrderId : TextView
    lateinit var detailOrderDate : TextView
    lateinit var detailOrderDeliveryDate : TextView
    lateinit var detailOrderStatus : TextView

    lateinit var detailUserName : TextView
    lateinit var detailUserContact : TextView
    lateinit var detailUserEmail : TextView
    lateinit var detailUserAddress : TextView

    lateinit var orderDetailsRecycler : RecyclerView

    lateinit var overAllRealPrice : TextView
    lateinit var overAllDiscount : TextView
    lateinit var overAllDiscountedPrice : TextView

    lateinit var grandTotal : TextView

    lateinit var detailPaymentMethod : TextView
    lateinit var paymentStatus : TextView

    lateinit var cancelOrder : Button

    lateinit var detailRatingBar : RatingBar
    lateinit var orderReview : TextView

    lateinit var orderUpdateLayout : TextInputLayout
    lateinit var orderUpdate : AutoCompleteTextView

    lateinit var ratingReviewSubmit : Button

    lateinit var orderDetailsRecyclerView: RecyclerView

    private lateinit var orderDetailsLayoutManager: GridLayoutManager

    private var orderDetailsAdapter: OrderDetailsRecAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orderdetails_activity)

        val orderId = intent.getStringExtra("orderId")

        Log.d("OrderDetailsActivity", "Order ID: $orderId")

        detailOrderId = findViewById(R.id.detailOrderId)
        detailOrderDate = findViewById(R.id.detailOrderDate)
        detailOrderDeliveryDate = findViewById(R.id.detailOrderDeliveryDate)
        detailOrderStatus = findViewById(R.id.detailOrderStatus)

        detailUserName = findViewById(R.id.detailUserName)
        detailUserContact = findViewById(R.id.detailUserContact)

        detailUserEmail = findViewById(R.id.detailUserEmail)
        detailUserAddress = findViewById(R.id.detailUserAddress)

        orderDetailsRecycler = findViewById(R.id.orderDetailsRecycler)

        overAllRealPrice = findViewById(R.id.overAllRealPrice)
        overAllDiscount = findViewById(R.id.overAllDiscount)
        overAllDiscountedPrice = findViewById(R.id.overAllDiscountedPrice)

        grandTotal = findViewById(R.id.grandTotal)

        detailPaymentMethod = findViewById(R.id.detailPaymentMethod)
        paymentStatus = findViewById(R.id.paymentStatus)

        cancelOrder = findViewById(R.id.cancelOrder)

        detailRatingBar = findViewById(R.id.detailRatingBar)
        orderReview = findViewById(R.id.orderReview)

        ratingReviewSubmit = findViewById(R.id.ratingReviewSubmit)

        orderDetailsRecyclerView = findViewById(R.id.orderDetailsRecycler)

        orderDetailsLayoutManager = GridLayoutManager(this, 2)

        orderDetailsRecyclerView.layoutManager = orderDetailsLayoutManager

        orderDetailsAdapter = OrderDetailsRecAdapter{
            if (orderDetailsAdapter!!.itemCount == 0) {
                orderDetailsRecyclerView.visibility = View.GONE
            } else {
                orderDetailsRecyclerView.visibility = View.VISIBLE
            }
        }

        orderDetailsRecyclerView.adapter = orderDetailsAdapter

        orderUpdateLayout = findViewById(R.id.orderUpdateLayout)
        orderUpdate = findViewById(R.id.orderUpdate)

        if (auth.currentUser?.email == "unrealadmin@gmail.com"){
            cancelOrder.visibility = View.GONE
            ratingReviewSubmit.visibility = View.GONE
            orderUpdateLayout.visibility = View.VISIBLE
        }
        else{
            cancelOrder.visibility = View.VISIBLE
            ratingReviewSubmit.visibility = View.VISIBLE
            orderUpdateLayout.visibility = View.GONE
        }

        orderUpdate.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.order_status)
            )
        )
        
        orderUpdate.setOnItemClickListener { parent, view, position, id ->
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("orders").document(orderId!!).update("orderStatus", orderUpdate.text.toString()).addOnSuccessListener {
                    runOnUiThread {
                        Toast.makeText(this@OrderDetailsActivity, "Order Status Updated", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            db.collection("orders").document(orderId!!).get().addOnSuccessListener {
                detailOrderId.text = "Order ID: #"+it.id
                detailOrderDate.text = "Order Date: "+it.getString("orderDate")
                detailOrderDeliveryDate.text = "Delivery Date: "+it.getString("expectedDeliveryDate")?.substring(0, 10)
                detailOrderStatus.text = "Order Status: "+it.getString("orderStatus")

                detailUserName.text = "Name: "+it.getString("userName")
                detailUserContact.text = "Contact: "+it.getString("selectedPhone")
                detailUserEmail.text = "Email: "+it.getString("userEmail")
                detailUserAddress.text = "Address: "+it.getString("selectedAddres")

                overAllRealPrice.text = "₹"+it.getString("overAllPrice")
                overAllDiscount.text = "₹"+it.getString("overAllDiscount")
                overAllDiscountedPrice.text = "₹"+it.getString("overAllDiscountedPrice")

                grandTotal.text = "₹"+it.getString("grandTotal")

                detailPaymentMethod.text = it.getString("paymentMethod")
                paymentStatus.text = it.getString("paymentStatus") ?: "Pending"

                if (it.getString("orderStatus") == "Order Placed") {
                    cancelOrder.isEnabled = true
                } else {
                    cancelOrder.isEnabled = false
                }

                if (it.getString("orderStatus") == "Order Delivered") {
                    detailRatingBar.isEnabled = true
                    orderReview.isEnabled = true
                    ratingReviewSubmit.isEnabled = true

                    detailRatingBar.rating = it.getString("orderRating")?.toFloat() ?: 0f
                    orderReview.setText(it.getString("orderReview"))
                } else {
                    detailRatingBar.isEnabled = false
                    orderReview.isEnabled = false
                    ratingReviewSubmit.isEnabled = false
                }

                val prodIds = it.get("prodIds") as ArrayList<String>
                val prodImages = it.get("prodImages") as ArrayList<String>
                val prodNames = it.get("prodNames") as ArrayList<String>
                val prodPrices = it.get("prodPrices") as  ArrayList<Int>
                val prodDiscounts = it.get("prodDiscounts") as ArrayList<Int>
                val prodDiscountedPrices = it.get("prodDiscountedPrices") as ArrayList<Int>
                val prodQuantities = it.get("prodQuantities") as ArrayList<Int>
                val prodRatings = it.get("prodRatings") as ArrayList<String>
                val prodRatingCounts = it.get("prodRatingCounts") as ArrayList<Int>

                orderDetailsAdapter!!.setData(
                    prodIds,
                    prodImages,
                    prodNames,
                    prodPrices,
                    prodDiscounts,
                    prodDiscountedPrices,
                    prodQuantities,
                    prodRatings,
                    prodRatingCounts
                )
            }
        }

        cancelOrder.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("orders").document(orderId!!).update("orderStatus", "Order Cancelled").addOnSuccessListener {
                    finish()
                }
            }
        }

        ratingReviewSubmit.setOnClickListener {
            val rating = detailRatingBar.rating
            val review = orderReview.text.toString()

            if (rating >= 1f ){
                if (Pattern.matches("^[a-zA-Z0-9]*$", review)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.collection("orders").document(orderId!!).update("orderRating", rating.toString()).addOnSuccessListener {
                            db.collection("orders").document(orderId).update("orderReview", review).addOnSuccessListener {
                                finish()
                                runOnUiThread {
                                    Toast.makeText(this@OrderDetailsActivity, "Review Submitted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    orderReview.error = "Invalid Review"
                    orderReview.requestFocus()
                }
            }
            else{
                Toast.makeText(this@OrderDetailsActivity,  "Minimum 1 Star is required to Submit Review", Toast.LENGTH_SHORT).show()
            }
        }
    }
}