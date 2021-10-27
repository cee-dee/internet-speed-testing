package com.oatrice.internet_speed_testing;

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.internet_speed_testing.Bandwidth
import com.example.internet_speed_testing.BandwidthResult
import java.math.BigDecimal
import java.util.*
import kotlin.math.roundToInt

enum class FinishState {
    NOT_FINISHED,
    SUCCESS,
    ERROR
}

data class BandwidthEntry(
    val percentage: Float,
    val downloadResult: Bandwidth,
    val finishState: FinishState
)

class Adapter : RecyclerView.Adapter<Adapter.BandwidthViewHolder>() {

    private val dataList = ArrayList<BandwidthEntry>();

    private fun ensureMinEntries(minCount: Int) {
        while (dataList.size < minCount) {
            dataList.add(createEmptyBandwidthEntry())
        }
    }

    private fun createEmptyBandwidthEntry(): BandwidthEntry {
        return BandwidthEntry(
            0f,
            createEmptyBandwidthResult(),
            FinishState.NOT_FINISHED
        )
    }

    private fun createEmptyBandwidthResult(): Bandwidth {
        return Bandwidth(
            BigDecimal.valueOf(0)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDownload(repetition: Int, data: BandwidthResult, finishState: FinishState) {
        ensureMinEntries(repetition)
        val currentEntry = dataList[repetition - 1]
        val newEntry = currentEntry.copy(
            percentage = data.percentage,
            downloadResult = data.bandwidth,
            finishState = finishState
        )
        dataList[repetition - 1] = newEntry

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BandwidthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false);
        return BandwidthViewHolder(view);
    }

    override fun onBindViewHolder(holder: BandwidthViewHolder, position: Int) {
        holder.bind(dataList[position]);
    }

    override fun getItemCount(): Int {
        return dataList.size;
    }

    class BandwidthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: BandwidthEntry) {

            val tvProgress = itemView.findViewById<TextView>(R.id.tvProgress);
            val tvDownload = itemView.findViewById<TextView>(R.id.tvDownload);
            val tvFinished = itemView.findViewById<TextView>(R.id.tvFinished);

            tvProgress.text = formatPercent(data.percentage)
            tvDownload.text = formatBandwidth(data.downloadResult)
            tvFinished.text = when (data.finishState) {
                FinishState.NOT_FINISHED -> "..."
                FinishState.SUCCESS -> "yes"
                FinishState.ERROR -> "failed"
            }
        }

        private fun formatPercent(percent: Float): String {
            return String.format("%d%%", percent.roundToInt())
        }

        private fun formatBandwidth(bandwidth: Bandwidth): String {
            return String.format("%,d", bandwidth.bitsPerSecond.toInt() / 8)
        }
    }
}
