package com.example.cheilros.models

class AddVisitPlanModel(val status: Int)

//region BaseURL
class AppSettingModel(val status: Int, val data : List<AppSettingData>)
class AppSettingData(val ROS_LabelID: Int, val ROS_Screen: String, val ROS_LabelName: String, val LanguageID: Int, val ImageLocation: String, val FixedLabelName: String)
//endregion