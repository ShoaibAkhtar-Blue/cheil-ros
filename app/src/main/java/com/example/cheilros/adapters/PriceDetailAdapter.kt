package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckListDetailData
import com.example.cheilros.models.PriceData
import com.example.cheilros.models.PriceDetailData
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_add_visit.*
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
    val activity: NewDashboardActivity
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
        var txtPromotion: TextView = view.findViewById(R.id.txtPromotion)
        var imgTag: ImageView = view.findViewById(R.id.imgTag)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_price_detail, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "WrongConstant")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.txtCount.text = (position+1).toString()
        holder.txtTitleHeader.text = itemList[position].ShortName
        holder.txtNetPrice.text = itemList[position].NetPrice
        holder.txtPrice.text = itemList[position].Price
        holder.txtPromotion.text = itemList[position].Promotion
        println("${CSP.getData("base_url")}/PricesPictures/${itemList[position].PiceTagPictureID}")
        Glide.with(context)
            .load("${CSP.getData("base_url")}/PricesPictures/${itemList[position].PiceTagPictureID}")
            .into(holder.imgTag!!)

        holder.LLPriceDetail.setOnClickListener {
            val li = LayoutInflater.from(context)
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

                                updateItem(
                                    itemList[position].ProductID,
                                    PriceDetailData(
                                        itemList[position].ProductID,
                                        itemList[position].ShortName,
                                        dialog.etNetPrice.text.toString(),
                                        dialog.etPrice.text.toString(),
                                        dialog.etPromotion.text.toString(),
                                        CSP.getData("PriceDetail_SESSION_IMAGE").toString()
                                    )
                                )

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

            dialog.show()
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int = position

    fun addNewItem(imgPath: String) {
        recylcerAdapterPA.addNewItem(imgPath)
    }

    fun updateItem(pid: Int, answer: PriceDetailData) {
        val index = itemList.indexOf(itemList.find { it.ProductID == pid })
        itemList[index] = answer
        notifyDataSetChanged()
    }
}