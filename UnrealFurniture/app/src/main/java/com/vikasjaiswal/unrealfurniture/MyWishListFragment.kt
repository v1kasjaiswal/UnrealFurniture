package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class MyWishListFragment : Fragment() {

    private lateinit var myWishListLayoutManager: GridLayoutManager

    private var myWishListAdapter: MyWishListRecAdapter? = null

    private lateinit var myWishListRecyclerView: RecyclerView

    private lateinit var myWishListAnimation : LottieAnimationView
    private lateinit var emptyWishTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mywishlist_fragment, container, false)

        myWishListRecyclerView = view.findViewById(R.id.myWishListRecyclerView)

        myWishListLayoutManager = GridLayoutManager(context, 1)

        myWishListRecyclerView.layoutManager = myWishListLayoutManager

        myWishListAdapter = MyWishListRecAdapter()

        myWishListRecyclerView.adapter = myWishListAdapter

        myWishListAnimation = view.findViewById(R.id.myWishListAnimation)
        emptyWishTextView = view.findViewById(R.id.emptyWishTextView)

        if (myWishListAdapter!!.itemCount == 0) {
            myWishListAnimation.visibility = View.VISIBLE
            emptyWishTextView.visibility = View.VISIBLE
        } else {
            myWishListAnimation.visibility = View.GONE
            emptyWishTextView.visibility = View.GONE
        }

        myWishListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = myWishListLayoutManager.itemCount
                val lastVisibleItemPosition =
                    myWishListLayoutManager.findLastVisibleItemPosition()

                if (!myWishListAdapter!!.isLoading && totalItemCount - 1 <= lastVisibleItemPosition) {
                    myWishListAdapter?.loadMoreItems()
                }
            }
        })

        return view
    }

}