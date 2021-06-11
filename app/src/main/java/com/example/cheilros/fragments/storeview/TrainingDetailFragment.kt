package com.example.cheilros.fragments.storeview

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.CapturedPictureAdapter
import com.example.cheilros.adapters.TrainingAttendeesAdapter
import com.example.cheilros.fragments.BaseFragment
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
import kotlinx.android.synthetic.main.fragment_acrivity_detail.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.txtTitleHeader
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_training.*
import kotlinx.android.synthetic.main.fragment_training_detail.*
import okhttp3.*
import java.io.IOException

class TrainingDetailFragment : BaseFragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: TrainingAttendeesAdapter

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()

    lateinit var CSP: CustomSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training_detail, container, false)

        CSP = CustomSharedPref(requireContext())

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

        fetchAttendees("${CSP.getData("base_url")}/Training.asmx/StoreTeamMemberForTraining?StoreID=${arguments?.getInt("StoreID")}")

        btnAddAttendee.setOnClickListener {

            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_training_attendee, null)

            val dialog = Dialog(requireContext())
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.txtTitle.text = "Add Attendee"
            dialog.txtQuestion.text = "You can add new attendee by typing his/her name below"

            dialog.btnCancel.text = "Cancel"
            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.btnAccept.text = "Add"
            dialog.btnAccept.setOnClickListener {
                recylcerAdapter.addNewItem(TeamMemberData(0, dialog.etAttendeeName.text.toString()))
                dialog.dismiss()
            }

            dialog.show()
        }

        btnTakePictureTraining.setOnClickListener {
            CSP.saveData("fragName", "TrainingDetail")
            val bundle = bundleOf("fragName" to "TrainingDetailFragment")
            findNavController().navigate(R.id.action_trainingDetailFragment_to_cameraActivity, bundle)
        }

        /*btnSubmit.setOnClickListener {
            val client = OkHttpClient()

            try {
                val builder: MultipartBody.Builder =
                    MultipartBody.Builder().setType(MultipartBody.FORM)

                CSP.getData("training_attendees")?.let { it1 ->
                    builder.addFormDataPart(
                        "Attendees",
                        it1
                    )
                }

                if (!CSP.getData("TrainingDetail_SESSION_IMAGE_SET").equals("")) {
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
                }
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
        }*/

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
        }
    }

    fun fetchAttendees(url: String){
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
    }
}