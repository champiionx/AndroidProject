package com.example.chinechat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.chinechat.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    var mAuth:FirebaseAuth? = null
    var mDatabase:FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        btn_create.setOnClickListener{
            var name = txt_name.text.toString().trim()
            var email = txt_email_regis.text.toString().trim()
            var password = txt_pass_regis.text.toString().trim()

            createUser(name,email,password)

            }
        }

    private fun createUser(name: String, email: String, password: String) {
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            mAuth!!.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    task: Task<AuthResult> ->

                if (task.isSuccessful)
                {
                   sendUserDataToFirebase(name)
                }
            }
        }
    }
    private fun sendUserDataToFirebase(name: String){
        var user = mAuth!!.currentUser
        var userid = user!!.uid

        var userRef = mDatabase!!.reference.child("Users").child(userid)
        var userObject = HashMap<String,String>()
        userObject.put("name",name)
        userObject.put("status","Hi I'm Champ")
        userObject.put("image","default")
        userObject.put("thumb_image","default")

        userRef.setValue(userObject).addOnCompleteListener {
                task: Task<Void> ->

            if (task.isSuccessful) {
                Toast.makeText(this, "Create Successful", Toast.LENGTH_LONG).show()
                var intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("userid",userid)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this,"Create unSuccessful",Toast.LENGTH_LONG).show()
            }
        }
    }
}