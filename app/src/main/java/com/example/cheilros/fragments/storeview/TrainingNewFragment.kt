package com.example.cheilros.fragments.storeview

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.CapturedPictureAdapter
import com.example.cheilros.adapters.TrainingAdapter
import com.example.cheilros.adapters.TrainingAttendeesAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.models.TeamMemberData
import com.example.cheilros.models.TeamMemberModel
import com.example.cheilros.models.TrainingModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.dialog_add_visit.btnAccept
import kotlinx.android.synthetic.main.dialog_add_visit.btnCancel
import kotlinx.android.synthetic.main.dialog_add_visit.btnDate
import kotlinx.android.synthetic.main.dialog_add_visit.txtQuestion
import kotlinx.android.synthetic.main.dialog_add_visit.txtTitle
import kotlinx.android.synthetic.main.dialog_checklist.*
import kotlinx.android.synthetic.main.dialog_training_attendee.*
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_training.*
import kotlinx.android.synthetic.main.fragment_training.rvTraining
import kotlinx.android.synthetic.main.fragment_training_detail.*
import kotlinx.android.synthetic.main.fragment_training_detail.btnAddAttendee
import kotlinx.android.synthetic.main.fragment_training_detail.btnSubmit
import kotlinx.android.synthetic.main.fragment_training_detail.btnTakePictureTraining
import kotlinx.android.synthetic.main.fragment_training_detail.mainLoadingLayoutTD
import kotlinx.android.synthetic.main.fragment_training_detail.rvAttendees
import kotlinx.android.synthetic.main.fragment_training_detail.rvTrainingPictures
import kotlinx.android.synthetic.main.fragment_training_detail.view.*
import kotlinx.android.synthetic.main.fragment_training_new.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class TrainingNewFragment : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: TrainingAttendeesAdapter

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    lateinit var layoutManagerTL: RecyclerView.LayoutManager
    lateinit var recylcerAdapterTL: TrainingAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training_new, container, false)
        toolbarVisibility(false)
        //region Set Labels
        try {
            //view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_Training" }.get(0).labelName
        } catch (ex: Exception) {

        }
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainLoadingLayoutTD.setState(LoadingLayout.LOADING)



        if (CSP.getData("team_type_id")!!.toInt() <= 4) {
            btnAddAttendee.visibility = View.GONE
            LLImageRV.visibility = View.GONE
            btnSubmit.visibility = View.INVISIBLE
        }

        try {
            rvTrainingPictures.setHasFixedSize(true)
            layoutManagerPA = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            rvTrainingPictures.layoutManager = layoutManagerPA
            recylcerAdapterPA = CapturedPictureAdapter(requireContext(), capturedPicturesList)
            rvTrainingPictures.adapter = recylcerAdapterPA
        } catch (ex: Exception) {

        }

        fetchTraining("${CSP.getData("base_url")}/Training.asmx/TrainingModelsList")

        fetchAttendees(
            "${CSP.getData("base_url")}/Training.asmx/StoreTeamMemberForTraining?StoreID=${
                if (CSP.getData("selectedStores") == "") arguments?.getInt(
                    "StoreID"
                ) else CSP.getData("selectedStores")
            }"
        )

        btnCheckinTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                btnCheckinTime.text = SimpleDateFormat("HH:mm:ss").format(cal.time)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnCheckoutTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                btnCheckoutTime.text = SimpleDateFormat("HH:mm:ss").format(cal.time)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnAddAttendee.setOnClickListener {

            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_training_attendee, null)

            val dialog = Dialog(requireContext())
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.txtTitle.text =
                settingData.filter { it.fixedLabelName == "Training_AddAttendees" }.get(0).labelName
            dialog.txtQuestion.text =
                settingData.filter { it.fixedLabelName == "Training_AddTittle" }.get(0).labelName
            dialog.OTAttendeeName.hint =
                settingData.filter { it.fixedLabelName == "Training_EnterName" }.get(0).labelName
            dialog.btnCancel.text =
                settingData.filter { it.fixedLabelName == "Logout_Cancel" }.get(0).labelName
            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.btnAccept.text =
                settingData.filter { it.fixedLabelName == "StoreList_PopupAdd" }.get(0).labelName
            dialog.btnAccept.setOnClickListener {
                recylcerAdapter.addNewItem(
                    TeamMemberData(
                        0,
                        dialog.etAttendeeName.text.toString(),
                        1
                    )
                )
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
                findNavController().navigate(
                    R.id.action_trainingNewFragment_to_cameraActivity,
                    bundle
                )
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
            val client = OkHttpClient()

            if (CSP.getData("sess_selected_training_features") != "") {
                try {

                    val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
                    val currentDateAndTime: String = simpleDateFormat.format(Date())

                    val builder: MultipartBody.Builder =
                        MultipartBody.Builder().setType(MultipartBody.FORM)

                    println(CSP.getData("training_attendees"))

                    builder.addFormDataPart(
                        "Stores",
                        "${
                            if (CSP.getData("selectedStores") == "") arguments?.getInt(
                                "StoreID"
                            ) else CSP.getData("selectedStores")
                        }"
                    )

                    builder.addFormDataPart(
                        "TrainingTypeID",
                        "1"
                    )

                    CSP.getData("training_attendees")?.let { it1 ->
                        builder.addFormDataPart(
                            "Attendees",
                            it1
                        )
                    }

                    CSP.getData("sess_selected_training_features")?.let { it1 ->
                        builder.addFormDataPart(
                            "TrainingModel",
                            it1
                        )
                    }

                    for (paths in capturedPicturesList) {
                        println(paths)
                        val sourceFile = File(paths)
                        val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                        val fileName: String = sourceFile.name
                        builder.addFormDataPart(
                            "TrainingPictures",
                            fileName,
                            sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                        )
                    }

                    println(
                        "${CSP.getData("base_url")}/Training.asmx/OperTrainingDetail?TrainingModelID=${
                            arguments?.getInt(
                                "TrainingModelID"
                            )
                        }&StoreID=${
                            if (CSP.getData("selectedStores") == "") arguments?.getInt(
                                "StoreID"
                            ) else CSP.getData("selectedStores")
                        }&Description=-&TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&TrainingDateTime=${currentDateAndTime}&StartTime=${currentDateAndTime} ${btnCheckinTime.text}&EndTime=${currentDateAndTime} ${btnCheckoutTime.text}&AttendeseTypeID=1"
                    )
                    val requestBody = builder.build()
                    val request: Request = Request.Builder()
                        .url(
                            "${CSP.getData("base_url")}/Training.asmx/OperTrainingDetail?TrainingModelID=${
                                arguments?.getInt(
                                    "TrainingModelID"
                                )
                            }&StoreID=${
                                if (CSP.getData("selectedStores") == "") arguments?.getInt(
                                    "StoreID"
                                ) else CSP.getData("selectedStores")
                            }&Description=-&TeamMemberID=${
                                CSP.getData(
                                    "user_id"
                                )
                            }&TrainingDateTime=${currentDateAndTime}&StartTime=${currentDateAndTime} ${btnCheckinTime.text}&EndTime=${currentDateAndTime} ${btnCheckoutTime.text}&AttendeseTypeID=1"
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
                                CSP.delData("sess_selected_training_features")
                                CSP.delData("TrainingDetail_SESSION_IMAGE")
                                CSP.delData("TrainingDetail_SESSION_IMAGE_SET")

                                if(team_type.toInt() >= 9){
                                    findNavController().navigate(R.id.action_trainingNewFragment_to_dashboardFragment)
                                }else{
                                    findNavController().navigateUp()
                                }

                            }
                        }

                    })

                } catch (ex: Exception) {
                    Log.e("Error_", ex.message.toString())
                }
            } else {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Warning!!")
                    .setMessage("Please select atleast one Feature!")
                    .sneakWarning()
            }


        }


    }

    override fun onResume() {
        super.onResume()
        println(CSP.getData("TrainingDetail_SESSION_IMAGE_SET"))
        if (!CSP.getData("TrainingDetail_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            if (CSP.getData("TrainingDetail_SESSION_IMAGE_SET").equals("")) {
                recylcerAdapterPA.addNewItem(CSP.getData("TrainingDetail_SESSION_IMAGE"))
                CSP.saveData(
                    "TrainingDetail_SESSION_IMAGE_SET",
                    CSP.getData("TrainingDetail_SESSION_IMAGE")
                )
                CSP.delData("TrainingDetail_SESSION_IMAGE")
            } else {
                recylcerAdapterPA.addNewItem(CSP.getData("TrainingDetail_SESSION_IMAGE"))
                CSP.saveData(
                    "TrainingDetail_SESSION_IMAGE_SET",
                    "${CSP.getData("TrainingDetail_SESSION_IMAGE_SET")},${CSP.getData("TrainingDetail_SESSION_IMAGE")}"
                )
                CSP.delData("TrainingDetail_SESSION_IMAGE")
            }
        } else if (!CSP.getData("sess_gallery_img").equals("")) {
            try {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Success!!")
                    .setMessage("Image Added to this session!")
                    .sneakSuccess()

                recylcerAdapterPA.addNewItem(CSP.getData("sess_gallery_img").toString())
                CSP.delData("sess_gallery_img")
            } catch (ex: Exception) {

            }
        }
    }

    fun fetchTraining(url: String) {
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
                    //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, TrainingModel::class.java)

                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvTraining.setHasFixedSize(true)
                            layoutManagerTL = LinearLayoutManager(requireContext())
                            rvTraining.layoutManager = layoutManagerTL
                            recylcerAdapterTL =
                                TrainingAdapter(requireContext(), apiData.data, arguments)
                            rvTraining.adapter = recylcerAdapterTL
                            //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                        })
                    } else {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Data not fetched.")
                                    .sneakWarning()
                            }
                            //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                        })
                    }
                } catch (ex: Exception) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage(ex.message.toString())
                                .sneakError()
                        }
                        findNavController().popBackStack()
                    })
                }
            }

        })
    }

    fun fetchAttendees(url: String) {
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
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, TeamMemberModel::class.java)

                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvAttendees.setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            rvAttendees.layoutManager = layoutManager
                            recylcerAdapter = TrainingAttendeesAdapter(
                                requireContext(),
                                apiData.data as MutableList<TeamMemberData>,
                                this@TrainingNewFragment,
                                arguments
                            )
                            rvAttendees.adapter = recylcerAdapter
                            mainLoadingLayoutTD.setState(LoadingLayout.COMPLETE)
                            toolbarVisibility(true)
                            (activity as NewDashboardActivity).shouldGoBack = true
                        })
                    } else {
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
                } catch (ex: Exception) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage(ex.message.toString())
                                .sneakError()
                        }
                        findNavController().popBackStack()
                    })
                }

            }

        })
    }
}