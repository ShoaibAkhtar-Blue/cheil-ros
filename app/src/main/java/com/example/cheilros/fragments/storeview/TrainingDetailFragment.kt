package com.example.cheilros.fragments.storeview

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.CapturedPictureAdapter
import com.example.cheilros.adapters.TrainingAttendeesAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.UtilClass
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.TeamMemberData
import com.example.cheilros.models.TeamMemberModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.dialog_add_visit.btnAccept
import kotlinx.android.synthetic.main.dialog_add_visit.btnCancel
import kotlinx.android.synthetic.main.dialog_add_visit.txtQuestion
import kotlinx.android.synthetic.main.dialog_add_visit.txtTitle
import kotlinx.android.synthetic.main.dialog_training_attendee.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.txtTitleHeader
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_training.*
import kotlinx.android.synthetic.main.fragment_training_detail.*
import kotlinx.android.synthetic.main.fragment_training_detail.view.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class TrainingDetailFragment : BaseFragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: TrainingAttendeesAdapter

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training_detail, container, false)

        //region Set Labels
        view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_Training" }.get(0).labelName
        view.txtTrainingDescription.hint = settingData.filter { it.fixedLabelName == "ActivityDescription" }.get(0).labelName
        view.PromoterName.text = settingData.filter { it.fixedLabelName == "PromoterName" }.get(0).labelName
        view.PromoterAttend.text = settingData.filter { it.fixedLabelName == "PromoterAttend" }.get(0).labelName
        //endregion



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtTitleHeader.text = arguments?.getString("TrainingModelTitle")

        try{
            rvTrainingPictures.setHasFixedSize(true)
            layoutManagerPA = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL, false)
            rvTrainingPictures.layoutManager = layoutManagerPA
            recylcerAdapterPA = CapturedPictureAdapter(requireContext(), capturedPicturesList)
            rvTrainingPictures.adapter = recylcerAdapterPA
        }catch (ex: Exception){

        }

        //fetchAttendees("${CSP.getData("base_url")}/Training.asmx/StoreTeamMemberForTraining?StoreID=${arguments?.getInt("StoreID")}")

        btnAddAttendee.setOnClickListener {

            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_training_attendee, null)

            val dialog = Dialog(requireContext())
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.txtTitle.text = settingData.filter { it.fixedLabelName == "AttendeeAddPopupTitle" }.get(0).labelName
            dialog.txtQuestion.text = settingData.filter { it.fixedLabelName == "AttendeeAddPopupInfo" }.get(0).labelName
            dialog.OTAttendeeName.hint = settingData.filter { it.fixedLabelName == "AttendeeAddPopupName" }.get(0).labelName
            dialog.btnCancel.text = settingData.filter { it.fixedLabelName == "Logout_Cancel" }.get(0).labelName
            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.btnAccept.text = settingData.filter { it.fixedLabelName == "StoreList_PopupAdd" }.get(0).labelName
            dialog.btnAccept.setOnClickListener {
                recylcerAdapter.addNewItem(TeamMemberData(0, dialog.etAttendeeName.text.toString(), 1, "")) // SA
                dialog.dismiss()
            }

            dialog.show()
        }

        btnTakePictureTraining.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose...")
            builder.setMessage("Please select one of the options")

            builder.setPositiveButton("Camera") { dialog, which ->
                CSP.saveData("fragName", "TrainingDetail")
                val bundle = bundleOf("fragName" to "TrainingDetailFragment")
                findNavController().navigate(R.id.action_trainingDetailFragment_to_cameraActivity, bundle)
            }

            builder.setNegativeButton("Gallery") { dialog, which ->
                val activity = requireActivity() as NewDashboardActivity
                activity.pickFromGallery()
            }

            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        btnSubmit.setOnClickListener {
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

                CSP.getData("training_attendees")?.let { it1 ->
                    builder.addFormDataPart(
                        "Attendees",
                        it1
                    )
                }

                for (paths in capturedPicturesList) {
                    println(paths)
                    val ImageFile = File(paths)
                    val sourceFile = UtilClass.saveBitmapToFile(ImageFile)
                    if (sourceFile!= null) {
                        val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                        val fileName: String = sourceFile.name
                        builder.addFormDataPart(
                            "TrainingPictures",
                            fileName,
                            sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                        )
                    }
                }

                /*if (!CSP.getData("TrainingDetail_SESSION_IMAGE_SET").equals("")) {
                    val imgPaths = CSP.getData("TrainingDetail_SESSION_IMAGE_SET")?.split(",")
                    if (imgPaths != null) {
                        for (paths in imgPaths) {
                            println(paths)
                            val sourceFile = File(paths)
                            val mimeType =
                                CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                            val fileName: String = sourceFile.name
                            builder.addFormDataPart(
                                "TrainingPictures",
                                fileName,
                                sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                            )
                        }
                    }
                }*/
                println("${CSP.getData("base_url")}/Training.asmx/OperTrainingDetail?TrainingModelID=${
                    arguments?.getInt(
                        "TrainingModelID"
                    )
                }&StoreID=${arguments?.getInt("StoreID")}&Description=${txtTrainingDescription.text}&TeamMemberID=${
                    CSP.getData(
                        "user_id"
                    )
                }&TrainingDateTime=2021-05-06")
                val requestBody = builder.build()
                val request: Request = Request.Builder()
                    .url(
                        "${CSP.getData("base_url")}/Training.asmx/OperTrainingDetail?TrainingModelID=${
                            arguments?.getInt(
                                "TrainingModelID"
                            )
                        }&StoreID=${arguments?.getInt("StoreID")}&Description=${txtTrainingDescription.text}&TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&TrainingDateTime=2021-05-06"
                    )
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        requireActivity().runOnUiThread {
                            activity?.let { it1 ->
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

                        requireActivity().runOnUiThread {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("Activity Updated!")
                                    .sneakSuccess()
                            }
                            CSP.delData("training_attendees")
                            CSP.delData("TrainingDetail_SESSION_IMAGE")
                            CSP.delData("TrainingDetail_SESSION_IMAGE_SET")

                            findNavController().navigateUp()
                        }
                    }

                })

            } catch (ex: Exception) {


            }
        }

    }

    override fun onResume() {
        super.onResume()
        println(CSP.getData("TrainingDetail_SESSION_IMAGE_SET"))
        if(!CSP.getData("TrainingDetail_SESSION_IMAGE").equals("")){
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            if (CSP.getData("TrainingDetail_SESSION_IMAGE_SET").equals("")) {
                recylcerAdapterPA.addNewItem(CSP.getData("TrainingDetail_SESSION_IMAGE"))
                CSP.saveData("TrainingDetail_SESSION_IMAGE_SET", CSP.getData("TrainingDetail_SESSION_IMAGE"))
                CSP.delData("TrainingDetail_SESSION_IMAGE")
            } else {
                recylcerAdapterPA.addNewItem(CSP.getData("TrainingDetail_SESSION_IMAGE"))
                CSP.saveData(
                    "TrainingDetail_SESSION_IMAGE_SET",
                    "${CSP.getData("TrainingDetail_SESSION_IMAGE_SET")},${CSP.getData("TrainingDetail_SESSION_IMAGE")}"
                )
                CSP.delData("TrainingDetail_SESSION_IMAGE")
            }
        }else if(!CSP.getData("sess_gallery_img").equals("")){
            try {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Success!!")
                    .setMessage("Image Added to this session!")
                    .sneakSuccess()

                recylcerAdapterPA.addNewItem(CSP.getData("sess_gallery_img").toString())
                CSP.delData("sess_gallery_img")
            }catch (ex: Exception){

            }
        }
    }

    /*fun fetchAttendees(url: String){
        val client = OkHttpClient()
        println(url)
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread(java.lang.Runnable {
                    activity?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Error!!")
                            .setMessage(e.message.toString())
                            .sneakError()
                    }
                    mainLoadingLayoutTD.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, TeamMemberModel::class.java)

                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvAttendees.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvAttendees.layoutManager = layoutManager
                        recylcerAdapter = TrainingAttendeesAdapter(requireContext(),
                            apiData.data as MutableList<TeamMemberData>, this@TrainingDetailFragment, arguments)
                        rvAttendees.adapter = recylcerAdapter
                        mainLoadingLayoutTD.setState(LoadingLayout.COMPLETE)
                    })
                }else{
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                        mainLoadingLayoutTD.setState(LoadingLayout.COMPLETE)
                    })
                }

            }

        })
    }*/
}