package com.oatrice.internet_speed_testing;

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

data class BandwidthEntry(
    val percentage: Float,
    val downloadResult: Bandwidth,
    val uploadResult: Bandwidth
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
            createEmptyBandwidthResult()
        )
    }

    private fun createEmptyBandwidthResult(): Bandwidth {
        return Bandwidth(
            BigDecimal.valueOf(0)
        )
    }

    fun updateDownload(data: BandwidthResult) {
        ensureMinEntries(data.repetition)
        val currentEntry = dataList[data.repetition - 1]
        val newEntry = currentEntry.copy(
            percentage = data.percentage,
            downloadResult = data.bandwidth
        )
        dataList[data.repetition - 1] = newEntry

        notifyDataSetChanged()
    }

    fun updateUpload(data: BandwidthResult) {
        ensureMinEntries(data.repetition)
        val currentEntry = dataList[data.repetition - 1]
        val newEntry = currentEntry.copy(
            percentage = data.percentage,
            uploadResult = data.bandwidth
        )
        dataList[data.repetition - 1] = newEntry

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
            val tvUpload = itemView.findViewById<TextView>(R.id.tvUpload);

            tvProgress.text = formatPercent(data.percentage)
            tvDownload.text = formatBandwidth(data.downloadResult)
            tvUpload.text = formatBandwidth(data.uploadResult)
        }

        private fun formatPercent(percent: Float): String {
            return String.format("%d%%", percent.roundToInt())
        }

        private fun formatBandwidth(bandwidth: Bandwidth): String {
            return String.format("%,d", bandwidth.bitsPerSecond.toInt() / 8)
        }
    }
}
