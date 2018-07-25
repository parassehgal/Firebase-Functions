package com.example.parassehgal.firebase

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

     var delayRun=2000L
     var accountCreated:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("userdata", Context.MODE_PRIVATE)
        accountCreated=sharedPreferences.getBoolean("accountCreated",false)

        var handler=Handler()
        var run= Runnable {
            if(!accountCreated){
                startActivity(Intent(this,Main2Activity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
        handler.postDelayed(run,delayRun)


    }
}
