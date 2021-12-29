package com.example.cheilros.models

import android.os.Parcelable
import android.text.Editable
import kotlinx.android.parcel.Parcelize


class AddVisitPlanModel(val status: Int)

//region Dashboard Cumlative
class DashboardCumlativeModel(val status: Int, val data: DashboardCumlativeData)
class DashboardCumlativeData(
    val market_activity: List<RecentActivityData>,
    val teammember_performance: List<DashboardBarChartData2>,
    val daily_trend: List<DashboardBarChartData>,
    val dashboard_labels: List<DashboardData>,
    val task_assigned: List<DashboardTaskAssignedData>,
    val managment_dashboard_labels: List<ManagerDashboardLabelData>,
    val managment_daily_sale: List<ManagerDailySaleData>,
    val managment_display_share: List<ManagerDisplayShareData>,
    val managment_daily_activity: List<RecentActivityData>,
    val training_summary: List<TrainingSummaryData>,
)
//endregion

//region Manager
class TrainingTypesModel(val status: Int, val data: List<TrainingTypesData>)
class TrainingTypesData(
    val TrainingTypeID: Int,
    val TrainingTypeName: String,
    val ActiveStatus: Int
)

class TrainingSummaryData(
    val TrainingTypeName: String,
    val TrainingDate: String,
    val Start: String,
    val EndTime: String,
    val Attendese: Int,
    val OtherAttendese: Int,
    val TeamMemberID: Int,
    val Description: String
)

class ManagerDashboardLabelData(
    val ManagementLabelID: String,
    val ManagementLabelName: String,
    val ManagementLabelValue: String
)

class ManagerDailySaleData(
    val Brand: String,
    val data: List<DailySaleData>
)

class DailySaleData(
    val date: String,
    val count: String
)

class ManagerDisplayShareData(
    val BrandName: String,
    val SaleDate: String,
    val TTLDisplay: String
)

class ManagerDailyActivityData(
    val BrandName: String,
    val SaleDate: String,
    val TTLDisplay: String
)

//endregion

//region TrainingSimple
class TrainingSimpleModel(val status: Int, val data: List<TrainingSimpleData>)
class TrainingSimpleData(
    val TrainingID: Int,
    val StoreID: Int,
    val StoreName: String,
    val TrainingDateTime: String,
    val Attendese: String,
    val AdditionalAttendese: String
)
//endregion

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
    val DistributorName: String,
    val SaleType: String
)

class AssetListModel(val status: Int, val data: List<AssetListData>)
class AssetListData(
    val AssetID: Int,
    val AssetTypeID: Int,
    val AssetTypeName: String,
    val AssetDescription: String,
    val BrandID: Int,
    val BrandName: String,
    val CreationDateTime: String,
    val TeamMemberName: String,
    val StoreID: Int,
    val StoreName: String,
    val ActiveStatus: Int,
    val InstallationDate: String,
    val Quantity: Int,
    val Capacity: Int,
    val StandTypeID: Int,
    val StandTypeName: String,
    val Stand_Width: String,
    val Stand_Depth_Length: String,
    val Stand_Height: String,
    val Stand_Sqm: String
)

class AssetBrandsModel(val status: Int, val data: List<AssetBrandsData>)
class AssetBrandsData(
    val BrandID: Int,
    val BrandName: String,
    val Assets: List<AssetBrandsListData>
)

class AssetBrandsListData(
    val AssetTypeID: Int,
    val AssetTypeName: String
)
//endregion

//region MyActivities
class MyActivitiesModel(val status: Int, val data: List<MyActivitiesData>)
class MyActivitiesData(
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
    val StoreName: String,
    val ImageActivity: String,
    val ImageActivity2: String
)
//region

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
class TrainingModelData(
    val TrainingModelID: Int,
    val TrainingModelTitle: String,
    val Features: List<TrainingFeaturesData>
)

class TrainingFeaturesData(val TrainingModelFeatureID: Int, val TrainingModelFeatureTitle: String)

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
class TeamMemberData(val TeamMemberID: Int, val TeamMemberName: String, val AttendeseTypeID: Int)

//endregion

//region TeamType

class ManagementTeamTypeModel(val status: Int, val data: List<ManagementTeamTypeData>)
class ManagementTeamTypeData(val TeamTypeID: Int, val TeamTypeName: String, val ActiveStatus: Int)

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
    val Quantity: String,
    val StoreID: Int,
    val StoreCode: String,
    val StoreName: String,
    val ImageActivity: String,
    val ImageActivity2: String
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
    val DivisionName: String,
    val TaskDeploymentCategoryID: Int,
    val TaskDeploymentCategoryName: String
)
//endregion

//region Issue Log
class IssueLogModel(val status: Int, val data: List<IssueLogData>)
class IssueLogData(
    val FollowupID: Int,
    val ActivityID: Int,
    val FollowupDescription: String,
    val FollowupDateTime: String,
    val TeamMemberID: Int,
    val ActiveStatus: Int,
    val FollowUpPicture1: String,
    val FollowUpPicture2: String
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

class DashboardBarChartData2(
    val TeamMemberID: Int,
    val PerformanceMonth: String,
    val PerformanceYear: String,
    val Score1: String,
    val Score2: String
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

//region Price
class PriceModel(val status: Int, val data: List<PriceData>)
class PriceData(
    val BrandID: Int,
    val BrandName: String,
    val Products: List<PriceModelData>
)

class PriceModelData(
    val ProductCategoryID: Int,
    val ProductCategoryName: String,
    val NoOfModels: Int
)

class PriceDetailModel(val status: Int, val data: List<PriceDetailData>)
class PriceDetailData(
    val ProductID: Int,
    val ShortName: String,
    val NetPrice: String,
    val Price: String,
    val Promotion: String,
    val Installment_3Month: String,
    val Installment_6Month: String,
    val Installment_12Month: String,
    val Username: String,
    var PiceTagPictureID: String
)
//endregion

//region Stock
class StockModel(val status: Int, val data: List<StockData>)
class StockData(
    val BrandID: Int,
    val BrandName: String,
    val Products: List<StockProductData>
)

class StockProductData(
    val ProductCategoryID: Int,
    val ProductCategoryName: String,
    val StockStatus: String,
    val Field2: String
)

class StockDetailModel(val status: Int, val data: List<StockDetailData>)
class StockDetailData(
    val ProductID: Int,
    val ShortName: String,
    val StockStatus: Int,
    val Field: String
)

class StockJSON(val data: List<StockJSONData>)
class StockJSONData(
    val ProductID: Int,
    val StoreID: Int?,
    val StockCount: String,
    val Field2: String,
    val TeamMemberID: Int?,
    val StockDate: String?
)
//endregion

//region Sales
class SalesModel(val status: Int, val data: List<SalesData>)
class SalesData(
    val BrandID: Int,
    val BrandName: String,
    val Products: List<SalesProductData>
)

class SalesProductData(
    val ProductCategoryID: Int,
    val ProductCategoryName: String,
    val SaleQuantity: Int,
    val SaleValue: Int
)

class SalesDetailModel(val status: Int, val data: List<SalesDetailData>)
class SalesDetailData(
    val ProductID: Int,
    val ShortName: String,
    val SaleQuantity: Int,
    val SaleValue: Int
)

class DailyMultipleSaleModel(val status: Int, val data: List<DailyMultipleSaleData>)
class DailyMultipleSaleData(
    val SaleID: Int,
    val StoreID: Int,
    val ProductID: Int,
    val SaleCount: Int,
    val SalePrice: Int,
    val SaleDate: String,
    val SaleType: Int
)


class SalesJSON(val data: List<SalesJSONData>)
class SalesJSONData(
    val ProductID: Int,
    val StoreID: Int?,
    val SaleCount: String,
    val SalePrice: String,
    val TeamMemberID: Int?,
    val SaleDate: String?,
    val intSerialNo: Int,
    val SaleType: Int
)
//endregion


//region DisplayCount
class DisplayCountModel(val status: Int, val data: List<DisplayCountData>)
class DisplayCountData(
    val BrandID: Int,
    val BrandName: String,
    val Products: List<DisplayProductData>
)

class DisplayProductData(
    val ProductCategoryID: Int,
    val ProductCategoryName: String,
    val DisplayCount: String
)

class DisplayProductCategoryModel(val status: Int, val data: List<DisplayProductCategoryData>)
class DisplayProductCategoryData(
    val ProductCategoryID: Int,
    val ProductCategoryName: String
)

class DisplayCountDetailViewModel(val status: Int, val data: List<DisplayCountDetailViewData>)
class DisplayCountDetailViewData(
    val SerialNumber: String,
    val UnitType: String
)

class DisplayCountViewModel(val status: Int, val data: List<DisplayCountViewData>)
class DisplayCountViewData(
    val ProductID: Int,
    val ShortName: String,
    var DisplayCount: Int,
    var isBarCodeEnabled: String
)

class DisplayCountJSON(val data: List<DisplayCountJSONData>)
class DisplayCountJSONData(
    val ProductID: Int,
    val StoreID: Int?,
    val SerialNumber: String?,
    val TeamMemberID: Int?,
    val IsBarCodeEnabled: String,
    val UnitType: String,
)

@Parcelize
data class DisplayCountProductsData(
    val ProductID: Int,
    val ShortName: String,
    val DisplayCount: Int
) : Parcelable

//endregion

//region Investment

class InvestmentModel(val status: Int, val data: List<InvestmentData>)
class InvestmentData(
    val ElementID: Int,
    val ElementTitle: String,
    val ElementUpdateDate: String,
    val InvestmentTypeID: Int,
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
    val CheckListStatus: String,
    val Rate: String
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
    val FixedLabelName: String,
    val ActiveStatus: Int
)

class AppLanguagesModel(val status: Int, val data: List<AppLanguagesData>)
class AppLanguagesData(
    val LanguageID: Int,
    val Language: String
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

//region Pending Deployment
class PendingDeploymentModel(val status: Int, val data: List<PendingDeploymentData>)
class PendingDeploymentData(
    val ActivityCategoryID: Int,
    val StoreID: Int,
    val PlanDateTime: String,
    val TaskDeploymentCategoryName: String,
    val ActivityCategoryName: String,
    val StoreCode: String,
    val StoreName: String,
    val StatusID: Int
)
//endregion

//region General Pictures
class GeneralElementModel(val status: Int, val data: List<GeneralElementData>)
class GeneralElementData(
    val StorePictureElementID: Int,
    val StorePictureElementName: String
)

class GeneralPicturesModel(val status: Int, val data: List<GeneralPicturesData>)
class GeneralPicturesData(
    val PictureID: Int,
    val StoreID: Int,
    val TeamMembeID: Int,
    val BrandID: Int,
    val PictureElementID: Int,
    val Remarks: String,
    val CreationDateTime: String,
    val StorePictureElementID: Int,
    val StorePictureElementName: String,
    val BrandName: String,
    val PictureName: String
)

class BrandModel(val status: Int, val data: List<BrandData>)
class BrandData(
    val BrandID: Int,
    val BrandName: String
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
    val VistedBy: String,
    val VisitStatusID: Int
)
data class SelectedMyCoverageData(
    val StoreID: Int,
    val StoreCode: String,
    val StoreName: String,
    val isSelect: Boolean
)

class ChannelModel(val status: Int, val data: List<ChannelData>)
class ChannelData(
    val ChannelID: Int,
    val ChannelName: String
)
class DeploymentReasonModel(val status: Int, val data: List<DeploymentReasonData>)
class DeploymentReasonData(
    val DeploymentReasonID: Int,
    val DeploymentReason: String,
    val ActiveStatus: Int
)

class ChannelTypeModel(val status: Int, val data: List<ChannelTypeData>)
class ChannelTypeData(
    val ChannelTypeID: Int,
    val ChannelTypeName: String
)
//endregion

//region Generic
class GenericModel(val status: Int)
//endregion