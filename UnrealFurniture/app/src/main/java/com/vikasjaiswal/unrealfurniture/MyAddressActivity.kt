package com.vikasjaiswal.unrealfurniture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyAddressActivity : AppCompatActivity()    {   //, OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var myAddressLayoutManager: GridLayoutManager

    private var myAddressAdapter: MyAddressRecAdapter? = null

    private lateinit var myAddressRecyclerView: RecyclerView

    private lateinit var myAddressAnimation : LottieAnimationView
    private lateinit var emptyAddressTextView: TextView

    lateinit var addAddress : FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myaddress_activity)

        myAddressRecyclerView = findViewById(R.id.myAddressRecyclerView)

        myAddressLayoutManager = GridLayoutManager(this,1)

        myAddressRecyclerView.layoutManager = myAddressLayoutManager

        myAddressAdapter = MyAddressRecAdapter()

        myAddressRecyclerView.adapter = myAddressAdapter

        myAddressAnimation = findViewById(R.id.myAddressAnimation)

        emptyAddressTextView = findViewById(R.id.emptyAddressTextView)

        addAddress = findViewById(R.id.addAddress)

        if (myAddressAdapter!!.itemCount == 0) {
            myAddressAnimation.visibility = LottieAnimationView.VISIBLE
            emptyAddressTextView.visibility = TextView.VISIBLE
        } else {
            myAddressAnimation.visibility = LottieAnimationView.GONE
            emptyAddressTextView.visibility = TextView.GONE
        }

        addAddress.setOnClickListener {
            val addAddressBottomSheet = BottomSheetDialog(this)

            val view = layoutInflater.inflate(R.layout.addaddress_bottomresource, null)
            addAddressBottomSheet.setContentView(view)

            addAddressBottomSheet.show()
        }

//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        val sydney = LatLng(-33.852, 151.211)
//
//        // Disable user interactions
//        googleMap.uiSettings.setAllGesturesEnabled(false)
//
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney")
//        )
//
//        val zoomLevel = 15.0f
//        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel)
//        googleMap.moveCamera(cameraUpdate)
//    }

}
