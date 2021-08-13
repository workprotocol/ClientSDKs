package com.spritehealth.sdk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.SpriteHealthClient.Companion.apiRoot
import com.spritehealth.sdk.model.AccessTokenResponse
import com.spritehealth.sdk.model.Speciality
import com.spritehealth.sdk.model.User
import kotlinx.android.synthetic.main.activity_vptfinder.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

internal class VPTFinder : AppCompatActivity() {

    val specialists: MutableList<Map<String, String>> = ArrayList()
    val objCommon = SpriteHealthClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vptfinder)
        setSupportActionBar(findViewById(R.id.toolbar))

        val queue = Volley.newRequestQueue(this)

        var progressBar: ProgressBar = findViewById(R.id.progressBar);
        progressBar.visibility = View.VISIBLE

        objCommon.createAccessToken(this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                var builder: GsonBuilder = GsonBuilder();
                var gson: Gson =builder.create()
                val accessTokenType = object : TypeToken<AccessTokenResponse>() {}.type
                var accessTokenResponse: AccessTokenResponse = gson.fromJson(response, accessTokenType);
                SpriteHealthClient.auth_token=accessTokenResponse.access_token;
                SpriteHealthClient.expires_in=accessTokenResponse.expires_in;

                fetchSpecialities()
            }
            override fun onError(error: String?)
            {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        } )


    }

    fun fetchSpecialities(){
        objCommon.fetchSpecialities(this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                var builder: GsonBuilder = GsonBuilder();
                var gson: Gson =builder.create()
                val type = object : TypeToken<List<Speciality>>() {}.type
                var specialities:ArrayList<Speciality> = gson.fromJson(response, type)
                SpriteHealthClient.specialities=specialities;

                fetchMemberDetails()
            }
            override fun onError(error: String?)
            {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        } )
    }

    fun fetchMemberDetails(){
        objCommon.memberDetail(this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                var builder: GsonBuilder = GsonBuilder();
                var gson: Gson =builder.create()
                val userType = object : TypeToken<User>() {}.type
                var member:User = gson.fromJson(response, userType);
                SpriteHealthClient.member=member;

                fetchSpecialists()
            }
            override fun onError(error: String?)
            {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        } )
    }

    fun fetchSpecialists(){

        objCommon.specialistAvailable(this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                // do stuff here
                val responseJsonArray = JSONArray(response)
                displayVPT(responseJsonArray)
                progressBar.visibility = View.GONE
            }
            override fun onError(error: String?)
            {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        } )
    }


    fun displayVPT(jsonarray_info:JSONArray?) {
        var i: Int = 0
        var size: Int = jsonarray_info!!.length()
        if(size>0){
            var builder: GsonBuilder = GsonBuilder();
            var gson: Gson =builder.create()
            val specialistListType = object : TypeToken<List<User>>() {}.type
            var specialistList:List<User> = gson.fromJson(jsonarray_info.toString(), specialistListType);

            recycler_view.adapter = SpecialistAdapter(specialistList,this)
            recycler_view.layoutManager = LinearLayoutManager(this)
        }else{
            tvResultMessage.visibility=View.VISIBLE;
        }



    }



    companion object {

    }

/*
    override fun onCellClickListener() {
        Toast.makeText(this,"Cell clicked", Toast.LENGTH_SHORT).show()
    }

    *
 */
}