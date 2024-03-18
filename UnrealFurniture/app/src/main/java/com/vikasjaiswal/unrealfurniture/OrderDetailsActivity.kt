package com.vikasjaiswal.unrealfurniture

import android.content.IntentFilter
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.util.regex.Pattern

class OrderDetailsActivity : AppCompatActivity() {


    lateinit var networkReceiver : CheckConnectivity

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

        networkReceiver = CheckConnectivity()

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

        db.collection("orders").document(orderId!!).get().addOnSuccessListener {
            detailRatingBar.rating = it.getString("orderRating")?.toFloat() ?: 0f
            orderReview.text = it.getString("orderReview")
        }

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
                db.collection("orders").document(orderId!!).update(
                    mapOf(
                        "orderStatus" to orderUpdate.text.toString(),
                        "paymentStatus" to "Paid"
                    )
                ).addOnSuccessListener {

                    var token = ""
                    var userName = ""
                    db.collection("orders").document(orderId).get().addOnSuccessListener {
                        var userId = it.getString("userId")
                        userName = it.getString("userName")!!
                        db.collection("users").document(userId!!).get().addOnSuccessListener {
                            token = it.get("token").toString()
                        }
                    }

                    var jsonObject = JSONObject()
                    var jsonObjectData = JSONObject()

                    jsonObjectData.put("title", "Order Status Updated")
                    jsonObjectData.put("body", "Hey! $userName your order status has been updated to ${orderUpdate.text.toString()} for order ID: $orderId")

                    jsonObject.put("to", token)
                    jsonObject.put("data", jsonObjectData)

                    processNotification(jsonObject)

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

                orderDetailsAdapter!!.setOrderStatus(it.getString("orderStatus")!!)

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

                detailRatingBar.rating = it.getString("orderRating")?.toFloat() ?: 0f
                orderReview.text = it.getString("orderReview")

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
                }
                else if (it.getString("orderStatus") == "Order Cancelled") {
                    detailRatingBar.isEnabled = false
                    orderReview.isEnabled = false
                    ratingReviewSubmit.isEnabled = false
                    orderUpdate.isEnabled = false
                    paymentStatus.text = "Not Applicable"
                }
                else {
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
                db.collection("orders").document(orderId!!).update(
                    mapOf(
                        "orderStatus" to "Order Cancelled",
                        "paymentStatus" to "Not Applicable"
                    )
                ).addOnSuccessListener {
                    CoroutineScope(Dispatchers.Main).launch {

                        for (i in 0 until orderDetailsAdapter!!.itemCount) {
                            db.collection("products").document(orderDetailsAdapter!!.prodIds[i]).get()
                                .addOnSuccessListener {
                                    var quantity = it.get("productStock").toString().toInt() + orderDetailsAdapter!!.prodQuantities[i]
                                    db.collection("products").document(orderDetailsAdapter!!.prodIds[i])
                                        .update("productStock", quantity)
                                        .addOnSuccessListener {
                                            Log.d(
                                                "TAG",
                                                "DocumentSnapshot successfully written!"
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "Error writing document", e)
                                        }
                                    Log.d("TAG", "DocumentSnapshot successfully written!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("TAG", "Error writing document", e)
                                }
                        }

                        var user = auth.currentUser
                        var token = db.collection("users").document(user!!.uid).get().await().get("token").toString()
                        Toast.makeText(this@OrderDetailsActivity, "Order Cancelled", Toast.LENGTH_SHORT).show()

                        var jsonObject = JSONObject()
                        var jsonObjectData = JSONObject()

                        jsonObjectData.put("title", "Order Cancelled")
                        jsonObjectData.put("body", "Hey! ${auth.currentUser?.displayName} your order has been cancelled with ID: $orderId")

                        jsonObject.put("to", token)
                        jsonObject.put("data", jsonObjectData)

                        processNotification(jsonObject)

                        sendNotificationToAdmin("Order Cancelled", "Hey! ${auth.currentUser?.displayName} has cancelled the order with ID: $orderId")
                    }
                    finish()
                }
            }
        }

        ratingReviewSubmit.setOnClickListener {
            val rating = detailRatingBar.rating
            val review = orderReview.text.toString()

            if (rating >= 1f ){
                if (review.isNotEmpty() && review.length >= 10) {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.collection("orders").document(orderId!!).update("orderRating", rating.toString()).addOnSuccessListener {
                            db.collection("orders").document(orderId).update("orderReview", review).addOnSuccessListener {

                                var user = auth.currentUser
                                var token = ""
                                db.collection("users").document(user!!.uid).get().addOnSuccessListener {
                                    token = it.get("token").toString()
                                }

                                var jsonObject = JSONObject()
                                var jsonObjectData = JSONObject()

                                jsonObjectData.put("title", "Review Submitted")
                                jsonObjectData.put("body", "Hey! ${auth.currentUser?.displayName} your review has been submitted.")

                                jsonObject.put("to", token)
                                jsonObject.put("data", jsonObjectData)

                                processNotification(jsonObject)

                                sendNotificationToAdmin("New Review", "Hey! ${auth.currentUser?.displayName} has submitted a review for order ID: $orderId")

                                runOnUiThread {
                                    Toast.makeText(this@OrderDetailsActivity, "Review Submitted", Toast.LENGTH_SHORT).show()
                                }

                                finish()
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

    private fun sendNotificationToAdmin(title: String, body: String) {
        var token = ""
        db.collection("users").whereEqualTo("useremail", "unrealadmin@gmail.com").get().addOnSuccessListener {
            token = it.documents[0].get("token").toString()
        }

        var jsonObject = JSONObject()
        var jsonObjectData = JSONObject()

        jsonObjectData.put("title", title)
        jsonObjectData.put("body", body)

        jsonObject.put("to", token)
        jsonObject.put("data", jsonObjectData)

        processNotification(jsonObject)
    }

    private fun processNotification(jsonObject: JSONObject) {
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = RequestBody.create(mediaType, jsonObject.toString())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer ${getString(R.string.fcm_servertoken)}")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("TAG", "processNotification: Notification sent successfully")
                } else {
                    Log.d("TAG", "processNotification: Failed to send notification ${response.toString()}")
                }

            } catch (e: IOException) {
                Log.e("TAG", "processNotification: ${e.message}", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }
}