package com.example.chinechat.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chinechat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.message_row.view.*

class MessageHolder(val customview : View): RecyclerView.ViewHolder(customview) {
    fun bind(message:String,sender:String,image:String){
        customview.txt_message_row.text = message
        customview.txt_name_message_row.text = sender
        customview.txt_message_row_right.text = message
        customview.txt_name_message_row_right.text = sender
        Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(customview.image_message_row)
        Picasso.get().load(image).placeholder(R.drawable.ic_profile).into(customview.image_message_row_right)
    }
}