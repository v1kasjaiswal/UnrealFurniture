package com.vikasjaiswal.unrealfurniture

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.regex.Pattern

class AddProductFragment : Fragment() {

    private val AR_FILE_PICKER_CODE = 100
    private val DIMEN_FILE_PICKER_CODE = 101

    private lateinit var prodMainImageCard: CardView
    private lateinit var prodLookImageCard: CardView
    private lateinit var prodDimenImageCard: CardView
    private lateinit var prod3DModelCard: CardView
    private lateinit var prodDimenFileCard: CardView

    private var selectedImageUri: Uri? = null

    private var selectedMainImageUri: Uri? = null
    private var selectedLookImageUri: Uri? = null
    private var selectedDimenImageUri: Uri? = null

    private var selected3DModelUri: Uri? = null
    private var selected3DModelFile : File? = null
    private var selectedDimenFileUri: Uri? = null

    private lateinit var prodMainImage : ImageView
    private lateinit var prodLookImage : ImageView
    private lateinit var prodDimenImage : ImageView

    lateinit var prod3DModelImage :  ImageView
    lateinit var prod3DModelText : TextView

    lateinit var prodDimenFileImage :  ImageView
    lateinit var prodDimenFileText : TextView

    lateinit var deleteMainImage : ImageView
    lateinit var deleteLookImage : ImageView
    lateinit var deleteDimenImage : ImageView
    lateinit var delete3DModelFile : ImageView
    lateinit var deleteDimenFile : ImageView

    lateinit var productCategory : AutoCompleteTextView
    lateinit var productName : EditText
    lateinit var productDescription : EditText
    lateinit var productPrice : EditText
    lateinit var productDiscount : EditText
    lateinit var productStock : EditText

    lateinit var cardClicked : CardView

    lateinit var addProduct : Button

    private val PERMISSION_CODE = 1001

    lateinit var dimensionsMap : Map<String, String>

    private lateinit var storageReference: StorageReference

    var db = FirebaseFirestore.getInstance()

    private val storagePermissions = if (Build.VERSION.SDK_INT >= 33) {
        arrayListOf(
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayListOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.addproduct_fragment, container, false)

        checkAndRequestPermissions()

        storageReference = FirebaseStorage.getInstance().reference

        prodMainImageCard = view.findViewById(R.id.prodMainImageCard)
        prodLookImageCard = view.findViewById(R.id.prodLookImageCard)
        prodDimenImageCard = view.findViewById(R.id.prodDimenImageCard)
        prod3DModelCard = view.findViewById(R.id.prod3DModelCard)
        prodDimenFileCard = view.findViewById(R.id.prodDimenFileCard)

        prodMainImage = view.findViewById(R.id.prodMainImage)
        prodLookImage = view.findViewById(R.id.prodLookImage)
        prodDimenImage = view.findViewById(R.id.prodDimenImage)

        prod3DModelImage = view.findViewById(R.id.prod3DModelImage)
        prod3DModelText = view.findViewById(R.id.prod3DModelText)

        prodDimenFileImage = view.findViewById(R.id.prodDimenFileImage)
        prodDimenFileText = view.findViewById(R.id.prodDimenFileText)

        deleteMainImage = view.findViewById(R.id.deleteMainImage)
        deleteLookImage = view.findViewById(R.id.deleteLookImage)
        deleteDimenImage = view.findViewById(R.id.deleteDimenImage)
        delete3DModelFile = view.findViewById(R.id.delete3DModelFile)
        deleteDimenFile = view.findViewById(R.id.deleteDimenFile)

        productCategory = view.findViewById(R.id.productCategory)
        productName = view.findViewById(R.id.productedit)
        productDescription = view.findViewById(R.id.productDescription)
        productPrice = view.findViewById(R.id.prodpriceedit)
        productDiscount = view.findViewById(R.id.proddiscountedit)
        productStock = view.findViewById(R.id.prodstockedit)

        addProduct = view.findViewById(R.id.addProduct)

        prodMainImageCard.setOnClickListener {
            cardClicked = prodMainImageCard
            imagePicker()
        }

        prodLookImageCard.setOnClickListener {
            cardClicked = prodLookImageCard
            imagePicker()
        }

        prodDimenImageCard.setOnClickListener {
            cardClicked = prodDimenImageCard
            imagePicker()
        }

        prod3DModelCard.setOnClickListener {
            open3DFilePicker()
        }

        prodDimenFileCard.setOnClickListener {
            openDimenFilePicker()
        }

        deleteMainImage.setOnClickListener {
            prodMainImage.visibility = View.GONE
            selectedMainImageUri = null
            deleteMainImage.visibility = View.GONE
        }

        deleteLookImage.setOnClickListener {
            prodLookImage.visibility = View.GONE
            selectedLookImageUri = null
            deleteLookImage.visibility = View.GONE
        }

        deleteDimenImage.setOnClickListener {
            prodDimenImage.visibility = View.GONE
            selectedDimenImageUri = null
            deleteDimenImage.visibility = View.GONE
        }

        delete3DModelFile.setOnClickListener {
            prod3DModelText.text = "Add Product\n3D Model File"
            prod3DModelImage.setImageResource(R.drawable.add)
            delete3DModelFile.visibility = View.GONE
            selected3DModelUri = null
        }

        deleteDimenFile.setOnClickListener {
            prodDimenFileText.text = "Add Product\nDimension File"
            prodDimenFileImage.setImageResource(R.drawable.add)
            deleteDimenFile.visibility = View.GONE
            selectedDimenFileUri = null
        }

        addProduct.setOnClickListener {
            addProductToFirebase()
        }

        productCategory.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.product_categories)
            )
        )

        return view
    }

    private suspend fun uploadImageAndGetDownloadUrl(reference: StorageReference, uri: Uri): String {
        return try {
            val taskSnapshot = reference.putFile(uri).await()
            return taskSnapshot.storage.downloadUrl.await().toString()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
            return ""
        }
    }

    private fun addProductToFirebase() {
        val progressDialog = ProgressDialog(requireContext()).apply {
            setMessage("Uploading...")
            setCancelable(false)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check for selected images and file
                if (selectedMainImageUri == null || selectedMainImageUri.toString().isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Please select main image", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                if (selectedLookImageUri == null || selectedLookImageUri.toString().isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Please select look image", Toast.LENGTH_SHORT).show()

                    }
                    return@launch
                }

                if (selectedDimenImageUri == null || selectedDimenImageUri.toString().isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Please select dimension image", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                if (selected3DModelUri == null || selected3DModelUri.toString().isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Please select 3D model file", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                if (selectedDimenFileUri == null || selectedDimenFileUri.toString().isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Please select dimension file", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                if (productCategory.text.toString().isBlank()) {
                    withContext(Dispatchers.Main){
                        productCategory.error = "Please select category"
                        productCategory.requestFocus()
                    }
                    return@launch
                }

                if (productName.text.toString().isBlank() && !Pattern.matches("^(?!\\s)[a-zA-Z0-9\\s]{2,}$", productName.text.toString())) {
                    withContext(Dispatchers.Main){
                        productName.error = "Please enter valid product name"
                        productName.requestFocus()
                    }
                    return@launch
                }

                if (productDescription.text.toString().isBlank() && productDescription.text.toString().length < 100)  {
                    withContext(Dispatchers.Main){
                        productDescription.error = "Please enter valid description"
                        productDescription.requestFocus()
                    }
                    return@launch
                }

                if (productPrice.text.toString().isEmpty()){
                    withContext(Dispatchers.Main){
                        productPrice.error = "Please enter valid price"
                        productPrice.requestFocus()
                    }
                    return@launch
                }

                if (productPrice.text.toString().toInt() <= 0){
                    withContext(Dispatchers.Main){
                        productPrice.error = "Please enter valid price"
                        productPrice.requestFocus()
                    }
                    return@launch
                }

                if (productDiscount.text.toString().isEmpty()){
                    withContext(Dispatchers.Main){
                        productDiscount.error = "Please enter valid discount"
                        productDiscount.requestFocus()
                    }
                    return@launch
                }

                if (productDiscount.text.toString().toInt() <= 0 && productDiscount.text.toString().toInt() >= 100){
                    withContext(Dispatchers.Main){
                        productDiscount.error = "Please enter valid discount"
                        productDiscount.requestFocus()
                    }
                    return@launch
                }


                if (productStock.text.toString().isEmpty()){
                    withContext(Dispatchers.Main){
                        productStock.error = "Please enter valid stock"
                        productStock.requestFocus()
                    }
                    return@launch
                }

                if (productStock.text.toString().toInt() <= 0){
                    withContext(Dispatchers.Main){
                        productStock.error = "Please enter valid stock"
                        productStock.requestFocus()
                    }
                    return@launch
                }

                withContext(Dispatchers.Main){
                    progressDialog.show()

                val prodMainImageRef = storageReference.child("productImages/${selectedMainImageUri?.lastPathSegment}")
                val prodLookImageRef = storageReference.child("productImages/${selectedLookImageUri?.lastPathSegment}")
                val prodDimenImageRef = storageReference.child("productImages/${selectedDimenImageUri?.lastPathSegment}")

                val prod3DModelRef = storageReference.child("product3DModels/${selected3DModelFile?.name}")

                val prodMainImageDownloadUrl = uploadImageAndGetDownloadUrl(prodMainImageRef, selectedMainImageUri!!)
                val prodLookImageDownloadUrl = uploadImageAndGetDownloadUrl(prodLookImageRef, selectedLookImageUri!!)
                val prodDimenImageDownloadUrl = uploadImageAndGetDownloadUrl(prodDimenImageRef, selectedDimenImageUri!!)
                val prod3DModelDownloadUrl = uploadImageAndGetDownloadUrl(prod3DModelRef, selected3DModelUri!!)

                // Check if all download URLs are obtained successfully
                if (prodMainImageDownloadUrl.isNotEmpty() && prodLookImageDownloadUrl.isNotEmpty() &&
                    prodDimenImageDownloadUrl.isNotEmpty() && prod3DModelDownloadUrl.isNotEmpty()) {

                    var discountedPrice = productPrice.text.toString().toInt() - (productPrice.text.toString().toInt() * productDiscount.text.toString().toInt() / 100)

                    // Create the product data
                    val product = hashMapOf(
                        "productCategory" to productCategory.text.toString(),
                        "productName" to productName.text.toString(),
                        "productDescription" to productDescription.text.toString(),
                        "productPrice" to productPrice.text.toString().toInt(),
                        "productDiscount" to productDiscount.text.toString().toInt(),
                        "productDiscountedPrice" to discountedPrice,
                        "productStock" to productStock.text.toString().toInt(),
                        "prodMainImage" to prodMainImageDownloadUrl,
                        "prodLookImage" to prodLookImageDownloadUrl,
                        "prodDimenImage" to prodDimenImageDownloadUrl,
                        "prod3DModel" to prod3DModelDownloadUrl,
                        "prodDimensions" to dimensionsMap,
                        "prodRating" to 0.0,
                        "prodRatingCount" to 0
                    )

                    db.collection("products").add(product).await()
                }

                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        resetUI()
                        Toast.makeText(requireContext(), "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("Error", e.toString())
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Failed to upload product1", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun imagePicker() {
        ImagePicker.with(this)
            .crop(1920.0f, 1080.0f)
            .compress(1024)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .maxResultSize(1920, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    selectedImageUri = data?.data

                    when (cardClicked) {
                        prodMainImageCard -> {
                            selectedMainImageUri = selectedImageUri
                            prodMainImage.setImageURI(selectedMainImageUri)
                            prodMainImage.visibility = View.VISIBLE
                            deleteMainImage.visibility = View.VISIBLE

                            Log.d("Main Image", selectedMainImageUri.toString())
                        }
                        prodLookImageCard -> {
                            selectedLookImageUri = selectedImageUri
                            prodLookImage.setImageURI(selectedLookImageUri)
                            prodLookImage.visibility = View.VISIBLE
                            deleteLookImage.visibility = View.VISIBLE
                        }
                        prodDimenImageCard -> {
                            selectedDimenImageUri = selectedImageUri
                            prodDimenImage.setImageURI(selectedDimenImageUri)
                            prodDimenImage.visibility = View.VISIBLE
                            deleteDimenImage.visibility = View.VISIBLE
                        }
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (requestCode == AR_FILE_PICKER_CODE && resultCode == Activity.RESULT_OK){
            handle3DFilePickerResult(data)
        }
        else if (requestCode == DIMEN_FILE_PICKER_CODE && resultCode == Activity.RESULT_OK){
            handleDimenFilePickerResult(data)
        }
        else {
            Toast.makeText(context, "Unrecognized request code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun open3DFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, AR_FILE_PICKER_CODE)
    }

    private fun openDimenFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, DIMEN_FILE_PICKER_CODE)
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    return it.getString(displayNameIndex)
                }
            }
        }
        return ""
    }

    private fun handle3DFilePickerResult(data: Intent?) {
        data?.data?.let { uri ->
            val fileName = getFileNameFromUri(uri).lowercase()

            if (fileName.endsWith(".glb") || fileName.endsWith(".gltf") || fileName.endsWith(".usdz") || fileName.endsWith(".obj") || fileName.endsWith(".fbx")) {

                selected3DModelUri = uri

                selected3DModelUri?.let {uri ->
                    val inputStream: InputStream = requireContext().contentResolver.openInputStream(uri)!!
                    try{
                        selected3DModelFile = fileName.let { File(requireContext().cacheDir, it) }
                        val outputStream = FileOutputStream(selected3DModelFile)
                        inputStream.copyTo(outputStream)
                        outputStream.close()
                    }
                    catch (e: Exception) {
                        Log.e("Error", e.toString())
                    }
                    finally {
                        inputStream.close()
                    }

                }

                Log.d("3D MODEL File", selected3DModelFile.toString())

                prod3DModelText.text = fileName
                prod3DModelImage.setImageResource(R.drawable.cube)
                delete3DModelFile.visibility = View.VISIBLE

            } else {
                Toast.makeText(context, "Invalid file selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun readJSONDimenFile(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val jsonFile = inputStream?.bufferedReader().use { it?.readText() }

        if (jsonFile != null) {
            dimensionsMap = jacksonObjectMapper().readValue(jsonFile)
            Log.d("Dimensions", dimensionsMap.toString())
        } else {
            Log.e("Error", "Failed to read JSON file")
        }

        inputStream?.close()
    }

    private fun handleDimenFilePickerResult(data: Intent?) {
        data?.data?.let { uri ->
            val fileName = getFileNameFromUri(uri)
            if (fileName.endsWith(".json")){
                selectedDimenFileUri = uri
                prodDimenFileText.text = fileName
                prodDimenFileImage.setImageResource(R.drawable.file)
                deleteDimenFile.visibility = View.VISIBLE

                readJSONDimenFile(uri)
            } else {
                Toast.makeText(context, "Invalid file selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (areStoragePermissionsGranted()) {
        } else {
            requestStoragePermissions()
        }
    }

    private fun areStoragePermissionsGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            return (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED)
        }

        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

    }

    private fun requestStoragePermissions() {
        val permissionsToRequest = storagePermissions.filter {
            ContextCompat.checkSelfPermission(requireActivity(), it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        CoroutineScope(Dispatchers.Main).launch {
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest, PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (isGranted) {
                    Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
                } else {
                    requestStoragePermissions()
                }
            }
        }
    }

    private fun resetUI(){
        prodMainImage.visibility = View.GONE
        selectedMainImageUri = null

        prodLookImage.visibility = View.GONE
        selectedLookImageUri = null

        prodDimenImage.visibility = View.GONE
        selectedDimenImageUri = null

        prod3DModelImage.setImageResource(R.drawable.add)
        prod3DModelText.text = "Add Product\n3D Model File"
        selected3DModelUri = null
        selected3DModelFile = null

        prodDimenFileImage.setImageResource(R.drawable.add)
        prodDimenFileText.text = "Add Product\nDimension File"
        selectedDimenFileUri = null
        dimensionsMap = emptyMap()

        deleteMainImage.visibility = View.GONE
        deleteLookImage.visibility = View.GONE
        deleteDimenImage.visibility = View.GONE
        delete3DModelFile.visibility = View.GONE
        deleteDimenFile.visibility = View.GONE

        productName.text.clear()
        productDescription.text.clear()
        productPrice.text.clear()
        productDiscount.text.clear()
        productStock.text.clear()
    }
}
