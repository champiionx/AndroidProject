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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
               firebaseAuth: FirebaseAuth ->
            var user = firebaseAuth.currentUser

            if (user != null)
            {
                var intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this, "Please Login", Toast.LENGTH_LONG).show()
            }
        }

        btn_login.setOnClickListener {
            var emaillogin = txt_email.text.toString().trim()
            var passwordlogin = txt_password.text.toString().trim()

            loginWithEmailAndPassword(emaillogin,passwordlogin)
        }
        btn_signup.setOnClickListener {
            var intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }

    }
    override fun onStart() {
        super.onStart()

        mAuth!!.addAuthStateListener (mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()

        mAuth!!.removeAuthStateListener (mAuthListener!!)
    }

    private fun loginWithEmailAndPassword(emaillogin: String, passwordlogin: String){
        if (!TextUtils.isEmpty(emaillogin) && !TextUtils.isEmpty(passwordlogin)) {
            mAuth!!.signInWithEmailAndPassword(emaillogin, passwordlogin)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                        var intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("userid",mAuth!!.currentUser!!.uid)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }

                }
        } else {
            Toast.makeText(this, " Please check your email and password !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

}