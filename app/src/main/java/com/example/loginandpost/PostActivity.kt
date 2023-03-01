package com.example.loginandpost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.UUID

private const val TAG = "MainActivity"

class PostActivity : AppCompatActivity() {

    lateinit var postText: EditText
    private lateinit var postButton: Button
    private lateinit var recyclerview_post: RecyclerView

    val adapter = GroupAdapter<GroupieViewHolder>()
    val currentUser = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.title = "Post Something"

        postText = findViewById(R.id.edittext_post)
        postButton = findViewById(R.id.post_button)
        recyclerview_post = findViewById(R.id.recyclerview_post)

        //fetchPosts()
        listenForPosts()

        recyclerview_post.adapter = adapter

        postButton.setOnClickListener {
            performPost()

        }


    }

    private fun performPost() {

        val referance =
            FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/posts/").push()

        val post = Post(currentUser!!.uid, postText.text.toString())

        referance.setValue(post)
            .addOnSuccessListener {
                Log.d(TAG, "Post basariyla database'e kaydedildi.")
                postText.text.clear()//yazdiktan sonra clearla
                recyclerview_post.scrollToPosition(adapter.itemCount -1)
            }.addOnFailureListener {
                Log.d(TAG, "Post database'e kaydedilemedi.")
            }

        val ref = FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/users")

        /*var something: String = ""


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user?.uid == Firebase.auth.currentUser?.uid) {
                        Log.d(TAG, "${user!!.username}")
                        something = user!!.username
                        adapter.add(postItem(post, something))
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })*/






        recyclerview_post.adapter = adapter


    }

    /*private fun fetchPosts() {
        val post = Post(currentUser!!.uid, postText.text.toString())

        val referance =
            FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/posts")

        val ref =
            FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/users")

        referance.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach {
                    val post = it.getValue(Post::class.java)

                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val user = it.getValue(User::class.java)
                                if (post?.posterUid == user!!.uid) {
                                    adapter.add(postItem(post, user.username))
                                }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })


                    //adapter.add(postItem(post!!, ))burda kaldın

                    recyclerview_post.adapter = adapter
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }*/


    private fun listenForPosts(){
        //val post = Post(currentUser!!.uid, postText.text.toString())

        val referance =
            FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/posts")

        val ref =
            FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/users")


        referance.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(Post::class.java)
                ref.addChildEventListener(object:ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val user = snapshot.getValue(User::class.java)
                        if(post?.posterUid == user?.uid){
                            adapter.add(postItem(post!!, user!!.username))
                        }



                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {

                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {

                    }

                })

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }


        })

    }








    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_profil -> {
                val intent = Intent(this, ProfilActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }


}

class postItem(val post: Post, val username: String) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {


        viewHolder.itemView.findViewById<TextView>(R.id.post_textview_post_row).text = post.post
        viewHolder.itemView.findViewById<TextView>(R.id.email_textview_post_row).text = username

    }

    override fun getLayout(): Int {

        return R.layout.post_row
    }


}

