package com.vikasjaiswal.unrealfurniture

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.tabs.TabLayout

class MyOrdersActivity : AppCompatActivity() {

    lateinit var networkReceiver : CheckConnectivity

    private lateinit var myOrderLayoutManager: GridLayoutManager

    private var myOrderAdapter: MyOrdersRecAdapter? = null

    private lateinit var myOrderRecyclerView: RecyclerView

    private lateinit var myOrderAnimation : LottieAnimationView
    private lateinit var emptyOrderTextView: TextView

    private lateinit var myOrdersTabLayout: TabLayout

    private lateinit var goBack : CardView

    private lateinit var skeleton : Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myorders_activity)

        networkReceiver = CheckConnectivity()

        myOrderRecyclerView = findViewById(R.id.myOrderRecyclerView)

        goBack = findViewById(R.id.goBack)

        goBack.setOnClickListener {
            finish()
        }

        skeleton = findViewById(R.id.skeletonLayout)

        myOrderLayoutManager = GridLayoutManager(this,1)

        myOrderRecyclerView.layoutManager = myOrderLayoutManager

        myOrderAdapter = MyOrdersRecAdapter{
            if (myOrderAdapter!!.itemCount == 0) {
                myOrderAnimation.visibility = View.VISIBLE
                emptyOrderTextView.visibility = View.VISIBLE
            } else {
                myOrderAnimation.visibility = View.GONE
                emptyOrderTextView.visibility = View.GONE
            }
        }


        myOrderRecyclerView.adapter = myOrderAdapter

        skeleton.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, 1000)

        myOrderAnimation = findViewById(R.id.myOrderAnimation)
        emptyOrderTextView = findViewById(R.id.emptyOrderTextView)

        myOrdersTabLayout = findViewById(R.id.myOrdersTabLayout)

        myOrdersTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        myOrderAdapter!!.updateData("Order Placed")
                    }
                    1 -> {
                        myOrderAdapter!!.updateData("Order Delivered")
                    }
                    2 -> {
                        myOrderAdapter!!.updateData("Order Cancelled")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        if (myOrderAdapter!!.itemCount == 0) {
            myOrderAnimation.visibility = View.VISIBLE
            emptyOrderTextView.visibility = View.VISIBLE
        } else {
            myOrderAnimation.visibility = View.GONE
            emptyOrderTextView.visibility = View.GONE
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