package com.example.cheilros.adapters

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.ChannelModel
import com.example.cheilros.models.GenericModel
import com.example.cheilros.models.RecentActivityData
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_followup.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.item_jpstatus.view.*
import okhttp3.*
import java.io.IOException

class RecentActivityAdapter(
    val context: Context,
    val itemList: MutableList<RecentActivityData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()

    var filterList = ArrayList<RecentActivityData>()

    init {
        filterList = itemList as ArrayList<RecentActivityData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLrecentactivity: LinearLayout = view.findViewById(R.id.LLrecentactivity)

        //var RLrecentactivity: RelativeLayout = view.findViewById(R.id.RLrecentactivity)
        var ActivityTypeName: TextView = view.findViewById(R.id.ActivityTypeName)
        var ActivityDescription: TextView = view.findViewById(R.id.ActivityDescription)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
        var imgActivity: ImageView = view.findViewById(R.id.imgActivity)
        var btnCancel: Button = view.findViewById(R.id.btnCancel)
        var btnAccept: Button = view.findViewById(R.id.btnAccept)
        //var RLimgActivity: RelativeLayout = view.findViewById(R.id.RLimgActivity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_recent_activities_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ActivityTypeName.text = filterList[position].ActivityTypeName
        holder.ActivityDescription.text = "Description: ${filterList[position].ActivityDescription}"
        holder.txtDate.text = filterList[position].ActivityDateTime
        if (filterList[position].ImageActivity != "" && filterList[position].ImageActivity != null)
            Glide.with(context)
                .load("${CSP.getData("base_url")}/${filterList[position].ImageActivity}")
                .into(holder.imgActivity!!)
        else
            holder.imgActivity.visibility = View.GONE

        if (filterList[position].ActivityTypeID == 20) {
            holder.btnCancel.visibility = View.GONE
            holder.btnAccept.text = "Follow-Up"
        } else {
            holder.btnAccept.text = "Edit"
            holder.btnCancel.text = "Delete"
        }

        holder.btnAccept.setOnClickListener {
            val view = it
            if (filterList[position].ActivityTypeID == 20) {
                val li = LayoutInflater.from(context)
                val promptsView: View = li.inflate(R.layout.dialog_followup, null)

                val dialog = Dialog(context)
                dialog.setContentView(promptsView)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(true)

                dialog.txtTitle.text = "Ticket # ${itemList[position].StoreCode}"

                dialog.rvTaskPictures.setHasFixedSize(true)
                layoutManagerPA = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                dialog.rvTaskPictures.layoutManager = layoutManagerPA
                capturedPicturesList.clear()
                recylcerAdapterPA = CapturedPictureAdapter(context, capturedPicturesList)
                dialog.rvTaskPictures.adapter = recylcerAdapterPA

                dialog.btnTakePictureTask.setOnClickListener {

                    /*val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

                    builder.setTitle("Choose...")
                    builder.setMessage("Please select one of the options")

                    builder.setPositiveButton("Camera") { dialog, which ->
                        CSP.saveData("fragName", "Dashboard")
                        Navigation.findNavController(view)
                            .navigate(R.id.action_dashboardFragment_to_cameraActivity)
                    }

                    builder.setNegativeButton("Gallery") { dialog, which ->
                        activity.pickFromGallery()
                    }

                    builder.setNeutralButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()*/
                }

                dialog.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            } else {
                val li = LayoutInflater.from(context)
                val promptsView: View = li.inflate(R.layout.dialog_flowup_else, null)

                val dialog = Dialog(context)
                dialog.setContentView(promptsView)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(true)

                dialog.txtTitle.text = "${itemList[position].StoreName}"

                dialog.rvTaskPictures.setHasFixedSize(true)
                layoutManagerPA = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                dialog.rvTaskPictures.layoutManager = layoutManagerPA
                capturedPicturesList.clear()
                recylcerAdapterPA = CapturedPictureAdapter(context, capturedPicturesList)
                dialog.rvTaskPictures.adapter = recylcerAdapterPA

                dialog.btnTakePictureTask.setOnClickListener {
                    /*val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

                    builder.setTitle("Choose...")
                    builder.setMessage("Please select one of the options")

                    builder.setPositiveButton("Camera") { dialog, which ->
                        CSP.saveData("fragName", "Dashboard")
                        Navigation.findNavController(view)
                            .navigate(R.id.action_dashboardFragment_to_cameraActivity)
                    }

                    builder.setNegativeButton("Gallery") { dialog, which ->
                        activity.pickFromGallery()
                    }

                    builder.setNeutralButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()*/
                }



                dialog.show()
            }
        }

        holder.btnCancel.setOnClickListener {
            if (filterList[position].ActivityTypeID != 20) {
                println("${CSP.getData("base_url")}/Activity.asmx/RemoveActivity?ActivityID=${itemList[position].ActivityID}")
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url("${CSP.getData("base_url")}/Activity.asmx/RemoveActivity?ActivityID=${itemList[position].ActivityID}")
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        (context as Activity).runOnUiThread {
                            context?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Request not completed!")
                                    .sneakWarning()
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println(body)

                        val gson = GsonBuilder().create()
                        val apiData = gson.fromJson(body, GenericModel::class.java)
                        if (apiData.status == 200) {
                            removeItem(position)
                        } else {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage("Request not completed!")
                                        .sneakWarning()
                                }
                            }
                        }
                    }

                })
            }
        }

        /*if(filterList[position].ActivityTypeID == 20)
            holder.RLrecentactivity.setBackgroundColor(Color.RED)
        else if(filterList[position].ActivityTypeID > 20)
            holder.RLrecentactivity.setBackgroundColor(Color.parseColor("#ffa500"))*/

        holder.LLrecentactivity.setOnClickListener {
            /*val bundle = bundleOf(
                "DivisionID" to arguments?.getInt("DivisionID"),
                "TrainingModelID" to itemList[position].TrainingModelID,
                "TrainingModelTitle" to itemList[position].TrainingModelTitle,
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )
            Navigation.findNavController(it)
                .navigate(R.id.action_trainingFragment_to_trainingDetailFragment, bundle)*/
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun removeItem(position: Int){
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }
}
