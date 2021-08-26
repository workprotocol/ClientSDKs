package com.partner.sampleapp.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.partner.sampleapp.R

import com.spritehealth.sdk.SpriteHealthClient
import com.spritehealth.sdk.model.InitOptions
import com.spritehealth.sdk.model.InitializationStatus
import com.spritehealth.sdk.model.InitializationStatusTypes
import com.spritehealth.sdk.model.IntegrationMode
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var user_identity: String=""
    private lateinit var mContext: HomeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
        mContext=this

        val bundle=this.intent
        user_identity= bundle?.getStringExtra("user_identity").toString()
        val name= user_identity!!.split("@")[0].capitalize()
            //bundle?.getStringExtra("name")

        welcomeMsg.text="Hi $name, How can I help you today?"


        lloVPT.setOnClickListener {
            goToVPT(mContext)
            }
        }

    private fun goToVPT(mContext: HomeActivity) {


        loading.visibility= View.VISIBLE

        val currentActivityIntent=this.intent
        val attributes: HashMap<String, String> = HashMap()

        val state: String =edtState.text.toString()
        if(state.isNotEmpty()){
            attributes["state"]=state
        }

        //Create instance of sdk client
        var sdkClientInstance = SpriteHealthClient.getInstance(mContext)

        //Initialize sdk client
        sdkClientInstance!!.initialize(
            InitOptions("enter you clientId here",user_identity, IntegrationMode.TEST), object:
                SpriteHealthClient.Callback<InitializationStatus> {
                override fun onSuccess(initializationStatus: InitializationStatus) {
                    loading.visibility= View.GONE
                    if(initializationStatus!=null && initializationStatus.status== InitializationStatusTypes.SUCCESS){
                        //Toast.makeText(mContext,"Successful to initialize Sprite Health client sdk.",Toast.LENGTH_LONG).show()
                        sdkClientInstance.launchVPTFlow(currentActivityIntent,mContext,attributes)
                    }
                }

                override fun onError(error: String?) {
                    loading.visibility= View.GONE
                    Toast.makeText(mContext,"Failed to initialize Sprite Health client sdk. Error:"+error,Toast.LENGTH_LONG).show()
                }

            }
        )
    }

}
