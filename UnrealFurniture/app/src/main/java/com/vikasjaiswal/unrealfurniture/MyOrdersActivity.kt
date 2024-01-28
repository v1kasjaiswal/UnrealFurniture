package com.vikasjaiswal.unrealfurniture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class MyOrdersActivity : AppCompatActivity() {


    private lateinit var myOrderLayoutManager: GridLayoutManager

    private var myOrderAdapter: MyOrdersRecAdapter? = null

    private lateinit var myOrderRecyclerView: RecyclerView

    private lateinit var myOrderAnimation : LottieAnimationView
    private lateinit var emptyOrderTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myorders_activity)

        myOrderRecyclerView = findViewById(R.id.myOrderRecyclerView)

        myOrderLayoutManager = GridLayoutManager(this,1)

        myOrderRecyclerView.layoutManager = myOrderLayoutManager

        myOrderAdapter = MyOrdersRecAdapter()

        myOrderRecyclerView.adapter = myOrderAdapter

        myOrderAnimation = findViewById(R.id.myOrderAnimation)
        emptyOrderTextView = findViewById(R.id.emptyOrderTextView)

        if (myOrderAdapter!!.itemCount == 0) {
            myOrderAnimation.visibility = View.VISIBLE
            emptyOrderTextView.visibility = View.VISIBLE
        } else {
            myOrderAnimation.visibility = View.GONE
            emptyOrderTextView.visibility = View.GONE
        }

        myOrderRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = myOrderLayoutManager.itemCount
                val lastVisibleItemPosition =
                    myOrderLayoutManager.findLastVisibleItemPosition()

                if (!myOrderAdapter!!.isLoading && totalItemCount - 1 <= lastVisibleItemPosition) {
                    myOrderAdapter?.loadMoreItems()
                }
            }
        })

    }
}