package com.example.chinechat.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chinechat.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.ByteArrayOutputStream
import java.io.File

class SettingActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mDatabase: FirebaseDatabase? = null
    var mStorage: FirebaseStorage? = null
    var galleryID: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()

        var userID = mAuth!!.currentUser!!.uid
        var userRef = mDatabase!!.reference.child("Users").child(userID)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                finish()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var displayname = snapshot!!.child("name").value.toString()
                var status = snapshot!!.child("status").value.toString()
                var image = snapshot!!.child("image").value.toString()

                txt_display_name.text = displayname
                txt_status.text = status

                if (image != null) {
                    Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(image_profile)
                }

            }
        })

        btn_status.setOnClickListener {
            var intent = Intent(this, StatusActivity::class.java)
            intent.putExtra("status", txt_status.text.toString())
            startActivity(intent)
        }

        btn_start_chat.setOnClickListener {
            var galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), galleryID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == galleryID && resultCode == Activity.RESULT_OK) {
            var image = data!!.data
            CropImage.activity(image).setAspectRatio(1, 1).start(this)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            var result = CropImage.getActivityResult(data)
            var resultUri = result.uri
            var thumbFile = File(resultUri.path)

            var thumbBitmap = Compressor(this)
                .setMaxHeight(200)
                .setMaxWidth(200)
                .setQuality(80)
                .compressToBitmap(thumbFile)

            var byteArray = ByteArrayOutputStream()
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
            var thumbByteArray = byteArray.toByteArray()

            var userID = mAuth!!.currentUser!!.uid

            var imageRef = mStorage!!.reference.child("profile_image").child("$userID.jpg")
            var thumbRef = mStorage!!.reference.child("profile_image").child("thumb_image")
                .child("$userID.jpg")

            imageRef.putFile(resultUri)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation imageRef.downloadUrl
                }).addOnCompleteListener { task: Task<Uri> ->
                    if (task.isComplete) {
                        var imageUri = task.result.toString()
                        var uploadTask: UploadTask = thumbRef.putBytes(thumbByteArray)

                        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            return@Continuation thumbRef.downloadUrl
                        }).addOnCompleteListener { task: Task<Uri> ->
                            if (task.isComplete) {
                                var thumbUri = task.result.toString()
                                var updateObject = HashMap<String, Any>()
                                updateObject.put("image", imageUri)
                                updateObject.put("thumb_image", thumbUri)


                                mDatabase!!.reference.child("Users").child(userID)
                                    .updateChildren(updateObject)
                                    .addOnCompleteListener {
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Upload Successful",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Upload unSuccessful",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }
                        }

                    }

                }

        }
    }
}