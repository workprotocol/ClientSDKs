package com.example.myapplication.ui.login

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var user_identity: String?=null
    private lateinit var mContext: HomeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
        mContext=this

        val bundle=this.intent
        user_identity= bundle?.getStringExtra("user_identity")
        val name= user_identity!!.split("@")[0].capitalize()
            //bundle?.getStringExtra("name")

        welcomeMsg.text="Hi $name, How can I help you today?"


        lloVPT.setOnClickListener {
            goToVPT(mContext)
            }
        }

    private fun goToVPT(mContext: HomeActivity) {
        com.spritehealth.sdk.SpriteHealthClient.user_identity= user_identity.toString()
        com.spritehealth.sdk.SpriteHealthClient().callVPTFinder(mContext.intent,mContext)
    }

}
