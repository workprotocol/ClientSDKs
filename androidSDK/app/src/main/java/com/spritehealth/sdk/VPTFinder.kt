package com.spritehealth.sdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.AccessTokenResponse
import com.spritehealth.sdk.model.Appointment
import com.spritehealth.sdk.model.Speciality
import com.spritehealth.sdk.model.User
import kotlinx.android.synthetic.main.activity_vptfinder.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet


internal class VPTFinder : AppCompatActivity() {

    val specialists: MutableList<Map<String, String>> = ArrayList()
    val sdkClientInstance = SpriteHealthClient()

    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson =builder.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vptfinder)
        //setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM;
        getSupportActionBar()?.setCustomView(R.layout.custom_toolbar);

        val tvPageHeading = supportActionBar!!.customView.findViewById<TextView>(R.id.tvPageHeading)
        tvPageHeading.text = "Physical Therapists"

        imgvBack.setOnClickListener(){
            this.finish();
        }

        val queue = Volley.newRequestQueue(this)

        var progressBar: ProgressBar = findViewById(R.id.progressBar);
        progressBar.visibility = View.VISIBLE


        sdkClientInstance.createAccessToken(this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                var builder: GsonBuilder = GsonBuilder();
                var gson: Gson = builder.create()
                val accessTokenType = object : TypeToken<AccessTokenResponse>() {}.type
                var accessTokenResponse: AccessTokenResponse = gson.fromJson(
                    response,
                    accessTokenType
                );
                SpriteHealthClient.auth_token = accessTokenResponse.access_token;
                SpriteHealthClient.expires_in = accessTokenResponse.expires_in;

                fetchSpecialities()
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })

    }

    fun fetchSpecialities(){
        sdkClientInstance.fetchSpecialities(this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                var builder: GsonBuilder = GsonBuilder();
                var gson: Gson = builder.create()
                val type = object : TypeToken<List<Speciality>>() {}.type
                var specialities: ArrayList<Speciality> = gson.fromJson(response, type)
                SpriteHealthClient.specialities = specialities;

                fetchMemberDetails()
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }

    fun fetchMemberDetails(){
        sdkClientInstance.memberDetail(this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {

                val userType = object : TypeToken<User>() {}.type
                var member: User = gson.fromJson(response, userType);
                SpriteHealthClient.member = member;

                fetchSpecialists()
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }

    fun fetchSpecialists(){
        /*
        val appointment=Appointment(5969017979273216)
        val intent = Intent(this, AppointmentDetails::class.java).apply {
            if(appointment!=null) {
                putExtra("appointmentJSON", gson.toJson(appointment))
            }
        }
        this.startActivity(intent)

    return;
    */

        val params: MutableMap<String, String> = HashMap()

            params["specialities"] = "26"
            params["serviceDefinitionIds"] = "5414975176704000"
            params["startIndex"] = "0"
            params["endIndex"] = "100"
            params["getOnlyFirstAvailability"] = "true"

        var currentHour= LocalDateTime.now().hour;
        val  currentTime:String?=null
        var refDate : LocalDateTime?=null

        if(currentHour>=22){
            refDate =LocalDateTime.now().plusHours(3)
        }else{
            refDate = LocalDateTime.now().plusHours(2)
        }

        params["startDateTime"] = refDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
        params["currentTime"] = refDate.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        if(SpriteHealthClient.member!=null && SpriteHealthClient.member.planSubscriptions?.size!! >0) {
            var networkIds: MutableSet<Long>? = HashSet();

            SpriteHealthClient.member.planSubscriptions!!.forEach {
                if (it.networkIds != null) {
                    networkIds?.addAll(it.networkIds!!)
                }
                if (it.directNetworkIds != null) {
                    networkIds?.addAll(it.directNetworkIds!!)
                }
            }
            if (networkIds != null) {
                if (networkIds.isNotEmpty()) {
                    params["networkIds"] = networkIds.joinToString(",")
                }
            }

        }

        sdkClientInstance.specialistAvailable(params, this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                // do stuff here
                val responseJsonArray = JSONArray(response)
                displayVPT(responseJsonArray)
                progressBar.visibility = View.GONE
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }


    fun displayVPT(jsonarray_info: JSONArray?) {
        var size: Int = jsonarray_info!!.length()
        if(size>0){
            var builder: GsonBuilder = GsonBuilder();
            var gson: Gson =builder.create()
            val specialistListType = object : TypeToken<List<User>>() {}.type
            var specialistList:List<User> = gson.fromJson(
                jsonarray_info.toString(),
                specialistListType
            );

            recycler_view.adapter = SpecialistAdapter(specialistList, this)
            recycler_view.layoutManager = LinearLayoutManager(this)
        }else{
            tvResultMessage.visibility=View.VISIBLE;
        }


    }

}
