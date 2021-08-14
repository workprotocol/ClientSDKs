
package com.spritehealth.sdk
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_specialist_detail.*
import org.json.JSONObject


internal class PreviewAppointment : AppCompatActivity() {

    var specialist:User?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_appointment)
        setSupportActionBar(findViewById(R.id.toolbar))

        var id = intent.getStringExtra("id")

        if(intent.hasExtra("specialistJSON")) {
            val specialistJSON = intent.getStringExtra("specialistJSON")

            var builder: GsonBuilder = GsonBuilder();
            var gson: Gson = builder.create()
            val specialistType = object : TypeToken<User>() {}.type
            specialist = gson.fromJson(specialistJSON.toString(), specialistType);

            if (specialist != null) {
                tvName.text = specialist!!.name

                if (specialist!!.specializationNames != null) {
                    tvSpeciality.text = specialist!!.specializationNames
                }
            }
        }

        val objCommon = SpriteHealthClient()

        var response="";
        var progressBar: ProgressBar = findViewById(R.id.progressBar);
        progressBar.visibility = View.VISIBLE


    }

    fun displaySpecailistDetail(specialistJSON:JSONObject?) {
        
        var builder: GsonBuilder = GsonBuilder();
        var gson: Gson =builder.create()
        val specialistType = object : TypeToken<User>() {}.type
        var specialistUser:User= gson.fromJson(specialistJSON.toString(), specialistType);

        val textView = findViewById<TextView>(R.id.tvName)
        textView.text = specialistUser.name


        if (specialistUser.imageIds!=null && specialistUser.imageIds!!.isNotEmpty()) {
            var imageId = specialistUser.imageIds!!.iterator().next()

            var imageUrl = SpriteHealthClient.apiRoot+"/resources/images/" + imageId.toString()
            Picasso.get().load(imageUrl).into(imgvUser)
        }

        var specialityNames:String?=""
        if(specialistUser.specialization!!.isNotEmpty()){

            var speciality=SpriteHealthClient.specialities.find {
                if(it.value!=null  && specialistUser.specialization!=null){
                    it.value!! == specialistUser.specialization!![0];
                }else{
                    false
                }
            }

            if (speciality != null) {
                specialityNames=speciality.name
                //save it for later use
                specialistUser.specializationNames=specialityNames;
                tvSpeciality.text=specialityNames;
            };
        }



        val genderView = findViewById<TextView>(R.id.tvGender)
        genderView.text = specialistUser.gender

   if(specialistUser.prefferedLanguage!=null){
       lloLanguage.visibility=View.VISIBLE
       tvLanguage.text = specialistUser.prefferedLanguage
   }

    }


    fun bookVisit(view: View){
        var intent = SpriteHealthClient.storedIntent.apply {}
        startActivity(intent)

        //val shObject: SpriteHealth  = SpriteHealth();
        //shObject.callMain();
    }



}
