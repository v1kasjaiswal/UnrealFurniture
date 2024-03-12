package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class SearchFragment : Fragment() {

    private lateinit var searchLayoutManager: GridLayoutManager

    private var searchAdapter: SearchRecAdapter? = null

    private lateinit var searchRecyclerView: RecyclerView

    lateinit var sortBy : Chip
    lateinit var filterBy : Chip
    lateinit var selectCategory: Chip
    lateinit var outOfStock : Chip

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        searchRecyclerView = view.findViewById(R.id.searchRecyclerView)

        searchLayoutManager = GridLayoutManager(context, 2)

        searchAdapter = SearchRecAdapter()

        searchRecyclerView.layoutManager = searchLayoutManager

        searchRecyclerView.adapter = searchAdapter

        sortBy = view.findViewById(R.id.sortBy)
        filterBy = view.findViewById(R.id.filterBy)
        selectCategory = view.findViewById(R.id.selectCategory)
        outOfStock = view.findViewById(R.id.outOfStock)

        sortBy.setOnClickListener {
            openSortByBottomSheet()
        }

        filterBy.setOnClickListener {
            openFilterByBottomSheet()
        }

        selectCategory.setOnClickListener {
            openSelectCategoryBottomSheet()
        }

        outOfStock.setOnClickListener {
            includeOutOfStockProducts()
        }




        return view
    }

    private fun openSortByBottomSheet(){
        
    }

    private fun openFilterByBottomSheet(){

    }

    private fun openSelectCategoryBottomSheet(){

    }

    private fun includeOutOfStockProducts(){

    }
}