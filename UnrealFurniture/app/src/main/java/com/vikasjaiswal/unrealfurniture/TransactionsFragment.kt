package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.tabs.TabLayout

class TransactionsFragment : Fragment() {

    private lateinit var TransactionsTabLayout: TabLayout

    private lateinit var transactionLayoutManager: GridLayoutManager

    private var transactionAdapter: TransactionRecAdapter ? = null

    private lateinit var transactionRecyclerView: RecyclerView

    private lateinit var transactionAnimation : LottieAnimationView
    private lateinit var emptyTransactionTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.transactions_fragment, container, false)

        TransactionsTabLayout = view.findViewById(R.id.transactionsTabLayout)

        transactionAnimation = view.findViewById(R.id.transactionAnimation)
        emptyTransactionTextView = view.findViewById(R.id.emptyTransactionTextView)

        transactionRecyclerView = view.findViewById(R.id.transactionsRecyclerView)

        transactionLayoutManager = GridLayoutManager(context, 1)

        transactionRecyclerView.layoutManager = transactionLayoutManager

        transactionAdapter = TransactionRecAdapter{
            if (transactionAdapter!!.itemCount == 0) {
                transactionAnimation.visibility = View.VISIBLE
                emptyTransactionTextView.visibility = View.VISIBLE
            } else {
                transactionAnimation.visibility = View.GONE
                emptyTransactionTextView.visibility = View.GONE
            }
        }

        transactionRecyclerView.adapter = transactionAdapter

        TransactionsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        transactionAdapter!!.updateData("Order Placed")
                    }
                    1 -> {
                        transactionAdapter!!.updateData("Order Shipped")
                    }
                    2 -> {
                        transactionAdapter!!.updateData("Order Out for Delivery")
                    }
                    3 -> {
                        transactionAdapter!!.updateData("Order Delivered")
                    }
                    4 -> {
                        transactionAdapter!!.updateData("Order Cancelled")
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        if (transactionAdapter!!.itemCount == 0) {
            transactionAnimation.visibility = View.VISIBLE
            emptyTransactionTextView.visibility = View.VISIBLE
        } else {
            transactionAnimation.visibility = View.GONE
            emptyTransactionTextView.visibility = View.GONE
        }

        return view
    }

}