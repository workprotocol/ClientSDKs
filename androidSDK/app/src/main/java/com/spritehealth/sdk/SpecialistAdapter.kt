package com.spritehealth.sdk

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.spritehealth.sdk.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.specialist_list_item.view.*
import java.nio.file.Files.find
import java.time.format.DateTimeFormatter

/*
data class SpecialistListItem(
    val id:String,
    val imageResource: String,
    val name: String,
    val specialityNames: String,
    val startTime: String
)

interface CellClickListener {
    fun onCellClickListener()
}
*/

class SpecialistAdapter(private val specialistList: List<User>, private val context: Context) :
    RecyclerView.Adapter<SpecialistAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.specialist_list_item,
            parent, false)
        return ExampleViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = specialistList[position]

        var firstSlot:String? =null
        if (currentItem.availableSlots?.size!! > 0) {
            var timePeriod = currentItem.availableSlots?.get(0);
            if(timePeriod!=null) {
                firstSlot = timePeriod.startTime
                //Format date time as 08/13 - Fri, 05:15 pm
                var inFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
                var firstDate=inFormatter.parse(firstSlot)//08-13-2021 17:15:00
                var outFormatter = DateTimeFormatter.ofPattern("MM/dd - EEE, hh:mm a")
                firstSlot= outFormatter.format(firstDate)
            }
        }else{
            firstSlot="No availability";
        }

        if (currentItem.imageIds!=null && currentItem.imageIds!!.isNotEmpty()) {
            var imageId = currentItem.imageIds!!.iterator().next()

            var imageUrl = SpriteHealthClient.apiRoot+"/resources/images/" + imageId.toString()
            Picasso.get().load(imageUrl).into(holder.imageView)
        }

        var specialityNames:String?=""
        if(currentItem.specialization!!.isNotEmpty()){

            var speciality=SpriteHealthClient.specialities.find {
                if(it.value!=null  && currentItem.specialization!=null){
                    it.value!! == currentItem.specialization!![0];
                }else{
                    false
                }
            }

            if (speciality != null) {
                specialityNames=speciality.name
                //save it for later use
                currentItem.specializationNames=specialityNames;
            };
        }

        holder.name.text = currentItem.name
        holder.speciality.text = specialityNames
        holder.firstSlot.text = firstSlot

        var builder:GsonBuilder = GsonBuilder()
        var gson:Gson =builder.create()
        var currentItemJSON=gson.toJson(currentItem)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SpecialistDetail::class.java).apply {
                putExtra("id", currentItem.id.toString())
                putExtra("specialistJSON", currentItemJSON)

            }
           context.startActivity(intent)
           // cellClickListener.onCellClickListener()
        }
    }
    override fun getItemCount() = specialistList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view
        val name: TextView = itemView.tvName
        val speciality: TextView = itemView.tvSpeciality
        val firstSlot: TextView = itemView.tvFirstSlot
    }
}