package com.vikasjaiswal.unrealfurniture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyAddressActivity : AppCompatActivity()    {   //, OnMapReadyCallback {


    private lateinit var myAddressLayoutManager: GridLayoutManager

    private var myAddressAdapter: MyAddressRecAdapter? = null

    private lateinit var myAddressRecyclerView: RecyclerView

    private lateinit var myAddressAnimation : LottieAnimationView
    private lateinit var emptyAddressTextView: TextView

    lateinit var fragmentContainerView : FragmentContainerView

    lateinit var addAddress : FloatingActionButton

    lateinit var skeleton : Skeleton

    lateinit var goBack : CardView

    lateinit var reload : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myaddress_activity)

        myAddressRecyclerView = findViewById(R.id.myAddressRecyclerView)

        myAddressLayoutManager = GridLayoutManager(this,1)

        myAddressRecyclerView.layoutManager = myAddressLayoutManager

        myAddressAdapter = MyAddressRecAdapter {
            updateEmptyViewVisibility()
        }
        myAddressAnimation = findViewById(R.id.myAddressAnimation)

        emptyAddressTextView = findViewById(R.id.emptyAddressTextView)

        skeleton  = findViewById(R.id.skeletonLayout)

        reload = findViewById(R.id.reload)

        reload.setOnClickListener {
        }


        if (myAddressAdapter?.itemCount == 0) {
            myAddressAnimation.visibility = LottieAnimationView.VISIBLE
            emptyAddressTextView.visibility = TextView.VISIBLE
        } else {
            myAddressAnimation.visibility = LottieAnimationView.GONE
            emptyAddressTextView.visibility = TextView.GONE
        }

        myAddressRecyclerView.adapter = myAddressAdapter

        skeleton.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, 1000)

        fragmentContainerView = findViewById(R.id.fragmentContainerView)

        addAddress = findViewById(R.id.addAddress)

        goBack = findViewById(R.id.goBack)


        addAddress.setOnClickListener {

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainerView, AddAddressFragment())
                .addToBackStack(null)
                .commit()
        }

        goBack.setOnClickListener {
            finish()
        }

        Log.d("Hello Worldkdnw", myAddressAdapter?.itemCount.toString())
    }

    private fun updateEmptyViewVisibility() {
        if (myAddressAdapter?.itemCount == 0) {
            myAddressAnimation.visibility = LottieAnimationView.VISIBLE
            emptyAddressTextView.visibility = TextView.VISIBLE
        } else {
            myAddressAnimation.visibility = LottieAnimationView.GONE
            emptyAddressTextView.visibility = TextView.GONE
        }
    }
}
