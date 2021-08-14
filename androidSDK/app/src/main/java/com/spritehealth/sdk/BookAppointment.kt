
package com.spritehealth.sdk


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.Reason
import com.spritehealth.sdk.model.Service
import com.spritehealth.sdk.model.SpecialistAvailability
import com.spritehealth.sdk.model.User
import kotlinx.android.synthetic.main.activity_book_appointment.*
import kotlinx.android.synthetic.main.activity_specialist_detail.*
import kotlinx.android.synthetic.main.activity_specialist_detail.progressBar
import org.json.JSONArray
import org.json.JSONObject


internal class BookAppointment : AppCompatActivity() {


    private var selectedReason: Reason? = null
    private var specialistAvailability: SpecialistAvailability? = null
    private var reasonList: List<Reason>? = null
    var specialist:User?=null;
    var service: Service?=null
    var serviceId:Long=0;
    val sdkClientInstance = SpriteHealthClient()
    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson = builder.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)
        setSupportActionBar(findViewById(R.id.toolbar))

        var progressBar: ProgressBar = findViewById(R.id.progressBar);

        if(intent.hasExtra("serviceId")){
            serviceId=intent.getLongExtra("serviceId", 0)
        }

        if(intent.hasExtra("specialistJSON")) {
            val specialistJSON = intent.getStringExtra("specialistJSON")
            val specialistType = object : TypeToken<User>() {}.type
            specialist = gson.fromJson(specialistJSON.toString(), specialistType);

            if (specialist != null) {
              fetchReasons();
                if(serviceId>0) {
                    fetchServiceDetails()
                    fetchAvailableSlots(true);
                }

            }
        }
    }


    private fun fetchReasons(){
        progressBar.visibility = View.VISIBLE

        specialist?.specialization?.let {
            sdkClientInstance.fetchReasonsBySpecialities(
                it.joinToString { "," },
                this,
                object : SpriteHealthClient.Callback {
                    override fun onSuccess(response: String?) {
                        var reasonArrayJson = JSONArray(response)
                        readReasons(reasonArrayJson)
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(error: String?) {
                        var errorMsg = error
                        progressBar.visibility = View.GONE
                    }
                })
        }

    }

    private fun readReasons(jsonResponse: JSONArray) {
        val reasonListType = object : TypeToken<List<Reason>>() {}.type
        reasonList = gson.fromJson(jsonResponse.toString(), reasonListType);

        if (reasonList?.isNotEmpty() == true) {

            //sort reasonNames by available date in Ascending order
                /*
            Collections.sort(reasonList, object : Comparator<Reason?>() {
                fun compare(reason1: Reason, reason2: Reason): Int {
                    var returnValue = 0
                    try {
                        returnValue =
                            reason1.name.toUpperCase().compareTo(reason2.name.toUpperCase())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return returnValue
                }
            })
            */
            //reasonList.add(0, Reason(0L, "select reason"))

            val reasonNames: MutableList<String> = ArrayList()
            for (reason in reasonList!!) {
                reasonNames.add(reason.name)
            }
            val reasonDataAdapter = ArrayAdapter(this, R.layout.spinner_item, reasonNames)
            reasonDataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            reasonSpinner.adapter = reasonDataAdapter

            reasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        selectedReason = reasonList!!.get(position)
                    } else {
                        selectedReason = null
                    }
                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //selectedReason=null;
                }
            }
        } 

    }

    private fun fetchServiceDetails() {
        progressBar.visibility = View.VISIBLE

        sdkClientInstance.serviceDetail(serviceId, this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                // do stuff here
                var responseJson = JSONObject(response)
                readService(responseJson)
                progressBar.visibility = View.GONE
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun readService(responseJson: JSONObject) {
        val type = object : TypeToken<Service>() {}.type
        service = gson.fromJson(responseJson.toString(), type);

        tvServiceName.text= service?.wpName ?: "";
        fetchServiceCoverage()

    }

    private fun fetchServiceCoverage() {
        progressBar.visibility = View.VISIBLE

        sdkClientInstance.fetchServiceCoverage(
            specialist?.id,
            serviceId,
            3,
            this,
            object : SpriteHealthClient.Callback {
                override fun onSuccess(response: String?) {
                    // do stuff here
                    var responseJson = JSONObject(response)
                    readCoverage(responseJson)
                    progressBar.visibility = View.GONE
                }

                override fun onError(error: String?) {
                    var errorMsg = error
                    progressBar.visibility = View.GONE
                }
            })
    }

    private fun readCoverage(responseJson: JSONObject) {

        val type = object : TypeToken<SpecialistAvailability>() {}.type
        specialistAvailability = gson.fromJson(responseJson.toString(), type);
    }



    private fun fetchAvailableSlots(isFirstTime: Boolean) {
        progressBar.visibility = View.VISIBLE

        sdkClientInstance.specialistAvailableSlot(
            specialist?.id,
            serviceId,
            3,
            this,
            object : SpriteHealthClient.Callback {
                override fun onSuccess(response: String?) {
                    // do stuff here
                    var responseJson = JSONObject(response)
                    readAvailability(responseJson)
                    progressBar.visibility = View.GONE
                }

                override fun onError(error: String?) {
                    var errorMsg = error
                    progressBar.visibility = View.GONE
                }
            })
    }

    private fun readAvailability(responseJson: JSONObject) {
        
        val type = object : TypeToken<SpecialistAvailability>() {}.type
        specialistAvailability = gson.fromJson(responseJson.toString(), type);
    }


    fun loadNextAvailability(view: View) {

    }

/*

    fun goToConfirmation(view: View){
        var builder:GsonBuilder = GsonBuilder()
        var gson:Gson =builder.create()

        val intent = Intent(this, PreviewAppointment::class.java).apply {
           /*
             putExtra("specialistWithAvailabilityJSON", gson.toJson(specialistWithAvailability))
            if(specialistUser!=null) {
                putExtra("specialistJSON", gson.toJson(specialistUser))
            }
            if(specialistWithAvailability!=null && specialistWithAvailability!!.serviceId!=null) {
                putExtra("serviceId", specialistWithAvailability!!.serviceId)
            }
        */
        }
        this.startActivity(intent)
    }
*/

}
