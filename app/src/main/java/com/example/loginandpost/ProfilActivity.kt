package com.example.loginandpost

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

private const val TAG = "MainActivity"

class ProfilActivity : AppCompatActivity() {

    private lateinit var profileButton: Button
    private lateinit var profileUsername:EditText
    private lateinit var profileImageButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        profileButton = findViewById(R.id.profil_button)
        profileUsername = findViewById(R.id.edittext_profil_username)
        profileImageButton = findViewById(R.id.profileimage_button)

        profileImageButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)

        }

        profileButton.setOnClickListener {
            uploadImageToFirebaseStorage()

        }





    }

    var selectedPhotoUri: Uri? = null
    //photo secme icin.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //google android circleimageview
        val selectphoto_imageview_profile = findViewById(R.id.circle_image_view_profile) as CircleImageView

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_profile.setImageBitmap(bitmap)
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //selectPhotoButton.setBackgroundDrawable(bitmapDrawable)

        }

    }

        private fun uploadImageToFirebaseStorage(){
            if(selectedPhotoUri == null){
                Toast.makeText(this,"Please select a profile image",Toast.LENGTH_LONG).show()
                return}

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File location: $it")

                        saveUserToFirebaseDatabase(it.toString())
                    }
                }.addOnFailureListener{
                    Log.d(TAG, "Could'nt upload image!")
                }


        }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/users/$uid")

        //val database = Firebase.database
        //val ref = database.getReference("/users/$uid")

        val usernameEditText = findViewById(R.id.edittext_profil_username) as EditText

        val user = User(uid, usernameEditText.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "We saved the user to database!!!")
                Toast.makeText(this,"Successfully saved user info",Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
                Log.d(TAG, "Couldnt save user to database!!!")
            }


    }







}