package com.example.loginandpost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

private const val TAG = "MainActivity"

class ChatActivity : AppCompatActivity() {

    val currentUser = Firebase.auth.currentUser
    lateinit var editText:EditText
    lateinit var buttonChat: Button
    lateinit var recyclerviewChat:RecyclerView
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        editText = findViewById(R.id.edittext_chat)
        buttonChat = findViewById(R.id.button_chat)
        recyclerviewChat = findViewById(R.id.recyclerview_chat)

        listenForMessages()


        recyclerviewChat.adapter = adapter

        buttonChat.setOnClickListener {
            performSendMessage()
        }





    }

    private fun performSendMessage(){
        val int = intent.getIntExtra(PostActivity.USER_KEY,5)

        val referance =
            FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/chatMessages/").push()

        val chatMessage = ChatMessage(currentUser!!.uid, editText.text.toString(),int)

        referance.setValue(chatMessage)
            .addOnSuccessListener {
            Log.d(TAG, "Chat mesaji basariyla kaydedildi!")
                editText.text.clear()//yazdiktan sonra clearla
                recyclerviewChat.scrollToPosition(adapter.itemCount -1)
        }.addOnFailureListener {
                Log.d(TAG, "Chat mesaji kaydedilemedi!")
            }

        recyclerviewChat.adapter = adapter
    }

    private fun listenForMessages(){
        val int = intent.getIntExtra(PostActivity.USER_KEY,5)

        val referance =
            FirebaseDatabase.getInstance("https://loginandpost-46bc6-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("/chatMessages")

        referance.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chat = snapshot.getValue(ChatMessage::class.java)
                if(currentUser!!.uid == chat!!.gonderenUid){
                    Log.d(TAG, chat.chat)
                    /*adapter.add(chatGonderilen(chat))
                    recyclerviewChat.adapter = adapter*/
                    if(int==0 && chat.int==0){
                        adapter.add(chatGonderilen(chat))
                        recyclerviewChat.adapter = adapter
                    }
                    if(int==1 && chat.int==1){
                        adapter.add(chatGonderilen(chat))
                        recyclerviewChat.adapter = adapter
                    }
                }else{
                    if(int==0 && chat.int==0){
                        adapter.add(chatGelen(chat))
                        recyclerviewChat.adapter = adapter
                    }
                    if(int==1 && chat.int==1){
                        adapter.add(chatGelen(chat))
                        recyclerviewChat.adapter = adapter
                    }
                    //adapter.add(chatGelen(chat))
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }


        })

    }




}

class chatGonderilen(val chat:ChatMessage): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_gonderilen).text = chat.chat
        viewHolder.itemView.findViewById<TextView>(R.id.imageview_gonderilen)

    }

    override fun getLayout(): Int {
        return R.layout.chat_gonderilen
    }
}
class chatGelen(val chat:ChatMessage): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.textview_gelen).text = chat.chat
        viewHolder.itemView.findViewById<TextView>(R.id.imageview_gelen)

    }

    override fun getLayout(): Int {
        return R.layout.chat_gelen
    }
}