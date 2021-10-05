package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.data.UserData
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_followup.*
import kotlinx.android.synthetic.main.dialog_followup.btnAccept
import kotlinx.android.synthetic.main.dialog_followup.btnCancel
import kotlinx.android.synthetic.main.dialog_followup.btnTakePictureTask
import kotlinx.android.synthetic.main.dialog_followup.rvTaskPictures
import kotlinx.android.synthetic.main.dialog_followup.txtTitle
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class RecentActivityAdapter(
    val context: Context,
    val fragName: String,
    val itemList: MutableList<RecentActivityData>,
    val arguments: Bundle?,
    val activity: NewDashboardActivity,
    val userData: List<UserData>
) : RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>(), Filterable {

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
        var imgActivity2: ImageView = view.findViewById(R.id.imgActivity2)
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

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        if (CSP.getData("team_type_id")!!.toInt() <= 4) {
            holder.btnAccept.visibility = View.GONE
            holder.btnCancel.visibility = View.GONE
        }

        holder.ActivityTypeName.text = filterList[position].ActivityTypeName
        holder.ActivityDescription.text = "Description: ${filterList[position].ActivityDescription}"
        holder.txtDate.text = filterList[position].ActivityDateTime
        if (filterList[position].ImageActivity != "" && filterList[position].ImageActivity != null)
            Glide.with(context)
                .load("${CSP.getData("base_url")}/${filterList[position].ImageActivity}")
                .into(holder.imgActivity!!)
        else
            holder.imgActivity.visibility = View.GONE

        if (filterList[position].ImageActivity2 != "" && filterList[position].ImageActivity2 != null)
            if (filterList[position].ImageActivity != filterList[position].ImageActivity2)
                Glide.with(context)
                    .load("${CSP.getData("base_url")}/${filterList[position].ImageActivity2}")
                    .into(holder.imgActivity2!!)
            else
                holder.imgActivity2.visibility = View.GONE
        else
            holder.imgActivity2.visibility = View.GONE

        if (CSP.getData("TicketFollowup").equals("N"))
            holder.btnAccept.visibility = View.GONE

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

                    if (capturedPicturesList.size <= 2) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

                        builder.setTitle("Choose...")
                        builder.setMessage("Please select one of the options")

                        builder.setPositiveButton("Camera") { dialog, which ->
                            try {
                                if (fragName == "dashboard") {
                                    CSP.saveData("fragName", "Dashboard_Followup")
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_dashboardFragment_to_cameraActivity)
                                }

                                if (fragName == "task") {
                                    CSP.saveData("fragName", "Task_Followup")
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_taskDeploymentFragment_to_cameraActivity)
                                }

                                if (fragName == "installation") {
                                    CSP.saveData("fragName", "Installation_Followup")
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_installationFragment_to_cameraActivity)
                                }

                                if (fragName == "activity") {
                                    CSP.saveData("fragName", "Activity_Followup")
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_activityFragment_to_cameraActivity)
                                }

                                if (fragName == "activity_sub") {
                                    CSP.saveData("fragName", "Activity_Sub_Followup")
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_activityDetailFragment_to_cameraActivity)
                                }
                            } catch (ex: Exception) {
                                Log.e("Error_", ex.message.toString())
                            }

                        }

                        builder.setNegativeButton("Gallery") { dialog, which ->
                            activity.pickFromGallery()
                        }

                        builder.setNeutralButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                }

                dialog.btnAccept.setOnClickListener {

                    var isFixed = if (dialog.cbIssue.isChecked) "1" else "0"

                    if (dialog.cbIssue.isChecked) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

                        builder.setTitle("Update Issue Log...")
                        builder.setMessage("Are you sure?")

                        builder.setPositiveButton("Yes") { dialog1, which ->
                            saveIssue(dialog, position, isFixed)
                        }

                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }

                        builder.show()
                    } else {
                        saveIssue(dialog, position, isFixed)
                    }
                }

                dialog.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                //region RV Issue Log
                var issueLogURL =
                    "${CSP.getData("base_url")}/MarketActivity.asmx/ActivityFollowup_Log?ActivityID=${itemList[position].ActivityID}"
                println(issueLogURL)
                val request = Request.Builder()
                    .url(issueLogURL)
                    .build()
                val client = OkHttpClient()
                var layoutManager: RecyclerView.LayoutManager
                var recylcerAdapter: IssueLogAdapter

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity.runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage(e.message.toString())
                                    .sneakError()
                            }
                        })
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println(body)
                        val gson = GsonBuilder().create()
                        val apiData = gson.fromJson(body, IssueLogModel::class.java)
                        if (apiData.status == 200) {
                            activity.runOnUiThread(java.lang.Runnable {
                                dialog.rvIssueLog.setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(context)
                                dialog.rvIssueLog.layoutManager = layoutManager
                                recylcerAdapter =
                                    IssueLogAdapter(context, apiData.data, arguments, userData)
                                dialog.rvIssueLog.adapter = recylcerAdapter
                            })
                        } else {
                            activity.runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage("Data not fetched.")
                                        .sneakWarning()
                                }
                            })
                        }
                    }
                })
                //endregion

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

    fun saveIssue(dialog: Dialog, position: Int, isFixed: String) {
        var url =
            "${CSP.getData("base_url")}/MarketActivity.asmx/ActivityFollowup?ActivityID=${itemList[position].ActivityID}&FollowupDescription=${dialog.etRemarks.text.toString()}&TeamMemberID=${
                CSP.getData("user_id")
            }&ActiveStatus=$isFixed"
        val client = OkHttpClient()
        try {
            val builder: MultipartBody.Builder =
                MultipartBody.Builder().setType(MultipartBody.FORM)

            var picNum = 1
            for (paths in capturedPicturesList) {
                println(paths)
                val sourceFile = File(paths)
                val mimeType =
                    CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                val fileName: String = sourceFile.name
                builder.addFormDataPart(
                    "Picture$picNum",
                    fileName,
                    sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                )
                picNum++
            }

            builder.addFormDataPart(
                "test",
                "test"
            )

            val requestBody = builder.build()
            val request: Request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("not completed!")
                                .sneakError()
                        }
                        dialog.dismiss()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Success!!")
                                .setMessage("updated!")
                                .sneakSuccess()
                        }
                        CSP.delData("fragName")
                        CSP.delData("Dashboard_Followup_SESSION_IMAGE")
                        CSP.delData("Dashboard_Followup_SESSION_IMAGE_SET")
                        CSP.delData("Task_Followup_SESSION_IMAGE")
                        CSP.delData("Task_Followup_SESSION_IMAGE_SET")
                        CSP.delData("Installation_Followup_SESSION_IMAGE")
                        CSP.delData("Installation_Followup_SESSION_IMAGE_SET")
                        CSP.delData("Activity_Followup_SESSION_IMAGE")
                        CSP.delData("Activity_Followup_SESSION_IMAGE_SET")
                        CSP.delData("Activity_Sub_Followup_SESSION_IMAGE")
                        CSP.delData("Activity_Sub_Followup_SESSION_IMAGE_SET")

                        dialog.dismiss()
                    }
                }

            })


        } catch (ex: Exception) {

        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    fun addNewItem(imgPath: String) {
        recylcerAdapterPA.addNewItem(imgPath)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<RecentActivityData>
                } else {
                    if (filterList.size > 0) {
                        val resultList = ArrayList<RecentActivityData>()
                        for (row in itemList) {
                            if (row.StoreName.toLowerCase().contains(
                                    constraint.toString().toLowerCase()
                                )
                            ) {
                                resultList.add(row)
                            }
                        }
                        filterList = resultList
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<RecentActivityData>
                notifyDataSetChanged()
            }
        }
    }
}
