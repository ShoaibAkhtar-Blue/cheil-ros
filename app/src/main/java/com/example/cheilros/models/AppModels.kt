package com.example.cheilros.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


class AddVisitPlanModel(val status: Int)


//region Store View
class StoreInfoModel(val status: Int, val data: List<StoreInfoData>)
class StoreInfoData(
    val StoreID: Int,
    val StoreCode: String,
    val StoreName: String,
    val StoreTypeName: String,
    val DistrcitName: String,
    val RegionName: String,
    val GradeName: String,
    val MallName: String,
    val Address: String,
    val DistributorName: String
)
//endregion

//region Team Status
class AssignedTeamMemberModel(val status: Int, val data: List<AssignedTeamMemberData>)
class AssignedTeamMemberData(
    val AssignedTeamMemberID: Int,
    val TeamMemberName: String,
    val TeamMemberID: Int
)

class TeamStatusModel(val status: Int, val data: List<TeamStatusData>)
class TeamStatusData(
    val TeamMemberID: Int,
    val PlanDate: String,
    val CheckInTime: String,
    val CheckOutTime: String,
    val VisitRemarks: String,
    val TeamMemberName: String,
    val StoreName: String,
    val TeamTypeName: String
)
//endregion

//region Training
class TrainingModel(val status: Int, val data: List<TrainingModelData>)
class TrainingModelData(val TrainingModelID: Int, val TrainingModelTitle: String)

class RecentTrainingModel(val status: Int, val data: List<RecentTrainingModelData>)
class RecentTrainingModelData(
    val TrainingModelID: Int,
    val TrainingModelTitle: String,
    val TrainingDateTime: String,
    val StoreID: Int,
    val Attendees: Int,
    val TeamMemberID: Int,
    val StoreID1: Int
)

class TeamMemberModel(val status: Int, val data: List<TeamMemberData>)
class TeamMemberData(val TeamMemberID: Int, val TeamMemberName: String)

//endregion

//region Activity
class RecentActivityModel(val status: Int, val data: List<RecentActivityData>)
class RecentActivityData(
    val ActivityID: Int,
    val TeamMemberID: Int,
    val mySingleID: String,
    val TeamMemberName: String,
    val ActivityDateTime: String,
    val ActivityTypeID: Int,
    val ActivityTypeName: String,
    val ActivityCategoryID: Int,
    val ActivityCategoryName: String,
    val BrandID: Int,
    val BrandName: String,
    val ActivityDescription: String,
    val StatusID: Int,
    val Quantity: Int,
    val StoreID: Int,
    val StoreCode: String,
    val StoreName: String
)

class ActivityTypeModel(val status: Int, val data: List<ActivityTypeData>)
class ActivityTypeData(
    val ActivityTypeID: Int,
    val ActivityTypeName: String,
    val DivisionID: Int,
    val DivisionName: String
)

class ActivityCategoryModel(val status: Int, val data: List<ActivityCategoryData>)
class ActivityCategoryData(
    val ActivityTypeID: Int,
    val ActivityTypeName: String,
    val ActivityCategoryID: Int,
    val ActivityCategoryName: String,
    val DivisionID: Int,
    val DivisionName: String
)
//endregion

//region Dashboard
class DashboardModel(val status: Int, val data: List<DashboardData>)
class DashboardData(val Coverage: String, val TodayVisit: String, val PendingData: String)

class DashboardBarChartModel(val status: Int, val data: List<DashboardBarChartData>)
class DashboardBarChartData(
    val TeamMemberID: Int,
    val TrendDate: String,
    val Value1: String,
    val Value2: String
)

class DashboardTaskAssignedModel(val status: Int, val data: List<DashboardTaskAssignedData>)
class DashboardTaskAssignedData(
    val TaskID: Int,
    val TaskTitle: String,
    val TaskDescription: String,
    val AssignedDateTime: String,
    val TeamMemberName: String,
    val TaskTitle1: String
)
//endregion

//region Investment

class InvestmentModel(val status: Int, val data: List<InvestmentData>)
class InvestmentData(
    val ElementID: Int,
    val ElementTitle: String,
    val ElementUpdateDate: String,
    val Brands: List<BrandsData>
)

class InvestmentAnswerModel(val status: Int, val data: List<InvestmentAnswerData>)
class InvestmentAnswerData(
    val ElementID: Int,
    val ElementTitle: String,
    val ElementStatus: String
)

@Parcelize
data class BrandsData(
    val BrandID: Int,
    val BrandName: String,
    val ElementStatus: String
) : Parcelable

@Parcelize
class BrandsList : ArrayList<BrandsData>(), Parcelable

class InvestmentJSON(val data: List<InvestmentJSONData>)
class InvestmentJSONData(
    val BrandID: Int,
    val StoreID: Int?,
    val ElementID: Int?,
    val ElementStatus: String,
    val Remarks: String,
    val TeamMemberID: Int?,
    val InvestmentUpdateDate: String
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

class CheckListJSON(val data: List<CheckListJSONData>)
class CheckListJSONData(
    val CheckListID: Int?,
    val StoreID: Int?,
    val CheckListStatus: String,
    val TeamMemberID: Int?
)

class CheckListAnswerModel(val status: Int, val data: List<CheckListAnswerData>)
class CheckListAnswerData(
    val CheckListID: Int,
    val Question: String,
    val StoreID: Int,
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

class ChannelTypeModel(val status: Int, val data: List<ChannelTypeData>)
class ChannelTypeData(
    val ChannelTypeID: Int,
    val ChannelTypeName: String
)
//endregion