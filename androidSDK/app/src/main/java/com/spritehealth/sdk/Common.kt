package com.spritehealth.sdk


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.spritehealth.sdk.model.Speciality
import com.spritehealth.sdk.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.suspendCoroutine


class SpriteHealthClient(): AppCompatActivity(){


    constructor(client_id: String, user_identity : String, context:Context) : this() {
        // some code
        SpriteHealthClient.client_id = client_id;
        SpriteHealthClient.user_identity = user_identity
        GlobalScope.launch(Dispatchers.IO) {
            val job = launch { createToken(context) }
            job.join()
        }

    }

    interface Callback{
        fun onSuccess(result: String?)
        fun onError(error: String?)
    }

    fun createAccessToken(context: Context, callback :Callback)
    {
        val url =
            apiRoot + "/resources/oauth/authorize?response_type=token&client_id=" +
                    SpriteHealthClient.client_id +
                    "&no_redirect=true&is_sso=true&skip_user_auth=true&user_identity=" +
                    SpriteHealthClient.user_identity + ""
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object: StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: " + response.substring(0,500))
                callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error
                val errorStr = "ERROR: %s".format(error.toString());
                Log.d("msg :",errorStr);
                callback.onError(errorStr);
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                return headers
            }
        }

        queue.add(stringRequest)
    }


    suspend fun createToken(context: Context) = suspendCoroutine<String>
    { cont ->
        val myurl =
            apiRoot + "/resources/oauth/authorize?response_type=token&client_id=" + SpriteHealthClient.client_id + "&no_redirect=true&is_sso=true&skip_user_auth=true&user_identity=" + SpriteHealthClient.user_identity + ""
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object: StringRequest(Request.Method.GET, myurl,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: " + response.substring(0,500))
                val response2 = response
                auth_token = response
                // displayVPT(response);
                // val responseJsonArray: JSONArray = JSONArray(response)
                //callback.onSuccess(response);
            },
            Response.ErrorListener { error ->
                // Handle error

                val errorStr = "ERROR: %s".format(error.toString());
                Log.d("msg :",errorStr);
                //callback.onError(errorStr);
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                return headers
            }
        }

        queue.add(stringRequest);
    }


    fun callGetRequest(context:Context, url: String, callback :Callback)
    {
        val auth_token = auth_token
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object: StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    Log.d("A", "Response is: " + response.substring(0,500))
                    callback.onSuccess(response);
                },
                Response.ErrorListener { error ->
                    // Handle error
                    val errorStr = "ERROR: %s".format(error.toString());
                    Log.d("msg :",errorStr);
                    callback.onError(errorStr);
                })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $auth_token"
                return headers
            }
        }

        queue.add(stringRequest)
    }

    fun callPostRequest(context:Context, appointmentCriteria:JSONObject, url: String, callback :Callback)
    {
        val auth_token = auth_token
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object: StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("A", "Response is: " + response.substring(0,500))

                    callback.onSuccess(response);
                },
                Response.ErrorListener { error ->
                    // Handle error

                    val errorStr = "ERROR: %s".format(error.toString());
                    Log.d("msg :",errorStr);
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
                val params: MutableMap<String, String> = HashMap()
                val iter: Iterator<String> = appointmentCriteria.keys()
                while (iter.hasNext()) {
                    val key = iter.next()
                    try {
                        val value: Any = appointmentCriteria.get(key)
                        params[key] = value.toString().trim()
                    } catch (e: JSONException) {
                        // Something went wrong!
                    }
                }
                return params
            }
        }

        queue.add(stringRequest)
    }


    fun memberDetail( context:Context,callback :Callback  )
    {
        val url = apiRoot +  "/resources/user?withCoverage=true"
        callGetRequest(context, url, callback )
    }

    fun familyMembers(familyId:String?, context:Context,callback :Callback  )
    {
        val url = apiRoot + "/resources/user/family/members?familyId=" + familyId;
        callGetRequest(context, url, callback )
    }


    fun specialistAvailable(context:Context, callback :Callback){

        val formatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
        val  currentTime = LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("hh:mm:ss"))
        val url =  apiRoot + "/resources/specialists/available?"+//TODO: Dynamic parameters
                "specialities=26&serviceDefinitionIds=5414975176704000&startIndex=0&endIndex=100&startDateTime="+
                formatted+"&currentTime="+currentTime+ "&getOnlyFirstAvailability=true&networkIds="

        callGetRequest(context, url, callback )
    }


    fun specialistDetail(specialistId:String?, context:Context,callback :Callback  )
    {
        val url = apiRoot + "/resources/user/specialists/"+specialistId + "?slim=HIGH";
        callGetRequest(context, url, callback )
    }

    fun fetchSpecialities(context:Context,callback :Callback ){
        val url= apiRoot+"/resources/file/JSON/specialities.json";
        callGetRequest(context, url, callback )
    }

    fun reasonList( context:Context,callback :Callback  )
    {
        val url = apiRoot +  "/resources/reasons?specialities=26"
        callGetRequest(context, url, callback )
    }

    fun specialistAvailableSlot(specialistId:String?, context:Context,callback :Callback  )
    {
        val url = apiRoot + "/resources/specialists/available?serviceId=6207822929854464&vendorUserId=" + specialistId + "&weeks=1"
        callGetRequest(context, url, callback )
    }

    fun listOfServiceByReason(reasonId: String?,specialistId:String?, context:Context,callback :Callback  )
    {
        val url = apiRoot + "/resources/reasons/"+reasonId+"/services?vendorId=" + specialistId
        callGetRequest(context, url, callback )
    }

    fun appointmentBooking( context:Context,appointmentCriteria:JSONObject,callback :Callback  )
    {

        var url = apiRoot+ "/resources/appointment"
        var bd = appointmentCriteria
        callPostRequest(context,appointmentCriteria, url, callback )
    }

   public fun callVPTFinder(calleeIntent: Intent, context: Context) {
        storedIntent = calleeIntent;

        //val intent = Intent( context, Class.forName("com.example.spritehealthsdk.MainActivity")).apply
        val intent= Intent( context,VPTFinder::class.java).apply{

        }
        if(intent != null) {
            context.startActivity(intent)
        }
    }

    companion object {
        var storedIntent: Intent = Intent();
        internal  var apiRoot = "https://wpbackendqa.appspot.com"

        //Keep it in memory
        internal var member=User()
        internal var specialities = ArrayList<Speciality>()

        //TODO: MAKE IT DYNAMIC
        internal  var auth_token:String? =""// "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJkZWVwYWsuZ2FnbmVqYUBnbWFpbC5jb20iLCJyb2xlIjoiQ0EiLCJhdXRoR3JhbnRUeXBlIjoicmVzb3VyY2Vfb3duZXJfY3JlZGVudGlhbHMiLCJpc3MiOiJTcHJpdGUgSGVhbHRoIEFQSXMiLCJ0aW1lWm9uZSI6IkFtZXJpY2FcL0NoaWNhZ28iLCJ1c2VyQWdlbnQiOiIiLCJ1c2VySWQiOjU2NDIwMzQ5MzMxMzc0MDgsImF1ZCI6IlJlc291cmNlIENyZWRlbnRpYWxzIGNsaWVudCIsIm5iZiI6MTYyODc4MjczMSwic2NvcGUiOiJmdWxsX2FjY2VzcyIsImV4cCI6MTYyODgxMTUzMSwiYXV0aFR5cGUiOiJHT09HTEUiLCJpYXQiOjE2Mjg3ODI3MzEsImp0aSI6ImM5NGU0NDliLWIxYTktNDU1Zi1iZWQwLWQyZjg2ZWEzYzYyZCIsImVtYWlsIjoiZGVlcGFrLmdhZ25lamFAZ21haWwuY29tIn0.nByHw-gUtVfnUpy02OfBqyFJjNMhBA4ujtF-JYM6WZKkL_Uqg5J3gAM7me343fMyAqWRbp36jSj5HR4hktVhhWzla2GJ6jIZGh6niW4psNAKsTramYDvdcXqAy4bOfdszzgS7EcZWbyCi_FfAB-PKcKrxgFrrE7gdyWfK5Q99ZGTOno1yeYtmUNpWiJ5HDfOieVdkWlzYVCaSQS2XxglDCw4uwP-fmfV2JxCf71kgxz8z6OJi9gIYdjhYyjSd7xUIitKruP9ZsICJOgM5aywId_MEU9lhYEQN9iyk7_JxwE7FQOrHci__MDIN-oitkwrvPstHlvMVc-TIy5jyqMFOw"

        //TODO: To ask from host app
        var user_identity = "dag@berger.com"
        var client_id = "0b5c8d72f9794ec69870886cd060bc82"
        var expires_in:Long? = 3600//min


       /* fun getAuthToken() : String {
            return auth_token;
        } */
    }
}
