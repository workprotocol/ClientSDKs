
package com.spritehealth.sdk


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.User
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class SpecialistDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specialist_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        var id = intent.getStringExtra("id")
        val specialistJSON = intent.getStringExtra("specialistJSON")
        //val specialityNames = intent.getStringExtra("specialityNames")
        //var startTime = intent.getStringExtra("startTime");


        var builder: GsonBuilder = GsonBuilder();
        var gson: Gson =builder.create()
        val specialistType = object : TypeToken<User>() {}.type
        var specialist:User = gson.fromJson(specialistJSON.toString(), specialistType);

        // Capture the layout's TextView and set the string as its text
        /* val textView = findViewById<TextView>(R.id.name).apply {
             text = name
         }

         val idView = findViewById<TextView>(R.id.uniqueId)
         idView.text = id;
         val availSlotView = findViewById<TextView>(R.id.availSlot)
         availSlotView.text = startTime;
         */
        val objCommon = SpriteHealthClient()

        var response="";
        var progressBar: ProgressBar = findViewById(R.id.progressBar);
        progressBar.visibility = View.VISIBLE

        objCommon.specialistDetail(id, this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                // do stuff here
                var responseJson = JSONObject(response)
                displaySpecailistDetail(responseJson)
                progressBar.visibility = View.GONE
            }
            override fun onError(error: String?)
            {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        } )
    }

    fun displaySpecailistDetail(specialistJson:JSONObject?) {

        val textView = findViewById<TextView>(R.id.name)
        textView.text = specialistJson!!.getString("name");

        val genderView = findViewById<TextView>(R.id.gender)
        genderView.text = specialistJson!!.getString("gender");
    }


    fun backClick(view: View){
        var intent = SpriteHealthClient.storedIntent.apply {}
        startActivity(intent)

        //val shObject: SpriteHealth  = SpriteHealth();
        //shObject.callMain();
    }
}