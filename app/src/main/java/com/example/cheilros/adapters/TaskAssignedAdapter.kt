package com.example.cheilros.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.DashboardTaskAssignedData
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_add_visit.*
import okhttp3.*
import java.io.IOException

class TaskAssignedAdapter(
    val context: Context,
    val itemList: List<DashboardTaskAssignedData>
) : RecyclerView.Adapter<TaskAssignedAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTaskTitle.text = itemList[position].TaskTitle
        holder.txtTaskDesc.text = itemList[position].TaskDescription
        holder.txtTime.text = itemList[position].AssignedDateTime

        holder.LLtask.setOnClickListener {
            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_assignedtask, null)

            val dialog = Dialog(context)
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.txtTitle.text = itemList[position].TaskTitle
            dialog.txtQuestion.text = itemList[position].TaskDescription

            dialog.btnCancel.setOnClickListener {
                val client = OkHttpClient()
                try {
                    val requestBody: RequestBody =
                        MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("test", "test").build()

                    val request: Request = Request.Builder()
                        .url("${CSP.getData("base_url")}/Tasks.asmx/TaskPicture?TeamMemberID=${CSP.getData("user_id")}&TaskID=${itemList[position].TaskID}&Description=${dialog.etRemarks.text}&Status=2")
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
                                dialog.dismiss()
                            }
                        }

                    })

                }catch (ex: Exception){

                }
            }

            dialog.btnAccept.setOnClickListener {
                val client = OkHttpClient()
                try {
                    val requestBody: RequestBody =
                        MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("test", "test").build()

                    val request: Request = Request.Builder()
                        .url("${CSP.getData("base_url")}/Tasks.asmx/TaskPicture?TeamMemberID=${CSP.getData("user_id")}&TaskID=${itemList[position].TaskID}&Description=${dialog.etRemarks.text}&Status=3")
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
                                dialog.dismiss()
                            }
                        }

                    })

                }catch (ex: Exception){

                }
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}