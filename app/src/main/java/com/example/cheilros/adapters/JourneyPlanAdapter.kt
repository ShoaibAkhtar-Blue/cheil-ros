package com.example.cheilros.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.cheilros.R
import com.example.cheilros.models.JourneyPlanModel
import com.ramotion.foldingcell.FoldingCell
import java.lang.String
import java.util.*

class JourneyPlanAdapter(context: Context?, objects: List<JourneyPlanModel?>?) :
    ArrayAdapter<JourneyPlanModel?>(context!!, 0, objects!!) {
    private val unfoldedIndexes = HashSet<Int>()
    var defaultRequestBtnClickListener: View.OnClickListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // get item for selected view
        val JourneyPlanModel: JourneyPlanModel? = getItem(position)
        // if cell is exists - reuse it, if not - create the new one from resource
        var cell = convertView as FoldingCell?
        val viewHolder: ViewHolder
        if (cell == null) {
            viewHolder = ViewHolder()
            val vi = LayoutInflater.from(context)
            cell = vi.inflate(R.layout.journy_plan_cell, parent, false) as FoldingCell
            viewHolder.btnCancel = cell.findViewById(R.id.btnCancel)
            // binding view parts to view holder
            /*viewHolder.price = cell.findViewById(R.id.title_price)
            viewHolder.time = cell.findViewById(R.id.title_time_label)
            viewHolder.date = cell.findViewById(R.id.title_date_label)
            viewHolder.fromAddress = cell.findViewById(R.id.title_from_address)
            viewHolder.toAddress = cell.findViewById(R.id.title_to_address)
            viewHolder.requestsCount = cell.findViewById(R.id.title_requests_count)
            viewHolder.pledgePrice = cell.findViewById(R.id.title_pledge)
            viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn)*/
            cell.tag = viewHolder
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true)
            } else {
                cell.fold(true)
            }
            viewHolder = cell.tag as ViewHolder
        }
        if (null == JourneyPlanModel) return cell

        // bind data from selected element to view through view holder
        /*viewHolder.price.setText(item.getPrice())
        viewHolder.time.setText(item.getTime())
        viewHolder.date.setText(item.getDate())
        viewHolder.fromAddress.setText(item.getFromAddress())
        viewHolder.toAddress.setText(item.getToAddress())
        viewHolder.requestsCount.setText(String.valueOf(item.getRequestsCount()))
        viewHolder.pledgePrice.setText(item.getPledgePrice())*/

        viewHolder.btnCancel!!.setOnClickListener{
            Log.i("btnCancel", "Clicked")
            registerToggle(position)
        }
        // set custom btn handler for list item from that item
        /*if (JourneyPlanModel.requestBtnClickListener != null) {
            viewHolder.btnCancel!!.setOnClickListener{
                unfoldedIndexes.remove(position)
            }
        } else {
            // (optionally) add "default" handler if no handler found in item
            //viewHolder.btnCancel!!.setOnClickListener(defaultRequestBtnClickListener)
        }*/
        return cell
    }

    fun registerToggle1(position: Int) {
        if (unfoldedIndexes.contains(position)) registerFold(position) else registerUnfold(position)
    }

    // simple methods for register cell state changes
    fun registerToggle(position: Int) {
        if (unfoldedIndexes.contains(position)) registerFold(position) else registerUnfold(position)
    }

    fun registerFold(position: Int) {
        unfoldedIndexes.remove(position)
    }

    fun registerUnfold(position: Int) {
        unfoldedIndexes.add(position)
    }

    // View lookup cache
    private class ViewHolder {
        var price: TextView? = null
        var btnCancel: Button? = null
        var pledgePrice: TextView? = null
        var fromAddress: TextView? = null
        var toAddress: TextView? = null
        var requestsCount: TextView? = null
        var date: TextView? = null
        var time: TextView? = null
    }
}