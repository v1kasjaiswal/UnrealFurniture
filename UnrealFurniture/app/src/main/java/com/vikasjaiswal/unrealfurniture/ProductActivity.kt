package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductActivity : AppCompatActivity() {

    lateinit var goBack : CardView

    lateinit var open3DView : CardView

    private lateinit var ratingReviewsLayoutManager: GridLayoutManager

    private var ratingReviewsAdapter: RatingReviewsRecAdapter? = null

    private lateinit var ratingReviewsRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_activity)

        goBack = findViewById(R.id.goBack)

        open3DView = findViewById(R.id.open3DView)

        ratingReviewsRecyclerView = findViewById(R.id.ratingReviewsRecyclerView)
        ratingReviewsLayoutManager = GridLayoutManager(this, 1)
        ratingReviewsRecyclerView.layoutManager = ratingReviewsLayoutManager

        ratingReviewsAdapter = RatingReviewsRecAdapter()

        ratingReviewsRecyclerView.adapter = ratingReviewsAdapter

        goBack.setOnClickListener {
            finish()
        }

        open3DView.setOnClickListener {
            try{
                val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
                val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                    .appendQueryParameter(
                        "file",
                        "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf"
                    )
                    .appendQueryParameter("mode", "ar_preferred")
                    .build()
                sceneViewerIntent.setData(intentUri)
                sceneViewerIntent.setPackage("com.google.ar.core")
                startActivity(sceneViewerIntent)
            }
            catch (e: Exception){
                Toast.makeText(this@ProductActivity, "Please Install AR-Core Service", Toast.LENGTH_SHORT).show()
            }
        }
    }
}