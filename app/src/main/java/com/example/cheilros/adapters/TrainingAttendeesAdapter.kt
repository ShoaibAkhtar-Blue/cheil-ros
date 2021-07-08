package com.example.cheilros.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.TrainingDetailFragment
import com.example.cheilros.fragments.storeview.TrainingNewFragment
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.InvestmentJSONData
import com.example.cheilros.models.TeamMemberData
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_training_detail.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TrainingAttendeesAdapter(
    val context: Context,
    val itemList: MutableList<TeamMemberData>,
    val fragment: TrainingNewFragment,
    val arguments: Bundle?
) :
    RecyclerView.Adapter<TrainingAttendeesAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref
    var investmentsCountData: MutableList<InvestmentJSONData> = mutableListOf()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtNum: TextView = view.findViewById(R.id.txtNum)
        var txtAttendee: TextView = view.findViewById(R.id.txtAttendee)
        var checkboxAttendee: CheckBox = view.findViewById(R.id.checkboxAttendee)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_training_attendees, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtNum.text = (position + 1).toString()
        holder.txtAttendee.text = itemList[position].TeamMemberName

        holder.checkboxAttendee.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (CSP.getData("training_attendees").equals("")) {
                    CSP.saveData("training_attendees", "${itemList[position].TeamMemberID}:${itemList[position].TeamMemberName}")
                } else {
                    CSP.saveData(
                        "training_attendees",
                        "${CSP.getData("training_attendees")},${itemList[position].TeamMemberID}:${itemList[position].TeamMemberName}"
                    )
                }
            }
            println("training_attendees: ${CSP.getData("training_attendees")}")
        }


        /*fragment.btnSubmit.setOnClickListener {

            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())

            val client = OkHttpClient()

            try {
                val builder: MultipartBody.Builder =
                    MultipartBody.Builder().setType(MultipartBody.FORM)

                CSP.getData("training_attendees")?.let { it1 ->
                    builder.addFormDataPart(
                        "Attendees",
                        it1
                    )
                }

                if (!CSP.getData("TrainingDetail_SESSION_IMAGE_SET").equals("")) {
                    val imgPaths = CSP.getData("TrainingDetail_SESSION_IMAGE_SET")?.split(",")
                    if (imgPaths != null) {
                        for (paths in imgPaths) {
                            println(paths)
                            val sourceFile = File(paths)
                            val mimeType =
                                CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                            val fileName: String = sourceFile.name
                            builder.addFormDataPart(
                                "TrainingPictures",
                                fileName,
                                sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                            )
                        }
                    }
                }
                val requestBody = builder.build()
                val request: Request = Request.Builder()
                    .url(
                        "${CSP.getData("base_url")}/Training.asmx/OperTrainingDetail?TrainingModelID=${
                            arguments?.getInt(
                                "TrainingModelID"
                            )
                        }&StoreID=${arguments?.getInt("StoreID")}&Description=${fragment.txtTrainingDescription.text}&TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&TrainingDateTime=${currentDateAndTime}"
                    )
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        (context as Activity).runOnUiThread {
                            context?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage(e.message.toString())
                                    .sneakWarning()
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println(body)

                        (context as Activity).runOnUiThread {
                            context?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("Training Updated!")
                                    .sneakSuccess()
                            }
                            CSP.delData("training_attendees")
                            CSP.delData("TrainingDetail_SESSION_IMAGE")
                            CSP.delData("TrainingDetail_SESSION_IMAGE_SET")

                            Navigation.findNavController(it).navigateUp()
                        }
                    }

                })

            } catch (ex: Exception) {

            }
        }*/
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int = position

    fun addNewItem(itemsNew: TeamMemberData){
        itemList.addAll(listOf(itemsNew))
        notifyDataSetChanged()
    }
}