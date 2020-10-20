package com.example.chinechat.model

import android.content.IntentSender

class Message() {
    var sender:String? = null
    var message:String? = null

    constructor(sender:String,message:String):this(){
        this.message = message
        this.sender = sender
    }
}