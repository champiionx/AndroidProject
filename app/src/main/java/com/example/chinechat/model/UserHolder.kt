package com.example.chinechat.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chinechat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_setting.view.*
import kotlinx.android.synthetic.main.user_row.view.*

class UserHolder(val customView: View) : RecyclerView.ViewHolder(customView){

    fun bind(user:User){
        customView.txt_name_row?.setText(user.name)
        customView.txt_status_row?.setText(user.status)
        if(!user.thumb_image!!.equals("default")){
            Picasso.get().load(user.thumb_image).placeholder(R.drawable.ic_profile).into(customView.image_user_row)
        }
    }
}