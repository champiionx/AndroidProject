package com.example.chinechat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chinechat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    var mDatabase: FirebaseDatabase? = null
    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        if (intent.extras != null)
        {
            var userId = intent.extras!!.get("userid").toString()
            var myId = mAuth!!.currentUser!!.uid

            mDatabase!!.reference.child("Users").child(userId).addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    finish()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var display_name = snapshot!!.child("name").value.toString()
                    var image = snapshot!!.child("image").value.toString()
                    var status = snapshot!!.child("status").value.toString()

                    txt_display_name_profile.text = display_name
                    txt_status_profile.text = status
                    if(!image.equals("default"))
                    {
                        Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(image_profile_user)
                    }
                }
            })

            btn_start_chat.setOnClickListener{

                var chatId: String?
                var chatRef = mDatabase!!.reference.child("Chat").child(myId).child(userId).child("chat_id")
                chatRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists())
                        {
                            chatId = snapshot.value.toString()
                        }
                        else{
                            var messageRef = mDatabase!!.reference.child("Messages").push()

                            var userList = HashMap<String, String>()
                            userList.put("0",myId)
                            userList.put("1",userId)
                            messageRef.child("User_list").setValue(userList)

                            chatId = messageRef.key.toString()

                            var userDataRef = mDatabase!!.reference.child("Chat").child(myId).child(userId).child("chat_id")
                            userDataRef.setValue(chatId)

                            var friendDataRef = mDatabase!!.reference.child("Chat").child(userId).child(myId).child("chat_id")
                            friendDataRef.setValue(chatId)
                        }

                        var intent = Intent(this@ProfileActivity,ChatActivity::class.java)
                        intent.putExtra("chatid",chatId)
                        intent.putExtra("friendid",userId)
                        startActivity(intent)
                    }
                })
            }
        }else{
            finish()
        }
    }
}