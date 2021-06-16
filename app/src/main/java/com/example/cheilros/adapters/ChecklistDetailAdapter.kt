package com.example.cheilros.adapters

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.ChecklistCategoryDetailFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckListDetailData
import com.example.cheilros.models.CheckListJSON
import com.example.cheilros.models.CheckListJSONData
import com.example.cheilros.models.InvestmentJSON
import com.google.gson.Gson
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.dialog_add_visit.btnAccept
import kotlinx.android.synthetic.main.dialog_add_visit.btnCancel
import kotlinx.android.synthetic.main.dialog_add_visit.txtQuestion
import kotlinx.android.synthetic.main.dialog_add_visit.txtTitle
import kotlinx.android.synthetic.main.dialog_checklist.*
import kotlinx.android.synthetic.main.dialog_checklist.btnDate
import kotlinx.android.synthetic.main.fragment_checklist_category_detail.*
import kotlinx.android.synthetic.main.fragment_checklist_category_detail.btnSubmit
import kotlinx.android.synthetic.main.fragment_checklist_category_detail.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_investment_detail.*
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*


class ChecklistDetailAdapter(
    val context: Context,
    val itemList: MutableList<CheckListDetailData>,
    val arguments: Bundle?,
    val fragment: ChecklistCategoryDetailFragment
) : RecyclerView.Adapter<ChecklistDetailAdapter.ViewHolder>() {

    var IdStart = 1000

    lateinit var CSP: CustomSharedPref

    var checklistAnswer: MutableList<CheckListJSONData> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var RLcolor: RelativeLayout = view.findViewById(R.id.RLcolor)
        var LLchecklist: LinearLayout = view.findViewById(R.id.LLchecklist)
        var LLAnswer: LinearLayout = view.findViewById(R.id.LLAnswer)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_checklistdetail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtTitle.text = itemList[position].Question

        if(itemList[position].CheckListStatus != ""){
            if(checklistAnswer.indexOf(checklistAnswer.find { it.CheckListID ==  itemList[position].ChecklistID}) != -1){
                holder.txtAnswer.text = "Answer: ${itemList[position].CheckListStatus}"
                holder.RLcolor.setBackgroundColor(Color.RED)
                holder.txtAnswer.visibility = View.VISIBLE
            }
        }


        holder.LLchecklist.setOnClickListener {
            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_checklist, null)

            val dialog = Dialog(context)
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.txtTitle.text = "Question"
            dialog.txtQuestion.text = itemList[position].Question

            //region Generate Answer Section
            if (itemList[position].InputTypeID == 1) {
                dialog.checkBox.visibility = View.VISIBLE
            }

            if (itemList[position].InputTypeID == 2) {
                dialog.OTnumber_edit_text.visibility = View.VISIBLE
                /*dialog.number_edit_text.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            println(s)
                            try {
                                if (checklistAnswer.isNullOrEmpty()) {
                                    checklistAnswer.add(
                                        CheckListJSONData(
                                            itemList[position].ChecklistID,
                                            arguments?.getInt("StoreID"),
                                            s.toString(),
                                            CSP.getData("user_id")?.toInt()
                                        )
                                    )
                                } else {
                                    val checklistSize =
                                        checklistAnswer.filter { it.CheckListID == itemList[position].ChecklistID }.size
                                    if (checklistSize == 0) {
                                        checklistAnswer.add(
                                            CheckListJSONData(
                                                itemList[position].ChecklistID,
                                                arguments?.getInt("StoreID"),
                                                s.toString(),
                                                CSP.getData("user_id")?.toInt()
                                            )
                                        )
                                    } else {
                                        val checklistIndex =
                                            checklistAnswer.indexOf(checklistAnswer.find { it.CheckListID == itemList[position].ChecklistID })

                                        checklistAnswer.add(
                                            checklistIndex,
                                            CheckListJSONData(
                                                itemList[position].ChecklistID,
                                                arguments?.getInt("StoreID"),
                                                s.toString(),
                                                CSP.getData("user_id")?.toInt()
                                            )
                                        )
                                    }
                                }
                            } catch (ex: Exception) {
                                Log.e("Error_", ex.message.toString())
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {
                        }
                    })*/
            }

            if (itemList[position].InputTypeID == 3) {
                dialog.OTalpha_edit_text.visibility = View.VISIBLE
                /* dialog.alpha_edit_text.addTextChangedListener(object : TextWatcher {
                     override fun beforeTextChanged(
                         s: CharSequence?,
                         start: Int,
                         count: Int,
                         after: Int
                     ) {

                     }

                     override fun onTextChanged(
                         s: CharSequence?,
                         start: Int,
                         before: Int,
                         count: Int
                     ) {
                         println(s)
                         try {
                             if (checklistAnswer.isNullOrEmpty()) {
                                 checklistAnswer.add(
                                     CheckListJSONData(
                                         itemList[position].ChecklistID,
                                         arguments?.getInt("StoreID"),
                                         s.toString(),
                                         CSP.getData("user_id")?.toInt()
                                     )
                                 )
                             } else {
                                 val checklistSize =
                                     checklistAnswer.filter { it.CheckListID == itemList[position].ChecklistID }.size
                                 if (checklistSize == 0) {
                                     checklistAnswer.add(
                                         CheckListJSONData(
                                             itemList[position].ChecklistID,
                                             arguments?.getInt("StoreID"),
                                             s.toString(),
                                             CSP.getData("user_id")?.toInt()
                                         )
                                     )
                                 } else {
                                     val checklistIndex =
                                         checklistAnswer.indexOf(checklistAnswer.find { it.CheckListID == itemList[position].ChecklistID })

                                     checklistAnswer.add(
                                         checklistIndex,
                                         CheckListJSONData(
                                             itemList[position].ChecklistID,
                                             arguments?.getInt("StoreID"),
                                             s.toString(),
                                             CSP.getData("user_id")?.toInt()
                                         )
                                     )
                                 }
                             }
                         } catch (ex: Exception) {
                             Log.e("Error_", ex.message.toString())
                         }
                     }

                     override fun afterTextChanged(s: Editable?) {
                     }
                 })*/
            }

            if (itemList[position].InputTypeID == 4) {
                dialog.btnDate.visibility = View.VISIBLE

                dialog.btnDate.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog =
                        DatePickerDialog(
                            context, DatePickerDialog.OnDateSetListener
                            { view, year, monthOfYear, dayOfMonth ->
                                val currentDate: String = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                                dialog.btnDate.text = currentDate
                            }, year, month, day
                        )
                    datePickerDialog.show()
                }
            }
            //endregion

            dialog.btnCancel.text = "Cancel"
            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.btnAccept.setOnClickListener {
                var answer = ""
                if (itemList[position].InputTypeID == 2) {
                    answer = dialog.number_edit_text.text.toString()
                } else if (itemList[position].InputTypeID == 3) {
                    answer = dialog.alpha_edit_text.text.toString()
                } else if (itemList[position].InputTypeID == 4) {
                    answer = dialog.btnDate.text.toString()
                } else if (itemList[position].InputTypeID == 1) {
                    answer = if (dialog.checkBox.isChecked) "True" else "False"
                }

                updateItem(
                    itemList[position].ChecklistID,
                    CheckListDetailData(
                        itemList[position].ChecklistID,
                        itemList[position].Question,
                        itemList[position].InputTypeID,
                        answer
                    )
                )

                try {
                    if (checklistAnswer.isNullOrEmpty()) {
                        checklistAnswer.add(
                            CheckListJSONData(
                                itemList[position].ChecklistID,
                                arguments?.getInt("StoreID"),
                                answer,
                                CSP.getData("user_id")?.toInt()
                            )
                        )
                    } else {
                        val checklistSize =
                            checklistAnswer.filter { it.CheckListID == itemList[position].ChecklistID }.size
                        if (checklistSize == 0) {
                            checklistAnswer.add(
                                CheckListJSONData(
                                    itemList[position].ChecklistID,
                                    arguments?.getInt("StoreID"),
                                    answer,
                                    CSP.getData("user_id")?.toInt()
                                )
                            )
                        } else {
                            val checklistIndex =
                                checklistAnswer.indexOf(checklistAnswer.find { it.CheckListID == itemList[position].ChecklistID })

                            checklistAnswer.add(
                                checklistIndex,
                                CheckListJSONData(
                                    itemList[position].ChecklistID,
                                    arguments?.getInt("StoreID"),
                                    answer,
                                    CSP.getData("user_id")?.toInt()
                                )
                            )
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("Error_", ex.message.toString())
                }

                dialog.dismiss()
            }

            dialog.show()

        }

        fragment.btnSubmit.setOnClickListener {
            /*println("LLchecklist")
            println(holder.LLAnswer.childCount)
            var ed: EditText = holder.LLAnswer.getChildAt(0).findViewById(R.id.number_edit_text)
            println(ed.text)*/
            /*var v = holder.LLAnswer.getChildAt(1)
            if (v is EditText) {
                println(v.text)
            }*/

            fragment.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

            val gson = Gson()
            val jsonString: String = gson.toJson(CheckListJSON(checklistAnswer))
            println(jsonString)

            val url = "${CSP.getData("base_url")}/Audit.asmx/CheckList_AuditAdd"

            val request_header: MediaType? = "application/text; charset=utf-8".toMediaTypeOrNull()

            var body: RequestBody = jsonString.toRequestBody(request_header)
            val request = Request.Builder().post(body).url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val tm = response.body?.string()
                    println(tm)
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Success!!")
                                .setMessage("Data saved!")
                                .sneakSuccess()
                        }
                        Navigation.findNavController(it).navigateUp()
                        fragment.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Failed", "FAILED")
                    //e.printStackTrace()
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not saved!")
                                .sneakError()
                        }
                        fragment.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    }
                }
            })

        }

        /* if (holder.LLAnswer!!.childCount == 0) {


             if (itemList[position].InputTypeID == 1) {
                 val rowView: View = inflater.inflate(R.layout.checkbox, null)
                 holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)
             }
             if (itemList[position].InputTypeID == 2) {
                 val rowView: View = inflater.inflate(R.layout.field, null)
                 holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)


                *//* val et = EditText(context)
                et.hint = "Type your Answer here..."
                et.id = IdStart * (0..200).random()
                et.width = LinearLayout.LayoutParams.MATCH_PARENT
                et.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                        //qa.saveAnswers(qid, et.text.toString())
                    }

                    override fun afterTextChanged(editable: Editable) {}
                })

                holder.LLAnswer!!.addView(et, holder.LLAnswer!!.childCount - 1)*//*

            }

            if (itemList[position].InputTypeID == 3) {
                val rowView: View = inflater.inflate(R.layout.field_text, null)
                holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)
            }

            if (itemList[position].InputTypeID == 4) {
                val rowView: View = inflater.inflate(R.layout.datebutton, null)
                holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)
            }

            if (itemList[position].InputTypeID == 2 || itemList[position].InputTypeID == 3) {

                var ed: EditText = if (itemList[position].InputTypeID == 2)
                    holder.LLAnswer.getChildAt(0).findViewById(R.id.number_edit_text)
                else
                    holder.LLAnswer.getChildAt(0).findViewById(R.id.alpha_edit_text)

                ed.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        println(s)
                        try {
                            if (checklistAnswer.isNullOrEmpty()) {
                                checklistAnswer.add(
                                    CheckListJSONData(
                                        itemList[position].ChecklistID,
                                        arguments?.getInt("StoreID"),
                                        s.toString(),
                                        CSP.getData("user_id")?.toInt()
                                    )
                                )
                            } else {
                                val checklistSize =
                                    checklistAnswer.filter { it.CheckListID == itemList[position].ChecklistID }.size
                                if (checklistSize == 0) {
                                    checklistAnswer.add(
                                        CheckListJSONData(
                                            itemList[position].ChecklistID,
                                            arguments?.getInt("StoreID"),
                                            s.toString(),
                                            CSP.getData("user_id")?.toInt()
                                        )
                                    )
                                } else {
                                    val checklistIndex =
                                        checklistAnswer.indexOf(checklistAnswer.find { it.CheckListID == itemList[position].ChecklistID })

                                    checklistAnswer.add(
                                        checklistIndex,
                                        CheckListJSONData(
                                            itemList[position].ChecklistID,
                                            arguments?.getInt("StoreID"),
                                            s.toString(),
                                            CSP.getData("user_id")?.toInt()
                                        )
                                    )
                                }
                            }
                        } catch (ex: Exception) {
                            Log.e("Error_", ex.message.toString())
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })
            }

        }*/


    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateItem(qid: Int, answer: CheckListDetailData) {
        val index = itemList.indexOf(itemList.find { it.ChecklistID == qid })
        itemList[index] = answer
        notifyDataSetChanged()
    }

    fun generateLayout(type: Int) {

    }
}