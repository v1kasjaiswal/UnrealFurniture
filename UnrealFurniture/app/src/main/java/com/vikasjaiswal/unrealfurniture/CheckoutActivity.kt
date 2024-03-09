package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CheckoutActivity : AppCompatActivity() {

    private lateinit var checkoutAddressLayoutManager: LinearLayoutManager
    private lateinit var checkoutProductLayoutManager: GridLayoutManager

    private var checkoutAddressAdapter: CheckoutAddressRecAdapter? = null
    private var checkoutProductAdapter: CheckoutProductRecAdapter? = null

    private lateinit var checkoutAddressRecyclerView: RecyclerView
    private lateinit var checkoutProductRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout_activity)

        checkoutAddressRecyclerView = findViewById(R.id.checkoutAddressRecyclerView)
        checkoutProductRecyclerView = findViewById(R.id.checkoutProductRecyclerView)

        checkoutAddressLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        checkoutProductLayoutManager = GridLayoutManager(this, 2)

        checkoutAddressRecyclerView.layoutManager = checkoutAddressLayoutManager
        checkoutProductRecyclerView.layoutManager = checkoutProductLayoutManager

        checkoutAddressAdapter = CheckoutAddressRecAdapter()
        checkoutProductAdapter = CheckoutProductRecAdapter()

        checkoutAddressRecyclerView.adapter = checkoutAddressAdapter
        checkoutProductRecyclerView.adapter = checkoutProductAdapter
    }
}