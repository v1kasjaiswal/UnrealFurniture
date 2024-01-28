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

class MyCartFragment : Fragment() {

    private lateinit var myCartLayoutManager: GridLayoutManager

    private var myCartAdapter: MyCartRecAdapter? = null

    private lateinit var myCartRecyclerView: RecyclerView

    private lateinit var myCartAnimation : LottieAnimationView
    private lateinit var emptyCartTextView: TextView

    private lateinit var myCartPriceStrikeText : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mycart_fragment, container, false)

        myCartRecyclerView = view.findViewById(R.id.myCartRecyclerView)

        myCartLayoutManager = GridLayoutManager(context, 1)

        myCartRecyclerView.layoutManager = myCartLayoutManager

        myCartAdapter = MyCartRecAdapter()

        myCartRecyclerView.adapter = myCartAdapter

        myCartAnimation = view.findViewById(R.id.myCartAnimation)
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView)

        myCartPriceStrikeText = view.findViewById(R.id.myCartPriceStrikeText)

        myCartPriceStrikeText.paint.isStrikeThruText = true

        if (myCartAdapter!!.itemCount == 0) {
            myCartAnimation.visibility = View.VISIBLE
            emptyCartTextView.visibility = View.VISIBLE
        } else {
            myCartAnimation.visibility = View.GONE
            emptyCartTextView.visibility = View.GONE
        }

        myCartRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = myCartLayoutManager.itemCount
                val lastVisibleItemPosition =
                    myCartLayoutManager.findLastVisibleItemPosition()

                if (!myCartAdapter!!.isLoading && totalItemCount - 1 <= lastVisibleItemPosition) {
                    myCartAdapter?.loadMoreItems()
                }
            }
        })

        return view
    }

}