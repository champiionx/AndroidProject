package com.example.chinechat.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chinechat.R
import com.example.chinechat.activity.ChatActivity
import com.example.chinechat.activity.ProfileActivity
import com.example.chinechat.model.User
import com.example.chinechat.model.UserHolder
import com.firebase.ui.database.FirebaseArray
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_user.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {

    var mDatabase: FirebaseDatabase? = null
    var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        var linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        var query = mDatabase!!.reference.child("Users").orderByChild("name")

        var option = FirebaseRecyclerOptions.Builder<User>().setQuery(query,User::class.java).setLifecycleOwner(this).build()

        var adapter = object : FirebaseRecyclerAdapter<User,UserHolder>(option){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
                return UserHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_row,parent,false))
            }

            override fun onBindViewHolder(holder: UserHolder, position: Int, model: User) {
                holder.bind(model)
                var friendID = getRef(position).key.toString()
                var userId = mAuth!!.currentUser!!.uid
                var chatId:String? = null

                var chatRef = mDatabase!!.reference.child("Chat").child(userId).child(friendID).child("chat_id")



                holder.itemView.setOnClickListener{
                    var option = arrayOf("View Profile","Chat")
                    var builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Select Option")
                    builder.setItems(option){dialogInterface, i ->
                        if (i==0)
                        {
                            var intent = Intent(context,ProfileActivity::class.java)
                            intent.putExtra("userid",friendID)
                            startActivity(intent)
                        }
                        else
                        {
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
                                        userList.put("0",userId)
                                        userList.put("1",friendID)
                                        messageRef.child("User_list").setValue(userList)

                                        chatId = messageRef.key.toString()

                                        var userDataRef = mDatabase!!.reference.child("Chat").child(userId).child(friendID).child("chat_id")
                                        userDataRef.setValue(chatId)

                                        var friendDataRef = mDatabase!!.reference.child("Chat").child(friendID).child(userId).child("chat_id")
                                        friendDataRef.setValue(chatId)
                                    }

                                    var intent = Intent(context,ChatActivity::class.java)
                                    intent.putExtra("chatid",chatId)
                                    intent.putExtra("friendid",friendID)
                                    startActivity(intent)
                                }
                            })


                        }
                    }
                    builder.show()
                }
            }

        }

        recycle_user.setHasFixedSize(true)
        recycle_user.layoutManager = linearLayoutManager
        recycle_user.adapter = adapter
    }


}