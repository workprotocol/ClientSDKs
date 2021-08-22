package com.spritehealth.sdk


import Util
import android.app.Activity
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

    private var mContext: Context = context


    companion object : SingletonHolder<SpriteHealthClient, Context>(::SpriteHealthClient) {

        //var launchingActivity: Intent? = null
        var launchAttributes: HashMap<String, String>?=null
        val timeOutInMS: Int = 50000
        var builder: GsonBuilder = GsonBuilder();
        var gson: Gson = builder.create()

        var storedIntent: Intent? = null

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


    fun initialize(
        //context: Context,
        initOptions: InitOptions,
        callback: Callback<InitializationStatus>
    ) {
        //this.mContext = context
        if (initOptions.clientId.isEmpty() || initOptions.userIdentity.isEmpty()) {
            callback.onError("clientId and userIdentity are mandatory fields of initOptions to initialize.")
            return
        }
        
        client_id= initOptions.clientId
        user_identity=initOptions.userIdentity

        selectedMode = initOptions.integrationMode
        if (selectedMode == IntegrationMode.TEST) {
            apiRoot = "https://${APIDomains.TEST.value}/resources"
            SSOWebAppRoot = "https://${WebClientDomains.TEST.value}"

        } else {
            apiRoot = "https://${APIDomains.LIVE.value}/resources"
            SSOWebAppRoot = "https://${WebClientDomains.LIVE.value}"
        }

        //create access token
        createAccessToken(mContext, object : Callback<AccessTokenResponse?> {
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


    /**
     * Adds a [member] to this group.
     *
     * @return the new size of the group.
     */
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



    /**
     * Gets profile details of current logged-in user/member

     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess returns [User] object
     */
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

    /**
     * Gets list of members ([User]) of a family by familyId.
     *
     * @param [familyId] familyId
     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns list of members [User]
     */
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


    /**
     * Returns list of specialists with their first availability.
     *
     * @param [queryParams] HashMap of parameters e.g.
     *
    {

    specialities: 26               --required

    serviceDefinitionIds: 5414975176704000               --required

    startDateTime: 06/22/2021 10:39 am                --required

    currentTime: 12:39:59               --required

    startIndex: 0              --recommended

    endIndex: 10              --recommended

    getOnlyFirstAvailability: true              --recommended

    networkIds: 5783379589988352,6214613415755776

    }

     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns list of specialists with available slots for service
     */
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


    /**
     * Gets specialist details ([Specialist]) by id.
     *
     * @param [specialistId] id of specialist/vendorUser
     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns [Specialist] object
     */
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


    /**
     * Gets service details ([Service]) by id.
     *
     * @param [serviceId] id of service
     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns [Service] object
     */
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

    /**
     * Gets list of all supported specialities ([Speciality]).

     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns list of all [Speciality]
     */
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

    /**
     * Gets list of reasons/issues ([Reason]).
     *
     * @param [queryParams] HashMap of parameters e.g.
     *
     * {
     *
     * specialities = "26,37"               --Optional

     * }

     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns the list of matching reasons
     */
    fun getReasons(
        queryParams: MutableMap<String, String>,
        context: Context,
        callback: Callback<MutableList<Reason>>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/reasons"
        callGetRequestWithParams(
            context,
            queryParams,
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



    /**
     * Gets availability i.e. free slots of specialist by specialistId and other filters
     *
     * @param [queryParams] HashMap of parameters e.g.
     * {

    startDateTime: 06/22/2021 11:10 am              --required

    serviceId: 6207822929854464              --required

    vendorUserId: 6267591627636736              --required

    weeks: 3              --required

    currentTime: 13:10:30              --required


     * }

     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns the list of [SpecialistAvailability].
     */
    fun getSpecialistAvailability(
        queryParams: MutableMap<String, String>,
        context: Context,
        callback: Callback<SpecialistAvailability>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/specialists/available"
        callGetRequestWithParams(
            context,
            queryParams,
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


    /**
     * Computes network coverage ([NetworkCoverage] ) of a service for a member given a provider and service code
     *
     * @param [formPost] HashMap of parameters e.g.
     *
     * {
     *
     * memberId: 6282717383622656               --required

    providerId: 5438400756711424               --required

    serviceCode: CPT 97110               --required

    termType: Purchase               --required

    specialistId: 6267591627636736               --required

    operation: COMPUTE               --required

    units: 1

    organizationId: 4710567524696064

    specialityNames: Physical Therapist


     * }

     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. OnSuccess event returns [NetworkCoverage] object
     */
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


    /**
     * Gets list of services ([Service]) by reasonId.
     *
     * @param [reasonId] id of reason object
     * @param [providerId] id of vendor/provider object
     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. onSuccess event returns list of services.
     */
    fun getServicesByReason(
        reasonId: Long?,
        providerId: Long?,
        context: Context,
        callback: Callback<List<Service>>
    ) {
        val url = "${SpriteHealthClient.apiRoot}/reasons/$reasonId/services?vendorId=$providerId"
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





    /**
     * Creates appointment ([Appointment]).
     *
     * @param [formPost] HashMap of parameters e.g.
     *
     * {
     *
    vendorUserId: 6267591627636736                --required

    providerName: Anne Wachsmann                --required
    
    serviceId: 6207822929854464                --required
        
    serviceName: Virtual Physical Therapy                --required
    
    patientId: 6282717383622656               --required
    
    serviceSetting: Telehealth               --required
    
    eventStartTime: 06/22/2021 02:15 pm                --required
    
    eventEndTime: 06/22/2021 03:00 pm                --required
    
    status: booked               --required
    
    customerPhone: +91 97808xxxx08              --required
    
    reasonId: 6272244076511232
    
    reasonName: Ankle pain  
    
    where: address or other details
        
    walletId: 6317246169219072

     *
     * }

     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. OnSuccess event returns appointment object.
     */
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

    /**
     * Get appointment details ([Appointment]) by appointmentId.
     *
     * @param [appointmentId] id of appointment
     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events.  OnSuccess event returns appointment object.
     */
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

    /**
     * Gets list of appointments ([Appointment]) by patientId and fetchMode.
     *
     * @param [patientId] id of member
     * @param [fetchMode] values are Upcoming or All
     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. OnSuccess event returns list of appointments
     */
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

    /**
     * Gets list of appointments ([Appointment]) for current logged-in user by fetchMode.
     *
     * @param [fetchMode] values are Upcoming or All
     * @param [context] Context of caller activity/application.
     * @param [callback] Callback object with onSuccess and onError events. OnSuccess event returns list of appointments
     */
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


    /**
     * Launches the UI flow for Virtual Physical Therapy Care
     *
     * @param [calleeIntent] Intent of the callee activity so as to return to it once UI flow ends
     * @param [context] Context of caller activity/application.
     * @param [attrs] HashMap of attributes e.g. { state = "TX"}
     */
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


    /**
     * ============================
     * Private and Internal methods/properties
     * ============================
     */


    internal fun createAccessToken(context: Context, callback: Callback<AccessTokenResponse?>) {
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


}


