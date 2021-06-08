package com.example.cheilros.models


class AddVisitPlanModel(val status: Int)

//region Investment
class InvestmentModel(val status: Int, val data: List<InvestmentData>)
class InvestmentData(
    val ElementID: Int,
    val ElementTitle: String,
    val Brands: List<BrandsData>
)

class BrandsData(
    val BrandID: Int,
    val BrandName: String,
    val ElementStatus: String
)
//endregion

//region Checklist
class CheckListModel(val status: Int, val data: List<CheckListData>)
class CheckListData(val ChecklistCategoryID: Int, val Checklist: String)

class CheckListDetailModel(val status: Int, val data: List<CheckListDetailData>)
class CheckListDetailData(
    val ChecklistID: Int,
    val Question: String,
    val InputTypeID: Int,
    val CheckListStatus: String
)
//endregion

//region BaseURL
class AppSettingModel(val status: Int, val data: List<AppSettingData>)
class AppSettingData(
    val ROS_LabelID: Int,
    val ROS_Screen: String,
    val ROS_LabelName: String,
    val LanguageID: Int,
    val ImageLocation: String,
    val FixedLabelName: String
)
//endregion

class CheckInOutModel(val status: Int)
class HookBin(val success: Int)

//region User Permission
class LoginUserPermission(val status: Int, val data: List<LoginUserPermissionData>)
class LoginUserPermissionData(
    val PermissionID: Int,
    val PermissionName: String,
    val Permission: String
)
//endregion

//region Journey Plan
class JPStatusModel(val status: Int, val data: List<JPStatusData>)
class JPStatusData(
    val VisitStatusID: Int,
    val VisitStatus: String,
    val StatusCount: String,
    val IconImage: String
)

class JourneyPlanModel(val status: Int, val data: List<JourneyPlanData>)
class JourneyPlanData(
    val VisitID: Int,
    val TeamMemberID: Int,
    val PlanDate: String,
    val Month: Int,
    val Year: Int,
    val WeekNo: Int,
    val CheckInTime: String,
    val CheckOutTime: String,
    val Address: String,
    val VisitRemarks: String,
    val CheckInRemarks: String,
    val CheckOutRemarks: String,
    val VisitStatusID: Int,
    val VisitStatus: String,
    val StoreID: Int,
    val StoreCode: String,
    val StoreName: String,
    val Longitude: String,
    val Latitude: String,
    val ImageLocation: String
)

class JPCurrentWeekData(
    val CurrentDate: String
)
//endregion

//region My Coverage
class MyCoverageModel(val status: Int, val data: List<MyCoverageData>)
class MyCoverageData(
    val StoreID: Int,
    val StoreCode: String,
    val StoreName: String,
    val ChannelID: Int,
    val ChannelName: String,
    val RegionName: String,
    val DistrcitName: String,
    val MallName: String,
    val Address: String,
    val Longitude: String,
    val Latitude: String,
    val LastVisitedDate: String,
    val VistedBy: String
)

class ChannelModel(val status: Int, val data: List<ChannelData>)
class ChannelData(
    val ChannelID: Int,
    val ChannelName: String
)
//endregion