package com.priyanshparekh.fairshare.group.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R

class ChartPagerAdapter(private val charts: ArrayList<View>): RecyclerView.Adapter<ChartPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chart_view_pager_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chartRootLayout.removeAllViews()

        val chartView = charts[position]
        (chartView.parent as? ViewGroup)?.removeView(chartView)

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )

        // Set constraints relative to parent (match parent ConstraintLayout)
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

        chartView.layoutParams = layoutParams

        holder.chartRootLayout.addView(chartView)
    }

    override fun getItemCount(): Int = charts.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chartRootLayout = view.findViewById<ConstraintLayout>(R.id.chart_root_layout)!!
    }
}