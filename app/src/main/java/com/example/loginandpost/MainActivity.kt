package com.example.loginandpost

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById(R.id.username_login)
        password = findViewById(R.id.password_login)
        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            performLogin()
        }


    }


    private fun performLogin(){

        Log.d(TAG, "Username is: " + username.text.toString())
        Log.d(TAG, "Password is: " + password.text.toString())

        if(username.text.toString().isEmpty() || password.text.toString().isEmpty()){
            Toast.makeText(this, "Please enter your email/password!", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(username.text.toString(), password.text.toString())
            .addOnCompleteListener {
                //if(!it.isSuccessful)return@addOnCompleteListener

                Log.d(TAG, "Successfully loged in user with uid: ${it.result.user?.uid}")
                Toast.makeText(this, "Successfully logged in!!!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PostActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }.addOnFailureListener {

                Log.d(TAG, "User failed to log in: ${it.message}")
                Toast.makeText(this, "User failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()

            }

    }
}