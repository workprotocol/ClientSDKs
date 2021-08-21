package com.spritehealth.sdk


import Util
import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.spritehealth.sdk.model.*


class SpriteHealthClient(): AppCompatActivity(){

    private val timeOutInMS: Int=50000
    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson = builder.create()

    /*
    constructor(client_id: String, user_identity: String, context: Context) : this() {
        // some code
        SpriteHealthClient.client_id = client_id;
        SpriteHealthClient.user_identity = user_identity
        GlobalScope.launch(Dispatchers.IO) {
            val job = launch { createToken(context) }
            job.join()
        }
    }
    */


    interface Callback<T>{
        fun onSuccess(result: T)
        fun onError(error: String?)
    }



    private fun callGetRequest(context: Context, url: String, callback: Callback<String?>)
    {
        val auth_token = auth_token
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object: StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: " + response)
                callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error
                val errorStr = "ERROR: %s".format(error.toString());
                Log.d("msg :", errorStr);
                callback.onError(errorStr);
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $auth_token"
                return headers
            }

        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            timeOutInMS,
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
    )
    {
        val auth_token = auth_token
        val queue = Volley.newRequestQueue(context)
         var urlWithQureyString=""
        val queryString:String?=Util().encodeParameters(queryParams)
        if(url!=null && url.contains("?")){
            urlWithQureyString=url.plus("&$queryString")
        }else{
            urlWithQureyString=url.plus("?$queryString")
        }

        val stringRequest = object: StringRequest(Request.Method.GET, urlWithQureyString,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: " + response)
                callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error
                val errorStr = "ERROR: %s".format(error.toString());
                Log.d("msg :", errorStr);
                callback.onError(errorStr);
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $auth_token"
                return headers
            }

        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            timeOutInMS,
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
    )
    {
        val auth_token = auth_token
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object: StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: " + response)

                callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error

                val errorStr = "ERROR: %s".format(error.toString());
                Log.d("msg :", errorStr);
                callback.onError(errorStr);
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $auth_token"
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
               return postDataParams
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            timeOutInMS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue.add(stringRequest)
    }

    fun createAccessToken(context: Context, callback: Callback<AccessTokenResponse?>)
    {
        val url ="$apiRoot/oauth/authorize?response_type=token&client_id=" +
                    SpriteHealthClient.client_id +
                    "&no_redirect=true&is_sso=true&skip_user_auth=true&user_identity=" +
                    SpriteHealthClient.user_identity + ""

        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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


    fun getMemberDetails(context: Context, callback: Callback<User>)
    {
        val url = "$apiRoot/user?withCoverage=true"
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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


    fun getFamilyMembers(familyId: String?, context: Context, callback: Callback<List<User>>)
    {
        val url = "$apiRoot/user/family/members?familyId=$familyId";
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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
    ){
        var url =  "$apiRoot/specialists/available"
        callGetRequestWithParams(
            context,
            queryParams,
            url,
            object : SpriteHealthClient.Callback<String?> {
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


    fun getSpecialistDetails(specialistId: Long?, context: Context, callback: Callback<Specialist>)
    {
        val url = "$apiRoot/user/specialists/$specialistId?slim=HIGH";
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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


    fun getServiceDetails(serviceId: Long?, context: Context, callback: Callback<Service>)
    {
        val url = "$apiRoot/services/$serviceId?slim=HIGH";
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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

    fun getSpecialities(context: Context, callback: Callback<List<Speciality>>){
        val url= "$apiRoot/file/JSON/specialities.json";
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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
    )
    {
        val url = "$apiRoot/reasons"
        callGetRequestWithParams(
            context,
            params,
            url,
            object : SpriteHealthClient.Callback<String?> {
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
    )
    {
        val url ="$apiRoot/specialists/available"
        callGetRequestWithParams(
            context,
            params,
            url,
            object : SpriteHealthClient.Callback<String?> {
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
        callback: SpriteHealthClient.Callback<NetworkCoverage>
    ) {
        var url = "$apiRoot/serviceCoverages/fetchMAA"
        callPostRequest(context, formPost, url, object : SpriteHealthClient.Callback<String?> {
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
    )
    {
        val url = "$apiRoot/reasons/$reasonId/services?vendorId=$vendorId"
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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
    )
    {
        val url = "$apiRoot/brandThemes?target=$target"
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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
    )
    {
        val url = "$apiRoot/developerAccounts?clientId=$clientId"
        callGetRequest(context, url, object : SpriteHealthClient.Callback<String?> {
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
    )
    {
        var url = "$apiRoot/calendar/event"
        callPostRequest(context, formPost, url, object : SpriteHealthClient.Callback<String?> {
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


    public fun launchVPTFlow(calleeIntent: Intent, context: Context, attrs: HashMap<String, String>) {
        storedIntent = calleeIntent;
        val intent= Intent(context, VPTFinder::class.java).apply {
            for ((key, value) in attrs) {
                require(!(key == null || value == null)) {
                    this.putExtra(key, value)
                }
            }
        }
        if(intent != null) {
            context.startActivity(intent)
        }
    }

    enum class Mode{
        LIVE,TEST;
    }

 enum class APIDomains(val value: String) {

     LIVE("wpbackendprod.appspot.com"),
     TEST("wpbackendqa.appspot.com");

 }

    enum class WebClientDomains(val value: String) {
        LIVE("wpfrontendprod.appspot.com"),
        TEST("wpfrontendqa.appspot.com");


    }


    companion object {
        var storedIntent: Intent = Intent()

        var selectedMode=Mode.TEST//default

        internal var targetAPIDomain:String=APIDomains.TEST.value
        internal var targetWebClientDomain:String=WebClientDomains.TEST.value

        internal val SSOWebAppRoot: String="https://$targetWebClientDomain"

        internal  var apiRoot = "https://$targetAPIDomain/resources"

        //Keep it in memory
        internal var member=User()
        internal var specialities :List<Speciality>?= ArrayList()

        //TODO: MAKE IT DYNAMIC
        internal  var auth_token:String? =""
        //TODO: To ask from host app
        var user_identity = "dag@berger.com"
        var client_id = "0b5c8d72f9794ec69870886cd060bc82"
        var expires_in:Long? = 3600//min

    }
}
