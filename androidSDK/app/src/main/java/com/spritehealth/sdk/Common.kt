package com.spritehealth.sdk


import Util
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.SpriteHealthClient.Companion.gson
import com.spritehealth.sdk.SpriteHealthClient.Companion.storedIntent
import com.spritehealth.sdk.model.*
import kotlinx.android.synthetic.main.activity_vptfinder.*


open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

class SpriteHealthClient private constructor(context: Context) {

    private lateinit var mContext: Context

    init {
        this.mContext = context
    }

    fun doSomething() {

    }

    companion object : SingletonHolder<SpriteHealthClient, Context>(::SpriteHealthClient) {

        var launchAttributes: HashMap<String, String>?=null
        val timeOutInMS: Int = 50000
        var builder: GsonBuilder = GsonBuilder();
        var gson: Gson = builder.create()

        var storedIntent: Intent = Intent()

        var selectedMode = IntegrationMode.TEST//default

        internal var targetAPIDomain: String = APIDomains.TEST.value
        internal var targetWebClientDomain: String = WebClientDomains.TEST.value

        internal var SSOWebAppRoot: String = "https://$targetWebClientDomain"

        internal var apiRoot = "https://$targetAPIDomain/resources"

        //Keep it in memory
        internal var member = User()
        internal var specialities: List<Speciality>? = ArrayList()

        //TODO: MAKE IT DYNAMIC
        internal var auth_token: String? = ""

        //TODO: To ask from host app
        var user_identity = ""// "dag@berger.com"
        var client_id = ""// "0b5c8d72f9794ec69870886cd060bc82"
        var expires_in: Long? = 3600//min

    }


//class SpriteHealthClient(): AppCompatActivity(){

    fun initialize(
        context: Context,
        initOptions: InitOptions,
        callback: Callback<InitializationStatus>
    ) {
        this.mContext = context
        if (initOptions.clientId.isEmpty() || initOptions.userIdentity.isEmpty()) {
            callback.onError("clientId and userIdentity are mandatory fields of initOptions to initialize.")
            return
        }
        
        SpriteHealthClient.client_id= initOptions.clientId
        SpriteHealthClient.user_identity=initOptions.userIdentity        

        selectedMode = initOptions.integrationMode
        if (selectedMode == IntegrationMode.TEST) {
            apiRoot = "https://${APIDomains.TEST.value}/resources"
            SSOWebAppRoot = "https://${WebClientDomains.TEST.value}"

        } else {
            apiRoot = "https://${APIDomains.LIVE.value}/resources"
            SSOWebAppRoot = "https://${WebClientDomains.LIVE.value}"
        }

        //create access token
        createAccessToken(context, object : Callback<AccessTokenResponse?> {
            override fun onSuccess(accessTokenResponse: AccessTokenResponse?) {
                if (accessTokenResponse != null) {
                    auth_token = accessTokenResponse.access_token
                    expires_in = accessTokenResponse.expires_in;
                    fetchMemberDetails(callback)
                } else {
                    Toast.makeText(mContext, "Failed to authenticate client.", Toast.LENGTH_LONG)
                        .show()
                    callback.onError("Failed to generate access token, please verify clientId and userIdentity for correct values.")
                }
            }

            override fun onError(error: String?) {
                var errorMsg = error
                callback.onError("Failed to generate access token, please verify clientId and userIdentity for correct values. Error: $error")
            }
        })
    }


    fun fetchMemberDetails(callback: Callback<InitializationStatus>) {
        getMemberDetails(mContext, object : Callback<User> {
            override fun onSuccess(memberInfo: User) {
                if (memberInfo != null) {
                    SpriteHealthClient.member = memberInfo
                    callback.onSuccess(
                        InitializationStatus(
                            "Success",
                            "Initialized sdk client successfully. Ready to use now..."
                        )
                    )
                } else {
                    Toast.makeText(mContext, "Failed to access member details.", Toast.LENGTH_LONG)
                        .show()
                    callback.onError("Failed to get member's profile data. Please check user identity.")
                }
            }

            override fun onError(error: String?) {
                var errorMsg = error
                callback.onError("Failed to get member's profile data. Please check user identity. Error: $error")
            }
        })
    }


    interface Callback<T> {
        fun onSuccess(result: T)
        fun onError(error: String?)
    }


    private fun callGetRequest(context: Context, url: String, callback: Callback<String?>) {

        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                //Log.d("A", "Response is: " + response)
                callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error
                val errorStr = "ERROR: %s".format(error.toString());
                //Log.d("msg :", errorStr);
                callback.onError(errorStr);
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${SpriteHealthClient.auth_token}"
                return headers
            }

        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            SpriteHealthClient.timeOutInMS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)
    }

    private fun callGetRequestWithParams(
        context: Context,
        queryParams: MutableMap<String, String>,
        url: String,
        callback: Callback<String?>
    ) {

        val queue = Volley.newRequestQueue(context)
        var urlWithQureyString = ""
        val queryString: String? = Util().encodeParameters(queryParams)
        if (url != null && url.contains("?")) {
            urlWithQureyString = url.plus("&$queryString")
        } else {
            urlWithQureyString = url.plus("?$queryString")
        }

        val stringRequest = object : StringRequest(Request.Method.GET, urlWithQureyString,
            Response.Listener<String> { response ->
                //Log.d("A", "Response is: " + response)
                callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error
                val errorStr = "ERROR: %s".format(error.toString());
                //Log.d("msg :", errorStr);
                callback.onError(errorStr);
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${SpriteHealthClient.auth_token}"
                return headers
            }

        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            SpriteHealthClient.timeOutInMS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)
    }

    private fun callPostRequest(
        context: Context,
        postDataParams: MutableMap<String, String>,
        url: String,
        callback: Callback<String?>
    ) {

        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response ->
                ////Log.d("A", "Response is: " + response)
                callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error

                val errorStr = "ERROR: %s".format(error.toString());
                //Log.d("msg :", errorStr);
                callback.onError(errorStr);
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${SpriteHealthClient.auth_token}"
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                return postDataParams
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            SpriteHealthClient.timeOutInMS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)
    }

    fun createAccessToken(context: Context, callback: Callback<AccessTokenResponse?>) {
        val url = "${SpriteHealthClient.apiRoot}/oauth/authorize?response_type=token&client_id=" +
                SpriteHealthClient.client_id +
                "&no_redirect=true&is_sso=true&skip_user_auth=true&user_identity=" +
                SpriteHealthClient.user_identity + ""

        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val accessTokenType = object : TypeToken<AccessTokenResponse>() {}.type
                var accessTokenResponse: AccessTokenResponse = gson.fromJson(
                    response,
                    accessTokenType
                );
                callback.onSuccess(accessTokenResponse);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })

    }


    fun getMemberDetails(context: Context, callback: Callback<User>) {
        val url = "${SpriteHealthClient.apiRoot}/user?withCoverage=true"
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val userType = object : TypeToken<User>() {}.type
                var member: User = gson.fromJson(response, userType);

                callback.onSuccess(member)
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })

    }


    fun getFamilyMembers(familyId: String?, context: Context, callback: Callback<List<User>>) {
        val url = "${SpriteHealthClient.apiRoot}/user/family/members?familyId=$familyId";
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<List<User>>() {}.type
                var members: List<User> = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(members);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }


    fun getAvailableSpecialists(
        queryParams: MutableMap<String, String>,
        context: Context,
        callback: Callback<List<Specialist>>
    ) {
        var url = "${SpriteHealthClient.apiRoot}/specialists/available"
        callGetRequestWithParams(
            context,
            queryParams,
            url,
            object : Callback<String?> {
                override fun onSuccess(response: String?) {
                    val type = object : TypeToken<List<Specialist>>() {}.type
                    var specialistsWithAvailability: List<Specialist> = gson.fromJson(
                        response,
                        type
                    );
                    callback.onSuccess(specialistsWithAvailability);
                }

                override fun onError(error: String?) {
                    callback.onError(error)
                }
            })
    }


    fun getSpecialistDetails(
        specialistId: Long?,
        context: Context,
        callback: Callback<Specialist>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/user/specialists/$specialistId?slim=HIGH";
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<Specialist>() {}.type
                var specialist: Specialist = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(specialist);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }


    fun getServiceDetails(serviceId: Long?, context: Context, callback: Callback<Service>) {
        val url = "${SpriteHealthClient.apiRoot}/services/$serviceId?slim=HIGH";
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<Service>() {}.type
                var service: Service = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(service);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }

    fun getSpecialities(context: Context, callback: Callback<List<Speciality>>) {
        val url = "${SpriteHealthClient.apiRoot}/file/JSON/specialities.json";
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<List<Speciality>>() {}.type
                var specialities: List<Speciality> = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(specialities);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }

    fun getReasons(
        context: Context,
        params: MutableMap<String, String>, callback: Callback<MutableList<Reason>>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/reasons"
        callGetRequestWithParams(
            context,
            params,
            url,
            object : Callback<String?> {
                override fun onSuccess(response: String?) {
                    val type = object : TypeToken<MutableList<Reason>>() {}.type
                    var reasons: MutableList<Reason> = gson.fromJson(
                        response,
                        type
                    );
                    callback.onSuccess(reasons);
                }

                override fun onError(error: String?) {
                    callback.onError(error)
                }
            })
    }


    fun getSpecialistAvailability(
        params: MutableMap<String, String>,
        context: Context,
        callback: Callback<SpecialistAvailability>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/specialists/available"
        callGetRequestWithParams(
            context,
            params,
            url,
            object : Callback<String?> {
                override fun onSuccess(response: String?) {
                    val type = object : TypeToken<SpecialistAvailability>() {}.type
                    var specialistAvailability: SpecialistAvailability = gson.fromJson(
                        response,
                        type
                    );
                    callback.onSuccess(specialistAvailability);
                }

                override fun onError(error: String?) {
                    callback.onError(error)
                }
            })
    }


    fun getServiceNetworkCoverage(
        formPost: MutableMap<String, String>,
        context: Context,
        callback: Callback<NetworkCoverage>
    ) {
        var url = "${SpriteHealthClient.apiRoot}/serviceCoverages/fetchMAA"
        callPostRequest(context, formPost, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<NetworkCoverage>() {}.type
                var networkCoverage: NetworkCoverage = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(networkCoverage);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }


    fun getServicesByReason(
        reasonId: Long?,
        vendorId: Long?,
        context: Context,
        callback: Callback<List<Service>>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/reasons/$reasonId/services?vendorId=$vendorId"
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<List<Service>>() {}.type
                var services: List<Service> = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(services);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }


    internal fun getBrandThemes(
        target: String?,
        context: Context,
        callback: Callback<List<BrandTheme>>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/brandThemes?target=$target"
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<List<BrandTheme>>() {}.type
                var brandThemes: List<BrandTheme> = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(brandThemes);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }

    internal fun getDeveloperAccount(
        clientId: String,
        context: Context,
        callback: Callback<DeveloperAccount>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/developerAccounts?clientId=$clientId"
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<DeveloperAccount>() {}.type
                var account: DeveloperAccount = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(account);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }

    fun createAppointment(
        formPost: MutableMap<String, String>,
        context: Context,
        callback: Callback<CalendarEvent>
    ) {
        var url = "${SpriteHealthClient.apiRoot}/calendar/event"
        callPostRequest(context, formPost, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<CalendarEvent>() {}.type
                var calendarEvent: CalendarEvent = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(calendarEvent);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }

    fun getAppointmentDetails(
        appointmentId: Long,
        context: Context,
        callback: Callback<Appointment>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/appointment/$appointmentId"
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<Appointment>() {}.type
                var appointment: Appointment = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(appointment);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }


    fun getAppointments(
        patientId: Long?,
        fetchMode: String?,
        context: Context,
        callback: Callback<List<Appointment>>
    ) {
        val url =
            "${SpriteHealthClient.apiRoot}/appointment?patientId=$patientId&fetchMode=$fetchMode"
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<List<Appointment>>() {}.type
                var appointments: List<Appointment> = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(appointments);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }


    fun getMyAppointments(
        fetchMode: String?,
        context: Context,
        callback: Callback<List<Appointment>>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/appointment/myappointments?fetchMode=$fetchMode"
        callGetRequest(context, url, object : Callback<String?> {
            override fun onSuccess(response: String?) {
                val type = object : TypeToken<List<Appointment>>() {}.type
                var appointments: List<Appointment> = gson.fromJson(
                    response,
                    type
                );
                callback.onSuccess(appointments);
            }

            override fun onError(error: String?) {
                callback.onError(error)
            }
        })
    }


    public fun launchVPTFlow(
        calleeIntent: Intent,
        context: Context,
        attrs: HashMap<String, String>
    ) {
        storedIntent = calleeIntent;
        SpriteHealthClient.launchAttributes=attrs
        
        val intent = Intent(context, VPTFinder::class.java).apply {
            for ((key, value) in attrs) {
                require(!(key == null || value == null)) {
                    this.putExtra(key, value)
                }
            }
        }
        if (intent != null) {
            context.startActivity(intent)
        }
    }

}


