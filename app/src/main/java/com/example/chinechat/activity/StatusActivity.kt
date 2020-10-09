package com.example.chinechat.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chinechat.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : AppCompatActivity() {

    var mDatabase: FirebaseDatabase? = null
    var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        if (intent.extras != null)
        {
            var status = intent.extras!!.get("status").toString()
            txt_input_status.setText(status)
        }

        btn_change_status.setOnClickListener{
            var updateStatus = txt_input_status.text.toString().trim()

            mCurrentUser = FirebaseAuth.getInstance().currentUser
            var uid = mCurrentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance()
            var statusRef = mDatabase!!.reference.child("Users").child(uid).child("status")
            statusRef.setValue(updateStatus).addOnCompleteListener {
                task: Task<Void> ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this,"Update Status Success",Toast.LENGTH_LONG).show()
                    finish()
                }else
                {
                    Toast.makeText(this,"Update Status Error",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}