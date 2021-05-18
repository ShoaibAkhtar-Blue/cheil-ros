package com.example.cheilros.models

import android.view.View
import java.util.*

/*class JourneyPlanModel {
    var price: String? = null
    var pledgePrice: String? = null
    var fromAddress: String? = null
    var toAddress: String? = null
    var requestsCount = 0
    var date: String? = null
    var time: String? = null
    var requestBtnClickListener: View.OnClickListener? = null

    constructor() {}
    constructor(
        price: String?,
        pledgePrice: String?,
        fromAddress: String?,
        toAddress: String?,
        requestsCount: Int,
        date: String?,
        time: String?
    ) {
        this.price = price
        this.pledgePrice = pledgePrice
        this.fromAddress = fromAddress
        this.toAddress = toAddress
        this.requestsCount = requestsCount
        this.date = date
        this.time = time
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val JourneyPlanModel = o as JourneyPlanModel
        if (requestsCount != JourneyPlanModel.requestsCount) return false
        if (if (price != null) price != JourneyPlanModel.price else JourneyPlanModel.price != null) return false
        if (if (pledgePrice != null) pledgePrice != JourneyPlanModel.pledgePrice else JourneyPlanModel.pledgePrice != null) return false
        if (if (fromAddress != null) fromAddress != JourneyPlanModel.fromAddress else JourneyPlanModel.fromAddress != null) return false
        if (if (toAddress != null) toAddress != JourneyPlanModel.toAddress else JourneyPlanModel.toAddress != null) return false
        return if (if (date != null) date != JourneyPlanModel.date else JourneyPlanModel.date != null) false else !if (time != null) time != JourneyPlanModel.time else JourneyPlanModel.time != null
    }

    override fun hashCode(): Int {
        var result = if (price != null) price.hashCode() else 0
        result = 31 * result + if (pledgePrice != null) pledgePrice.hashCode() else 0
        result = 31 * result + if (fromAddress != null) fromAddress.hashCode() else 0
        result = 31 * result + if (toAddress != null) toAddress.hashCode() else 0
        result = 31 * result + requestsCount
        result = 31 * result + if (date != null) date.hashCode() else 0
        result = 31 * result + if (time != null) time.hashCode() else 0
        return result
    }

    companion object {
        *//**
         * @return List of elements prepared for tests
         *//*
        val testingList: ArrayList<JourneyPlanModel>
            get() {
                val JourneyPlanModels = ArrayList<JourneyPlanModel>()
                JourneyPlanModels.add(
                    JourneyPlanModel(
                        "$14",
                        "$270",
                        "W 79th St, NY, 10024",
                        "W 139th St, NY, 10030",
                        3,
                        "TODAY",
                        "05:10 PM"
                    )
                )
                JourneyPlanModels.add(
                    JourneyPlanModel(
                        "$23",
                        "$116",
                        "W 36th St, NY, 10015",
                        "W 114th St, NY, 10037",
                        10,
                        "TODAY",
                        "11:10 AM"
                    )
                )
                JourneyPlanModels.add(
                    JourneyPlanModel(
                        "$63",
                        "$350",
                        "W 36th St, NY, 10029",
                        "56th Ave, NY, 10041",
                        0,
                        "TODAY",
                        "07:11 PM"
                    )
                )
                JourneyPlanModels.add(
                    JourneyPlanModel(
                        "$19",
                        "$150",
                        "12th Ave, NY, 10012",
                        "W 57th St, NY, 10048",
                        8,
                        "TODAY",
                        "4:15 AM"
                    )
                )
                JourneyPlanModels.add(
                    JourneyPlanModel(
                        "$5",
                        "$300",
                        "56th Ave, NY, 10041",
                        "W 36th St, NY, 10029",
                        0,
                        "TODAY",
                        "06:15 PM"
                    )
                )
                return JourneyPlanModels
            }
    }
}*/
