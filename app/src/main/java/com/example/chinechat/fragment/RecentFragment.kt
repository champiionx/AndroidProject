package com.example.chinechat.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.solver.widgets.Snapshot
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chinechat.R
import com.example.chinechat.activity.ChatActivity
import com.example.chinechat.model.User
import com.example.chinechat.model.UserHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_recent.*
import kotlinx.android.synthetic.main.fragment_user.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecentFragment : Fragment() {

    var mDatabase: FirebaseDatabase? = null
    var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        var userId = mAuth!!.currentUser!!.uid

        var linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,true)
        linearLayoutManager.stackFromEnd = true
        var query = mDatabase!!.reference.child("Chat").child(userId)
            .orderByChild("Recent/date")

        var dataPhase = SnapshotParser<HashMap<String,String>>(){
            snapshot : DataSnapshot ->
            var friendId = snapshot.key!!
            var chatId = snapshot.child("chat_id").value.toString()
            var lastMessage = snapshot.child("Recent").child("last_message").value.toString()

            var data = HashMap<String,String>()
            data.put("friendid",friendId)
            data.put("chatid",chatId)
            data.put("last_message",lastMessage)
            data

        }

        var option = FirebaseRecyclerOptions.Builder<HashMap<String,String>>().setQuery(query,dataPhase)
            .setLifecycleOwner(this).build()

        var adapter = object : FirebaseRecyclerAdapter<HashMap<String,String>,UserHolder>(option){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
                return UserHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_row,parent,false))

            }

            override fun onBindViewHolder(
                holder: UserHolder,
                position: Int,
                model: HashMap<String, String>
            ) {
                var friendId = model.get("friendid")
                var chatId = model.get("chatid")
                var lastMessage = model.get("last_message").toString()

                var friendRef = mDatabase!!.reference.child("Users").child(friendId!!)
                friendRef.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            var friendUser = snapshot.getValue(User::class.java)
                            holder.bind(friendUser!!,lastMessage)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                holder.itemView.setOnClickListener{
                    var intent = Intent(context,ChatActivity::class.java)
                    intent.putExtra("friendid",friendId)
                    intent.putExtra("chatid",chatId)
                    startActivity(intent)
                }
            }



        }

        rv_recent.setHasFixedSize(true)
        rv_recent.layoutManager = linearLayoutManager
        rv_recent.adapter = adapter

    }


}