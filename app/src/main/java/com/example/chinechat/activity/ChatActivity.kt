package com.example.chinechat.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chinechat.R
import com.example.chinechat.model.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.actionbar_chat.view.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.message_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var mDatabase:FirebaseDatabase? = null
    private var mAuth:FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        mDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        var userId  = mAuth!!.currentUser!!.uid
        var myUser: User? = null
        var friendUser: User? = null

        if (intent.extras != null)
        {
            var chatId = intent.extras!!.get("chatid").toString()
            var friendId = intent.extras!!.get("friendid").toString()
            var linearLayoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false)
            var query = mDatabase!!.reference.child("Messages").child(chatId).child("data")

            var option = FirebaseRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).setLifecycleOwner(this).build()

            var inflater = this.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var adapter = object : FirebaseRecyclerAdapter<Message, MessageHolder>(option){
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
                    return  MessageHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_row,parent,false))
                }

                override fun onBindViewHolder(holder: MessageHolder, position: Int, model: Message) {
                   if (model.message != null)
                   {
                       var message = model.message
                       var senderName:String?
                       var image:String?
                       if (model.sender == userId)
                       {
                           senderName = myUser!!.name
                           image = myUser!!.image

                           holder.customview.txt_name_message_row.visibility = View.GONE
                           holder.customview.txt_message_row.visibility = View.GONE
                           holder.customview.image_message_row.visibility = View.GONE

                           holder.customview.txt_name_message_row_right.visibility = View.VISIBLE
                           holder.customview.txt_message_row_right.visibility = View.VISIBLE
                           holder.customview.image_message_row_right.visibility = View.VISIBLE
                       }else {
                           senderName = friendUser!!.name
                           image = friendUser!!.image
                       }
                       holder.bind(message!!,senderName!!,image!!)
                   }
                }

            }

            var myRef = mDatabase!!.reference.child("Users").child(userId)
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    myUser = snapshot.getValue(User::class.java)

                    var friendRef = mDatabase!!.reference.child("Users").child(friendId)
                    friendRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            friendUser = snapshot.getValue(User::class.java)

                            rv_chat.layoutManager = linearLayoutManager
                            rv_chat.adapter = adapter

                            var actionbar = inflater.inflate(R.layout.actionbar_chat, null)
                            actionbar.txt_bar_name.text = friendUser!!.name
                            Picasso.get().load(friendUser!!.thumb_image).placeholder(R.drawable.ic_profile).into(actionbar.im_bar)

                            supportActionBar!!.customView = actionbar
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



            btn_send_message.setOnClickListener{
                var text = edt_chat.text.toString().trim()
                edt_chat.setText("")
                if(!TextUtils.isEmpty(text))
                {
                    var messageRef = mDatabase!!.reference.child("Messages").child(chatId).child("data").push()

                    var message = Message(userId,text)

                    messageRef.setValue(message)

                    var dateFormat = SimpleDateFormat("yyMMddHHmmssSSS")
                    var date = dateFormat.format(Date())

                    var recentRef = mDatabase!!.reference.child("Chat").child(userId).child(friendId).child("Recent")
                    var friendRecentRef = mDatabase!!.reference.child("Chat").child(friendId).child(userId).child("Recent")

                    var recentChat = RecentChat(date,message.message!!)
                    recentRef.setValue(recentChat)
                    friendRecentRef.setValue(recentChat)

                }

        }

        }
    }
}