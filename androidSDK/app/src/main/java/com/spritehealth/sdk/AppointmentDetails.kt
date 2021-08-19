
package com.spritehealth.sdk

import CustomTabHelper
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.Appointment


internal class AppointmentDetails : AppCompatActivity() {

    private var WEB_URL_TO_LAUNCH: String? =null
    private var appointment: Appointment? =null
    var webView: WebView?=null
    var progressBar:ProgressBar?=null

    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson = builder.create()

    private var customTabHelper: CustomTabHelper = CustomTabHelper()

    val mainContext=this;

    val clientSdkInstance = SpriteHealthClient()

    private val PERMISSION_REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)
        //setSupportActionBar(findViewById(R.id.toolbar))
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


        if (checkPermission()) {
            //main logic or main code
            // . write your main code to execute, It will execute if the permission is already given.

        } else {
            requestPermission();
        }



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

        val customTabsIntent = builder.build()

        // check is chrom available
        val packageName = WEB_URL_TO_LAUNCH?.let { CustomTabHelper().getPackageNameToUse(this, it) }
        if (packageName == null)
        // if chrome not available open in web view
            loadDetailsPage()
        else {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(this, Uri.parse(WEB_URL_TO_LAUNCH))
        }

    }




    private fun checkPermission(): Boolean {
        val cameraAccessGranted:Boolean=ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
        val audioAccessGranted:Boolean=ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        return if (!cameraAccessGranted || !audioAccessGranted ) {
            // Permission is not granted
            false
        } else true
    }


        private fun requestPermission() {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO),
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

            /*
           if (Uri.parse(url).host == "www.example.com") {
               // This is my web site, so do not override; let my WebView load the page
               return false
           }

           // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
           Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
               startActivity(this)
           }
           */
            return true
        }


    }



}