package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.fragments.DashboardFragment
import com.example.cheilros.globals.UtilClass
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.DashboardTaskAssignedData
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_add_visit.btnAccept
import kotlinx.android.synthetic.main.dialog_add_visit.btnCancel
import kotlinx.android.synthetic.main.dialog_add_visit.etRemarks
import kotlinx.android.synthetic.main.dialog_add_visit.txtQuestion
import kotlinx.android.synthetic.main.dialog_add_visit.txtTitle
import kotlinx.android.synthetic.main.dialog_assignedtask.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class TaskAssignedAdapter(
    val context: Context,
    val itemList: MutableList<DashboardTaskAssignedData>,
    val fragment: DashboardFragment,
    val activity: NewDashboardActivity
) : RecyclerView.Adapter<TaskAssignedAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLtask: LinearLayout = view.findViewById(R.id.LLtask)
        var txtTaskTitle: TextView = view.findViewById(R.id.txtTaskTitle)
        var txtTaskDesc: TextView = view.findViewById(R.id.txtTaskDesc)
        var txtTime: TextView = view.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_assignedtask, parent, false)
        return ViewHolder(view)
    }

    fun navToCam() {

    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.txtTaskTitle.text = itemList[position].TaskTitle
        holder.txtTaskDesc.text = itemList[position].TaskDescription
        holder.txtTime.text = itemList[position].AssignedDateTime

        holder.LLtask.setOnClickListener {
            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_assignedtask, null)

            val view = it

            val dialog = Dialog(context)
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.txtTitle.text = itemList[position].TaskTitle
            dialog.txtQuestion.text = itemList[position].TaskDescription

            dialog.rvTaskPictures.setHasFixedSize(true)
            layoutManagerPA = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            dialog.rvTaskPictures.layoutManager = layoutManagerPA
            capturedPicturesList.clear()
            recylcerAdapterPA = CapturedPictureAdapter(context, capturedPicturesList)
            dialog.rvTaskPictures.adapter = recylcerAdapterPA


            dialog.btnTakePictureTask.setOnClickListener {

                val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

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
                builder.show()
            }

            dialog.btnCancel.setOnClickListener {


                //val client = OkHttpClient()
                //NIK: 2022-03-22
                val client: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .build()
                try {
                    val builder: MultipartBody.Builder =
                        MultipartBody.Builder().setType(MultipartBody.FORM)

                    for (paths in capturedPicturesList) {
                        println(paths)
                        //val sourceFile = File(paths)
                        //NIK: 2022-03-22
                        val ImageFile = File(paths)
                        val sourceFile = UtilClass.saveBitmapToFile(ImageFile)
                        if (sourceFile!= null) {
                            val mimeType =
                                CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                            val fileName: String = sourceFile.name
                            builder.addFormDataPart(
                                "TaskPicture",
                                fileName,
                                sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                            )
                        }
                    }

                    builder.addFormDataPart(
                        "test",
                        "test"
                    )

                    val requestBody = builder.build()

                    val request: Request = Request.Builder()
                        .url(
                            "${CSP.getData("base_url")}/Tasks.asmx/TaskPicture?TeamMemberID=${
                                CSP.getData(
                                    "user_id"
                                )
                            }&TaskID=${itemList[position].TaskID}&Description=${dialog.etRemarks.text}&Status=2"
                        )
                        .post(requestBody)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage("Task not completed!")
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
                                        .setMessage("Task updated!")
                                        .sneakSuccess()
                                }
                                itemList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, itemCount - position)

                                CSP.delData("fragName")
                                CSP.delData("Dashboard_SESSION_IMAGE")
                                CSP.delData("Dashboard_SESSION_IMAGE_SET")
                                dialog.dismiss()
                            }
                        }

                    })

                } catch (ex: Exception) {

                }
            }

            dialog.btnAccept.setOnClickListener {
                //val client = OkHttpClient()
                //NIK: 2022-03-22
                val client: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .build()
                try {
                    val builder: MultipartBody.Builder =
                        MultipartBody.Builder().setType(MultipartBody.FORM)

                    for (paths in capturedPicturesList) {
                        println(paths)
                        //val sourceFile = File(paths)
                        //NIK: 2022-03-22
                        val ImageFile = File(paths)
                        val sourceFile = UtilClass.saveBitmapToFile(ImageFile)
                        if (sourceFile!= null) {
                            val mimeType =
                                CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                            val fileName: String = sourceFile.name
                            builder.addFormDataPart(
                                "TaskPicture",
                                fileName,
                                sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                            )
                        }
                    }

                    builder.addFormDataPart(
                        "test",
                        "test"
                    )

                    val requestBody = builder.build()

                    val request: Request = Request.Builder()
                        .url(
                            "${CSP.getData("base_url")}/Tasks.asmx/TaskPicture?TeamMemberID=${
                                CSP.getData(
                                    "user_id"
                                )
                            }&TaskID=${itemList[position].TaskID}&Description=${dialog.etRemarks.text}&Status=3"
                        )
                        .post(requestBody)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage("Task not completed!")
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
                                        .setMessage("Task updated!")
                                        .sneakSuccess()
                                }
                                itemList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, itemCount - position)

                                CSP.delData("fragName")
                                CSP.delData("Dashboard_SESSION_IMAGE")
                                CSP.delData("Dashboard_SESSION_IMAGE_SET")

                                dialog.dismiss()
                            }
                        }

                    })

                } catch (ex: Exception) {

                }
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun addNewItem(imgPath: String) {
        recylcerAdapterPA.addNewItem(imgPath)
    }

}