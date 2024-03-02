package com.vikasjaiswal.unrealfurniture

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.regex.Pattern

class ProfileFragment : Fragment() {

    lateinit var myOrdersCard : CardView
    lateinit var myAddressCard : CardView
    lateinit var appInfoCard : CardView

    lateinit var signOut : CardView

    lateinit var updateProfilePic : CardView

    var auth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var storageReference: StorageReference
    private var selectedImageUri: Uri? = null

    lateinit var userImage : ImageView
    lateinit var userName : TextView

    lateinit var privacyCard : CardView

    lateinit var resetPassCard : CardView

    lateinit var changeName : ImageView

    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        myOrdersCard = view.findViewById(R.id.myOrdersCard)
        myAddressCard = view.findViewById(R.id.myAddressCard)
        appInfoCard = view.findViewById(R.id.appInfoCard)

        signOut = view.findViewById(R.id.signOut)

        updateProfilePic = view.findViewById(R.id.updateProfilePic)

        changeName = view.findViewById(R.id.changeName)

        userImage = view.findViewById(R.id.userImage)

        userName = view.findViewById(R.id.userName)

        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()

        storageReference = FirebaseStorage.getInstance().reference

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gsio)

        myOrdersCard.setOnClickListener {
            val intent = Intent(context, MyOrdersActivity::class.java)
            startActivity(intent)
        }

        myAddressCard.setOnClickListener {
            val intent = Intent(context, MyAddressActivity::class.java)
            startActivity(intent)
        }

        appInfoCard.setOnClickListener {
            val intent = Intent(context, AppInfoActivity::class.java)
            startActivity(intent)
        }

        signOut.setOnClickListener {
            try {
                auth.signOut()
                googleSignInClient.signOut()

                val intent = Intent(context, SignInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Error: " + e.message)
            }
        }

        updateProfilePic.setOnClickListener {
            imagePicker()
        }

        privacyCard = view.findViewById(R.id.privacyCard)

        privacyCard.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Privacy Policy")
                .setMessage("This app does not collect any personal data. It only stores the data that you provide for the purpose of the app. The data is stored in a secure database and is not shared with any third party.")
                .setPositiveButton("Ok", null)
                .show()
        }

        resetPassCard = view.findViewById(R.id.resetPassCard)

        resetPassCard.setOnClickListener {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        changeName.setOnClickListener {
            changeUserName()
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadProfileData()
        }

        return  view
    }

    private fun imagePicker(){
        ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .galleryMimeTypes(  //Exclude gif images
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .maxResultSize(1080, 1080)
            .start()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ImagePicker.REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        selectedImageUri = data?.data

                        CoroutineScope(Dispatchers.IO).launch {
                            uploadImageToFirebaseStorage()
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

            else -> {
                Toast.makeText(context, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImageToFirebaseStorage() {
        val selectedImageUri = selectedImageUri
        if (selectedImageUri != null) {
            val uid = auth.currentUser?.uid
            val filename = UUID.randomUUID().toString()
            val ref = storageReference.child("UsersData/$uid/$filename")
            ref.putFile(selectedImageUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        Log.d("Image", "File Location: $uri")
                        if (uid != null) {
                            db.collection("users").document(uid)
                                .update("userimage", uri.toString())
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Image Uploaded",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    loadProfileData()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Image Upload Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        } else {
                            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Image Upload Failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileData(){
        try{

            db.collection("users").document(auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && isAdded) {
                        val userimage = document.getString("userimage")
                        if (userimage != null) {
                            Glide
                                .with(this)
                                .load(userimage)
                                .centerCrop()
                                .placeholder(R.drawable.user)
                                .into(userImage)
                        }
                        else{
                            Glide
                                .with(this)
                                .load(R.drawable.user)
                                .centerCrop()
                                .placeholder(R.drawable.user)
                                .into(userImage)
                        }

                        val username = document.getString("username")
                        if (username != null) {
                            val name = "$username"

                            userName.text = name
                        }
                    } else {
                        Log.d("MainActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("MainActivity", "get failed with ", exception)
                }
            }
        catch (e: Exception){
            Log.e("MainActivity", "Error: ${e.message}", e)
        }
    }

    private fun changeUserName() {
        try {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.editusername_dialog, null)

            val userName  = dialogView.findViewById<EditText>(R.id.newUserName)

            val alertDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("New User Name")
                .setView(dialogView)
                .setPositiveButton("Save", null) // Set positive button with null click listener
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                val newUserName = userName.text.toString()
                if (newUserName.isNotBlank() && Pattern.matches(
                        "^(?!\\s)[a-zA-Z\\s]{2,}$",
                        newUserName
                    )
                ) {
                    db.collection("users").document(auth.currentUser?.uid.toString())
                        .update("username", newUserName)
                        .addOnSuccessListener {
                            Toast.makeText(context, "User Name Updated", Toast.LENGTH_SHORT).show()
                            loadProfileData()
                            alertDialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "User Name Update Failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    Toast.makeText(context, "User Name can't be empty", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "Error: ${e.message}", e)
        }
    }
}