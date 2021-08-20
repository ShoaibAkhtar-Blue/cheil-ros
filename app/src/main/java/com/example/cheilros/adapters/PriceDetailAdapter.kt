package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
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
import com.example.cheilros.data.AppSetting
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.PriceDetailData
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_price_detail.*
import kotlinx.android.synthetic.main.dialog_price_detail.btnAccept
import kotlinx.android.synthetic.main.dialog_price_detail.btnCancel
import kotlinx.android.synthetic.main.dialog_price_detail.txtTitle
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


class PriceDetailAdapter(
    val context: Context,
    val itemList: MutableList<PriceDetailData>,
    val StoreID: Int?,
    val activity: NewDashboardActivity,
    val settingData: List<AppSetting>
) : RecyclerView.Adapter<PriceDetailAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //var RLHeader: RelativeLayout = view.findViewById(R.id.RLHeader)
        var LLPriceDetail: LinearLayout = view.findViewById(R.id.LLPriceDetail)
        var txtCount: TextView = view.findViewById(R.id.txtCount)
        var txtTitleHeader: TextView = view.findViewById(R.id.txtTitleHeader)
        var txtNetPrice: TextView = view.findViewById(R.id.txtNetPrice)
        var txtPrice: TextView = view.findViewById(R.id.txtPrice)
        var txtInstallment: TextView = view.findViewById(R.id.txtInstallment)
        //var txtPromotion: TextView = view.findViewById(R.id.txtPromotion)
        var txtUsername: TextView = view.findViewById(R.id.txtUsername)
        var etNetPrice: EditText = view.findViewById(R.id.etNetPrice)
        var etPrice: EditText = view.findViewById(R.id.etPrice)
        var etPromotion: EditText = view.findViewById(R.id.etPromotion)
        var imgTag: ImageView = view.findViewById(R.id.imgTag)
        var et3Month: EditText = view.findViewById(R.id.et3Month)
        var et6Month: EditText = view.findViewById(R.id.et6Month)
        var et12Month: EditText = view.findViewById(R.id.et12Month)
        var btnAccept: Button = view.findViewById(R.id.btnAccept)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_price_detail_new, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "WrongConstant")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        //region set Labels
        try {
            holder.txtNetPrice.text = settingData.filter { it.fixedLabelName == "PricePromotion_RRP" }[0].labelName
            holder.txtPrice.text = settingData.filter { it.fixedLabelName == "PricePromotion_NetPrice" }[0].labelName
            holder.txtInstallment.text = settingData.filter { it.fixedLabelName == "PricePromotion_Promotion" }[0].labelName
            holder.etPromotion.hint = settingData.filter { it.fixedLabelName == "PricePromotion_Installment" }[0].labelName
            holder.et3Month.hint = settingData.filter { it.fixedLabelName == "PricePromotion_3Month" }[0].labelName
            holder.et6Month.hint = settingData.filter { it.fixedLabelName == "PricePromotion_6Month" }[0].labelName
            holder.et12Month.hint = settingData.filter { it.fixedLabelName == "PricePromotion_12Month" }[0].labelName
        }catch (ex: Exception){
            Log.e("Error_", ex.message.toString())
        }
        //endregion


        holder.txtCount.text = (position+1).toString()
        holder.txtTitleHeader.text = itemList[position].ShortName //Todo: Add Category name too
        holder.txtUsername.text = itemList[position].Username
        holder.etNetPrice.setText(itemList[position].NetPrice)
        holder.etPrice.setText(itemList[position].Price)
        holder.etPromotion.setText(itemList[position].Promotion)
        holder.et3Month.setText(itemList[position].Installment_3Month)
        holder.et6Month.setText(itemList[position].Installment_6Month)
        holder.et12Month.setText(itemList[position].Installment_12Month)
        println("${CSP.getData("base_url")}/PricesPictures/${itemList[position].PiceTagPictureID}")

        println("PiceTagPictureID: ${itemList[position].PiceTagPictureID}")
        if(itemList[position].PiceTagPictureID != "0" && !itemList[position].PiceTagPictureID.contains("/storage") )
        Glide.with(context)
            .load("${CSP.getData("base_url")}/PricesPictures/${itemList[position].PiceTagPictureID}")
            .into(holder.imgTag!!)
        else
            Glide.with(context)
                .load("${itemList[position].PiceTagPictureID}")
                .into(holder.imgTag!!)


        if(CSP.getData("productid") != ""){
            if(CSP.getData("productid") == itemList[position].ProductID.toString()){
                if(!CSP.getData("sess_gallery_img").equals(""))
                    Glide.with(context)
                        .load(CSP.getData("sess_gallery_img"))
                        .into(holder.imgTag!!)
                else
                    Glide.with(context)
                        .load(CSP.getData("PriceDetail_SESSION_IMAGE_SET"))
                        .into(holder.imgTag!!)

                //CSP.delData("PriceDetail_SESSION_IMAGE")
                //CSP.delData("sess_gallery_img")
            }
        }



        holder.imgTag.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

            builder.setTitle("Choose...")
            builder.setMessage("Please select one of the options")

            builder.setPositiveButton("Camera") { dialog, which ->
                CSP.saveData("fragName", "PriceDetail")
                CSP.saveData("productid", itemList[position].ProductID.toString())
                Navigation.findNavController(it)
                    .navigate(R.id.action_priceDetailFragment_to_cameraActivity)
            }

            builder.setNegativeButton("Gallery") { dialog, which ->
                activity.pickFromGallery()
            }

            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        holder.btnAccept.setOnClickListener {
            val client = OkHttpClient()
            try {
                val builder: MultipartBody.Builder =
                    MultipartBody.Builder().setType(MultipartBody.FORM)

                var ImgPath = ""
                if (!CSP.getData("sess_gallery_img").equals(""))
                    ImgPath = CSP.getData("sess_gallery_img").toString()
                else
                    ImgPath = CSP.getData("PriceDetail_SESSION_IMAGE").toString()


                if(ImgPath != ""){
                    val sourceFile = File(ImgPath)
                    val mimeType =
                        CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                    val fileName: String = sourceFile.name
                    builder.addFormDataPart(
                        "PricesImg",
                        fileName,
                        sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                    )
                }


                /*for (paths in capturedPicturesList) {
                    println(paths)
                    val sourceFile = File(paths)
                    val mimeType =
                        CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                    val fileName: String = sourceFile.name
                    builder.addFormDataPart(
                        "PricesImg",
                        fileName,
                        sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                    )
                }*/

                builder.addFormDataPart(
                    "test",
                    "test"
                )

                val requestBody = builder.build()

                println("${CSP.getData("base_url")}/Prices.asmx/PricePromotionAdd?ProductID=${itemList[position].ProductID}&StoreID=${StoreID}&Price=${holder.etPrice.text}&Promotion=${holder.etPromotion.text}&TeamMemberID=${
                    CSP.getData(
                        "user_id"
                    ).toString()
                }&NetPrice=${holder.etNetPrice.text}&Installment_3Month=${holder.et3Month.text}&Installment_6Month=${holder.et6Month.text}&Installment_12Month=${holder.et12Month.text}")

                val request: Request = Request.Builder()
                    .url(
                        "${CSP.getData("base_url")}/Prices.asmx/PricePromotionAdd?ProductID=${itemList[position].ProductID}&StoreID=${StoreID}&Price=${holder.etPrice.text}&Promotion=${holder.etPromotion.text}&TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            ).toString()
                        }&NetPrice=${holder.etNetPrice.text}&Installment_3Month=${holder.et3Month.text}&Installment_6Month=${holder.et6Month.text}&Installment_12Month=${holder.et12Month.text}"
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
                            ImgPath = if(ImgPath != "") ImgPath else itemList[position].PiceTagPictureID
                                updateItem(
                                    itemList[position].ProductID,
                                    PriceDetailData(
                                        itemList[position].ProductID,
                                        itemList[position].ShortName,
                                        holder.etNetPrice.text.toString(),
                                        holder.etPrice.text.toString(),
                                        holder.etPromotion.text.toString(),
                                        holder.et3Month.text.toString(),
                                        holder.et6Month.text.toString(),
                                        holder.et12Month.text.toString(),
                                        itemList[position].Username,
                                        ImgPath
                                    )
                                )

                            //Reset Session
                            CSP.delData("fragName")
                            CSP.delData("productid")
                            CSP.delData("PriceDetail_SESSION_IMAGE")
                            CSP.delData("PriceDetail_SESSION_IMAGE_SET")
                            CSP.delData("sess_gallery_img")

                        }
                    }

                })

            } catch (ex: Exception) {

            }
        }



        holder.LLPriceDetail.setOnClickListener {
            /*val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_price_detail, null)

            val view = it

            val dialog = Dialog(context)
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.txtTitle.text = itemList[position].ShortName

            dialog.rvTaskPictures.setHasFixedSize(true)
            layoutManagerPA = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            dialog.rvTaskPictures.layoutManager = layoutManagerPA
            capturedPicturesList.clear()
            recylcerAdapterPA = CapturedPictureAdapter(context, capturedPicturesList)
            dialog.rvTaskPictures.adapter = recylcerAdapterPA

            dialog.etNetPrice.setText(itemList[position].NetPrice)
            dialog.etPrice.setText(itemList[position].Price)
            dialog.etPromotion.setText(itemList[position].Promotion)

            dialog.btnTakePictureTask.setOnClickListener {

                if(capturedPicturesList.size == 0){
                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

                    builder.setTitle("Choose...")
                    builder.setMessage("Please select one of the options")

                    builder.setPositiveButton("Camera") { dialog, which ->
                        CSP.saveData("fragName", "PriceDetail")
                        Navigation.findNavController(view)
                            .navigate(R.id.action_priceDetailFragment_to_cameraActivity)
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

            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.btnAccept.setOnClickListener {
                val client = OkHttpClient()
                try {
                    val builder: MultipartBody.Builder =
                        MultipartBody.Builder().setType(MultipartBody.FORM)

                    for (paths in capturedPicturesList) {
                        println(paths)
                        val sourceFile = File(paths)
                        val mimeType =
                            CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                        val fileName: String = sourceFile.name
                        builder.addFormDataPart(
                            "PricesImg",
                            fileName,
                            sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                        )
                    }

                    builder.addFormDataPart(
                        "test",
                        "test"
                    )

                    val requestBody = builder.build()

                    val request: Request = Request.Builder()
                        .url(
                            "${CSP.getData("base_url")}/Prices.asmx/PricePromotionAdd?ProductID=${itemList[position].ProductID}&StoreID=${StoreID}&Price=${dialog.etPrice.text}&Promotion=${dialog.etPromotion.text}&TeamMemberID=${
                                CSP.getData(
                                    "user_id"
                                ).toString()
                            }&NetPrice=${dialog.etNetPrice.text}"
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

//                                updateItem(
//                                    itemList[position].ProductID,
//                                    PriceDetailData(
//                                        itemList[position].ProductID,
//                                        itemList[position].ShortName,
//                                        dialog.etNetPrice.text.toString(),
//                                        dialog.etPrice.text.toString(),
//                                        dialog.etPromotion.text.toString(),
//                                        CSP.getData("PriceDetail_SESSION_IMAGE").toString()
//                                    )
//                                )

                                CSP.delData("fragName")
                                CSP.delData("PriceDetail_SESSION_IMAGE")
                                CSP.delData("PriceDetail_SESSION_IMAGE_SET")

                                dialog.dismiss()
                            }
                        }

                    })

                } catch (ex: Exception) {

                }
            }

            dialog.show()*/
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int = position

    fun addNewItem(pid:String,imgPath: String) {

        println("addNewItem")
        println(imgPath)
        println(pid)
        val index = itemList.indexOf(itemList.find { it.ProductID == pid.toInt() })
        println(index)
        //itemList[index].PiceTagPictureID = imgPath
        val updateItem = itemList[index].apply { PiceTagPictureID = imgPath }
        itemList[index] = updateItem
        notifyItemChanged(index)
        notifyDataSetChanged()
        (context as Activity).runOnUiThread {
            context?.let { it1 ->

            }
        }

        //recylcerAdapterPA.addNewItem(imgPath)
    }

    fun updateItem(pid: Int, answer: PriceDetailData) {
        val index = itemList.indexOf(itemList.find { it.ProductID == pid })
        itemList[index] = answer
        notifyDataSetChanged()
    }
}