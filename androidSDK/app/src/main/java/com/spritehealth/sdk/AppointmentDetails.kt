
package com.spritehealth.sdk

import CustomTabHelper
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.Appointment
import kotlinx.android.synthetic.main.custom_toolbar.*


internal class AppointmentDetails : AppCompatActivity() {

    private lateinit var customTabsIntent: CustomTabsIntent

    private var requestCode: Int=0
    private var WEB_URL_TO_LAUNCH: String? =null
    private var appointment: Appointment? =null
    var webView: WebView?=null
    var progressBar:ProgressBar?=null

    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson = builder.create()

    private var customTabHelper: CustomTabHelper = CustomTabHelper()

    val mainContext=this;

    val clientSdkInstance = SpriteHealthClient.getInstance(this)

    private val PERMISSION_REQUEST_CODE = 200
    var OPEN_NEW_ACTIVITY:Int = 12345

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)
        //setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM;
        getSupportActionBar()?.setCustomView(R.layout.custom_toolbar);

        val tvPageHeading = supportActionBar!!.customView.findViewById<TextView>(R.id.tvPageHeading)
        tvPageHeading.text = "Appointment Details"

        imgvBack.setOnClickListener(){
            this.finish();
        }

        webView =findViewById(R.id.webView1);
        progressBar =findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);

        val bundle = intent.extras
        if (bundle != null && intent.hasExtra("appointmentJSON")) {
            val appointmentJSON:String?=intent.getStringExtra("appointmentJSON")
            val type = object : TypeToken<Appointment>() {}.type
            appointment=gson.fromJson(appointmentJSON, type)

            val brandingParam:String="&wlb=true%3Bimplicit%3B${SpriteHealthClient.member.organizationId}"
            WEB_URL_TO_LAUNCH=SpriteHealthClient.SSOWebAppRoot + "/globals/appointments/${appointment?.id}?$brandingParam#access_token=${SpriteHealthClient.auth_token}"

        }

        
        /*

        if (checkPermission()) {
            //main logic or main code
            // . write your main code to execute, It will execute if the permission is already given.

        } else {
            requestPermission();
        }
        
        */


    }

    override fun onResume() {
        super.onResume()
        //if (requestCode >= 0){
            if (requestCode == OPEN_NEW_ACTIVITY && SpriteHealthClient.storedIntent != null) {
                requestCode = -1
                this.startActivity(SpriteHealthClient.storedIntent)
            } else {
                triggerPage()
            }
     //}
    }

    fun triggerPage(){


        val builder = CustomTabsIntent.Builder()
        // modify toolbar color
        //builder.setToolbarColor(ContextCompat.getColor(this, R.color.primary))
        // add share button to overflow men
        //builder.addDefaultShareMenuItem()
        // add menu item to oveflow
        // builder.addMenuItem("MENU_ITEM_NAME", pendingIntent)
        // show website title
        //builder.setUrlBarHidingEnabled(true).setShowTitle(false)
        // modify back button icon
        //builder.setCloseButtonIcon(bitmap)
        // menu item icon
        //builder.setActionButton(bitmap, "Android", pendingIntent, true)
        // animation for enter and exit of tab            builder.setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
        builder.setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
        //By default, if we don’t set any animations then the

        customTabsIntent = builder.build()
        requestCode=OPEN_NEW_ACTIVITY
        // check is chrom available
        val packageName = WEB_URL_TO_LAUNCH?.let { CustomTabHelper().getPackageNameToUse(this, it) }
        if (packageName == null)
        // if chrome not available open in web view
            loadDetailsPage()
        else {

            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(this, Uri.parse(WEB_URL_TO_LAUNCH))

            /*
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse(WEB_URL_TO_LAUNCH)
            startActivityForResult(openURL,OPEN_NEW_ACTIVITY)
            */
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (requestCode == OPEN_NEW_ACTIVITY && SpriteHealthClient.storedIntent!=null) {
            this.startActivity(SpriteHealthClient.storedIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_NEW_ACTIVITY && SpriteHealthClient.storedIntent!=null) {
           this.startActivity(SpriteHealthClient.storedIntent)
        }
    }


    private fun checkPermission(): Boolean {
        val cameraAccessGranted:Boolean=ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )== PackageManager.PERMISSION_GRANTED
        val audioAccessGranted:Boolean=ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        return if (!cameraAccessGranted || !audioAccessGranted ) {
            // Permission is not granted
            false
        } else true
    }


        private fun requestPermission() {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        }

    /*
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
        ) {
            when (requestCode) {
                PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()

                    // main logic
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            showMessageOKCancel("You need to allow access permissions",
                                DialogInterface.OnClickListener { dialog, which ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermission()
                                    }
                                })
                        }
                    }
                }
            }
        }

        private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        }


    fun permission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You already granted the permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(
                this,
                String()[]{ Manifest.permission.CAMERA },
                MY_PERMISSION_REQUEST_CAMERA
            );
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You already granted the permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(
                this,
                new String []{ Manifest.permission.RECORD_AUDIO },
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
            );
        }
    }


        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
        ) {
            if (requestCode == MY_PERMISSION_REQUEST_CAMERA || requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    */
    @SuppressLint("SetJavaScriptEnabled")
    private fun loadDetailsPage() {
    val webSetting = webView?.settings
    if (webSetting != null) {
        webSetting.builtInZoomControls = true
    }
    if (webSetting != null) {
        webSetting.javaScriptEnabled = true
    }
    // webView?.webViewClient = MyWebViewClient()
    this.setWebClient()

    WEB_URL_TO_LAUNCH?.let { webView?.loadUrl(it) }
}

    private fun setWebClient() {
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                super.onPermissionRequest(request)

                if (request != null) {
                    //request.grant(request.getResources());
                }
                runOnUiThread { request!!.grant(request.resources) }
            }
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult
            ): Boolean {

                result.confirm()
                return true
            }

            override fun onProgressChanged(
                view: WebView,
                newProgress: Int
            ) {
                super.onProgressChanged(view, newProgress)

            }
        }
    }

    private class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return true
        }


    }



}