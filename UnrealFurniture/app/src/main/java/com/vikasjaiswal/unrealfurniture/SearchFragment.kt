package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var searchLayoutManager: GridLayoutManager

    private var searchAdapter: SearchRecAdapter? = null

    private lateinit var searchRecyclerView: RecyclerView

    private lateinit var filterBottomSheetDialog: BottomSheetDialog
    private lateinit var categoryBottomSheetDialog: BottomSheetDialog

    lateinit var categoryRadioGroup: RadioGroup
    lateinit var filterRadioGroup: RadioGroup

    lateinit var filterBy : Chip
    lateinit var selectCategory: Chip
    lateinit var outOfStock : Chip

    lateinit var searchView : SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        searchRecyclerView = view.findViewById(R.id.searchRecyclerView)

        searchLayoutManager = GridLayoutManager(context, 2)

        searchAdapter = SearchRecAdapter{
            Log.d("SearchFragment", "Updated Fragment")
        }

        searchRecyclerView.layoutManager = searchLayoutManager

        searchRecyclerView.adapter = searchAdapter

        filterBy = view.findViewById(R.id.filterBy)
        selectCategory = view.findViewById(R.id.selectCategory)
        outOfStock = view.findViewById(R.id.outOfStock)

        searchView = view.findViewById(R.id.searchView)

        filterBottomSheetDialog = BottomSheetDialog(requireContext())
        val filterBottomSheetView = layoutInflater.inflate(R.layout.sortby_bottomresource, null)
        filterBottomSheetDialog.setContentView(filterBottomSheetView)

        filterRadioGroup = filterBottomSheetDialog.findViewById<RadioGroup>(R.id.filterRadioGroup)!!
        filterRadioGroup.check(R.id.priceLowToHigh)

        categoryBottomSheetDialog = BottomSheetDialog(requireContext())
        val categoryBottomSheetView = layoutInflater.inflate(R.layout.categories_bottomresource, null)
        categoryBottomSheetDialog.setContentView(categoryBottomSheetView)

        categoryRadioGroup = categoryBottomSheetDialog.findViewById<RadioGroup>(R.id.categoryRadioGroup)!!
        categoryRadioGroup.check(R.id.allCategories)

        outOfStock.isChecked = false

        filterBy.setOnClickListener {
            openFilterByBottomSheet()
        }

        selectCategory.setOnClickListener {
            openSelectCategoryBottomSheet()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchAdapter?.updateData(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchAdapter?.updateData(newText!!)
                return true
            }
        })

        outOfStock.setOnCheckedChangeListener { buttonView, isChecked ->
            val category = when(categoryRadioGroup.checkedRadioButtonId){
                R.id.filterSofas -> "Sofas"
                R.id.filterBeds -> "Beds"
                R.id.filterTables -> "Tables"
                R.id.filterChairs -> "Chairs"
                R.id.filterBookshelves -> "Bookshelves"
                R.id.filterWardrobes -> "Wardrobes"
                R.id.filterOthers -> "Others"
                else -> "All"
            }

            val sortBy = when(filterRadioGroup.checkedRadioButtonId){
                R.id.priceLowToHigh -> "PriceLowToHigh"
                R.id.priceHighToLow -> "PriceHighToLow"
                R.id.ratingLowToHigh -> "RatingLowToHigh"
                R.id.ratingHighToLow -> "RatingHighToLow"
                R.id.discountLowToHigh -> "DiscountLowToHigh"
                R.id.discountHighToLow -> "DiscountHighToLow"
                else -> "PriceLowToHigh"
            }

            if (categoryRadioGroup.checkedRadioButtonId == R.id.allCategories){
                searchAdapter?.updateData(sortBy, isChecked)
            }
            else{
                searchAdapter?.updateData(sortBy, category, isChecked)
            }
        }

        return view
    }

    private fun openFilterByBottomSheet(){
        filterBottomSheetDialog.show()

        filterRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val category = when(categoryRadioGroup.checkedRadioButtonId){
                R.id.filterSofas -> "Sofas"
                R.id.filterBeds -> "Beds"
                R.id.filterTables -> "Tables"
                R.id.filterChairs -> "Chairs"
                R.id.filterBookshelves -> "Bookshelves"
                R.id.filterWardrobes -> "Wardrobes"
                R.id.filterOthers -> "Others"
                else -> "All"
            }

            when(checkedId){
                R.id.priceLowToHigh -> {
                    filterBottomSheetDialog.dismiss()

                    if (categoryRadioGroup.checkedRadioButtonId == R.id.allCategories){
                        searchAdapter?.updateData("PriceLowToHigh", outOfStock.isChecked)
                    }
                    else{
                        searchAdapter?.updateData("PriceLowToHigh", category, outOfStock.isChecked)
                    }
                }
                R.id.priceHighToLow -> {
                    filterBottomSheetDialog.dismiss()

                    if (categoryRadioGroup.checkedRadioButtonId == R.id.allCategories){
                        searchAdapter?.updateData("PriceHighToLow", outOfStock.isChecked)
                    }
                    else{
                        searchAdapter?.updateData("PriceHighToLow", category, outOfStock.isChecked)
                    }
                }
                R.id.ratingLowToHigh -> {
                    filterBottomSheetDialog.dismiss()

                    if (categoryRadioGroup.checkedRadioButtonId == R.id.allCategories){
                        searchAdapter?.updateData("RatingLowToHigh", outOfStock.isChecked)
                    }
                    else{
                        searchAdapter?.updateData("RatingLowToHigh", category, outOfStock.isChecked)
                    }
                }
                R.id.ratingHighToLow -> {
                    filterBottomSheetDialog.dismiss()

                    if (categoryRadioGroup.checkedRadioButtonId == R.id.allCategories){
                        searchAdapter?.updateData("RatingHighToLow", outOfStock.isChecked)
                    }
                    else{
                        searchAdapter?.updateData("RatingHighToLow", category, outOfStock.isChecked)
                    }
                }
                R.id.discountLowToHigh -> {
                    filterBottomSheetDialog.dismiss()

                    if (categoryRadioGroup.checkedRadioButtonId == R.id.allCategories){
                        searchAdapter?.updateData("DiscountLowToHigh", outOfStock.isChecked)
                    }
                    else{
                        searchAdapter?.updateData("DiscountLowToHigh", category, outOfStock.isChecked)
                    }
                }
                R.id.discountHighToLow -> {
                    filterBottomSheetDialog.dismiss()

                    if (categoryRadioGroup.checkedRadioButtonId == R.id.allCategories){
                        searchAdapter?.updateData("DiscountHighToLow", outOfStock.isChecked)
                    }
                    else{
                        searchAdapter?.updateData("DiscountHighToLow", category, outOfStock.isChecked)
                    }
                }
            }
        }
    }

    private fun openSelectCategoryBottomSheet(){
        categoryBottomSheetDialog.show()

        categoryRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            val sortBy = when(filterRadioGroup.checkedRadioButtonId){
                R.id.priceLowToHigh -> "PriceLowToHigh"
                R.id.priceHighToLow -> "PriceHighToLow"
                R.id.ratingLowToHigh -> "RatingLowToHigh"
                R.id.ratingHighToLow -> "RatingHighToLow"
                R.id.discountLowToHigh -> "DiscountLowToHigh"
                R.id.discountHighToLow -> "DiscountHighToLow"
                else -> "PriceLowToHigh"
            }

            when(checkedId){
                R.id.allCategories -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, outOfStock.isChecked)
                }
                R.id.filterSofas -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, "Sofas", outOfStock.isChecked)
                }
                R.id.filterBeds -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, "Beds", outOfStock.isChecked)
                }
                R.id.filterTables -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, "Tables", outOfStock.isChecked)
                }
                R.id.filterChairs -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, "Chairs", outOfStock.isChecked)
                }
                R.id.filterBookshelves -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, "Bookshelves", outOfStock.isChecked)
                }
                R.id.filterWardrobes -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, "Wardrobes", outOfStock.isChecked)
                }
                R.id.filterOthers -> {
                    categoryBottomSheetDialog.dismiss()

                    searchAdapter?.updateData(sortBy, "Others", outOfStock.isChecked)
                }
            }
        }
    }

}