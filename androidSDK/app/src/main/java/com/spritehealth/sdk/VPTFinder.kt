package com.spritehealth.sdk

import Util
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spritehealth.sdk.SpriteHealthClient.Callback
import com.spritehealth.sdk.model.*
import kotlinx.android.synthetic.main.activity_vptfinder.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet


internal class VPTFinder : AppCompatActivity() {

    private var state: String?=null
    private var currentLongitude: Double?=null
    private var currentLatitude: Double?=null
    val specialists: MutableList<Map<String, String>> = ArrayList()
    var sdkClientInstance: SpriteHealthClient? = null

    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson =builder.create()
    var mContext:Context=this

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        var progressBar: ProgressBar = findViewById(R.id.progressBar);
        progressBar.visibility = View.VISIBLE



        sdkClientInstance=SpriteHealthClient.getInstance(this.applicationContext)
        sdkClientInstance!!.initialize(this,
            InitOptions("0b5c8d72f9794ec69870886cd060bc82","dag@berger.com",IntegrationMode.TEST), object:Callback<InitializationStatus>{
                override fun onSuccess(initializationStatus: InitializationStatus) {
                    if(initializationStatus!=null && initializationStatus.status==InitializationStatusTypes.SUCCESS){
                        //fetchSpecialists()
                        getLastLocation()
                    }
                }

                override fun onError(error: String?) {
                   Toast.makeText(mContext,"Failed to initialize Sprite Health client sdk.",Toast.LENGTH_LONG).show()
                }

            }
        )


        /*
         val queue = Volley.newRequestQueue(this)
        sdkClientInstance.createAccessToken(this, object : SpriteHealthClient.Callback<AccessTokenResponse?> {
            override fun onSuccess(accessTokenResponse: InitializationStatus) {
                if (accessTokenResponse != null) {
                    SpriteHealthClient.auth_token = accessTokenResponse.access_token
                    SpriteHealthClient.expires_in = accessTokenResponse.expires_in;
                    fetchMemberDetails()
                }else{
                    Toast.makeText(mContext,"Failed to authenticate client.",Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
        */

    }


    /*

    fun fetchMemberDetails(){
        sdkClientInstance.getMemberDetails(this, object : SpriteHealthClient.Callback<User> {
            override fun onSuccess(memberInfo: InitializationStatus) {
                if (memberInfo != null) {
                    SpriteHealthClient.member = memberInfo
                    fetchSpecialists()
                }else{
                    Toast.makeText(mContext,"Failed to access member details.",Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }
    */

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

            if(state!=null) {
                params["state"] = state!!
            }
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

        progressBar.visibility = View.VISIBLE
        sdkClientInstance?.getAvailableSpecialists(params, this, object : Callback<List<Specialist>> {
            override fun onSuccess(speciaistsWithAvailability: List<Specialist>) {

                if(SpriteHealthClient.specialities?.isNotEmpty() == true){
                    displayVPT(speciaistsWithAvailability)
                }else{
                    fetchSpecialities(speciaistsWithAvailability)
                }
                progressBar.visibility = View.GONE
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }

    fun fetchSpecialities(specialistList: List<Specialist>) {
        progressBar.visibility = View.VISIBLE

        sdkClientInstance?.getSpecialities(this, object : Callback<List<Speciality>> {
            override fun onSuccess(specialities: List<Speciality>) {
                SpriteHealthClient.specialities = specialities;

                displayVPT(specialistList)
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }



    fun displayVPT(specialistList: List<Specialist>) {
        progressBar.visibility = View.GONE
        var size: Int = specialistList!!.size
        if(size>0){
            recycler_view.adapter = SpecialistAdapter(specialistList, this)
            recycler_view.layoutManager = LinearLayoutManager(this)
        }else{
            tvResultMessage.visibility=View.VISIBLE;
        }
    }



    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLatitude=location.latitude
                        currentLongitude=location.longitude
                        readLocationForState()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun readLocationForState() {
        var address = Util().getAddressFromLocation(this, currentLatitude, currentLongitude)
        if(address!=null){
            state=address.adminArea
        }
        if(state==null){
            getMemberHomeState()
        }
        fetchSpecialists()
    }
    
    fun locationDetectionFailed(){
        getMemberHomeState()
        fetchSpecialists()
    }

    fun getMemberHomeState(){
        var homeLocation=Util().getDefaultMemberLocation()
        if(homeLocation!=null && homeLocation.stateOrProvince!=null){
            state=homeLocation.stateOrProvince
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            //findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            //findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()
            currentLatitude=mLastLocation.latitude
            currentLongitude=mLastLocation.longitude
            readLocationForState()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }else{
                locationDetectionFailed()
            }
        }
    }

}
