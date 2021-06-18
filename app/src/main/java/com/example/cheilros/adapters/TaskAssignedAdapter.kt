package com.example.cheilros.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.DashboardTaskAssignedData
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.dialog_add_visit.btnAccept
import kotlinx.android.synthetic.main.dialog_add_visit.btnCancel
import kotlinx.android.synthetic.main.dialog_add_visit.etRemarks
import kotlinx.android.synthetic.main.dialog_add_visit.txtQuestion
import kotlinx.android.synthetic.main.dialog_add_visit.txtTitle
import kotlinx.android.synthetic.main.dialog_assignedtask.*
import kotlinx.android.synthetic.main.fragment_training_detail.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class TaskAssignedAdapter(
    val context: Context,
    val itemList: List<DashboardTaskAssignedData>
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

    fun navToCam(){

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
            layoutManagerPA = LinearLayoutManager(context,RecyclerView.HORIZONTAL, false)
            dialog.rvTaskPictures.layoutManager = layoutManagerPA
            capturedPicturesList.clear()
            recylcerAdapterPA = CapturedPictureAdapter(context, capturedPicturesList)
            dialog.rvTaskPictures.adapter = recylcerAdapterPA


            dialog.btnTakePictureTask.setOnClickListener {
                CSP.saveData("fragName", "Dashboard")
                Navigation.findNavController(view)
                    .navigate(R.id.action_dashboardFragment_to_cameraActivity)
            }

            dialog.btnCancel.setOnClickListener {
                val client = OkHttpClient()
                try {
                    val builder: MultipartBody.Builder =
                        MultipartBody.Builder().setType(MultipartBody.FORM)


                    if (!CSP.getData("Dashboard_SESSION_IMAGE_SET").equals("")) {
                        val imgPaths = CSP.getData("Dashboard_SESSION_IMAGE_SET")?.split(",")
                        if (imgPaths != null) {
                            for (paths in imgPaths) {
                                println(paths)
                                val sourceFile = File(paths)
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
                val client = OkHttpClient()
                try {
                    val builder: MultipartBody.Builder =
                        MultipartBody.Builder().setType(MultipartBody.FORM)

                    if (!CSP.getData("Dashboard_SESSION_IMAGE_SET").equals("")) {
                        val imgPaths = CSP.getData("Dashboard_SESSION_IMAGE_SET")?.split(",")
                        if (imgPaths != null) {
                            for (paths in imgPaths) {
                                println(paths)
                                val sourceFile = File(paths)
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

    fun addNewItem(imgPath: String){
        recylcerAdapterPA.addNewItem(imgPath)
    }

}