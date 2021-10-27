package com.oatrice.internet_speed_testing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.internet_speed_testing.ProgressionModel
import java.util.ArrayList

class Adapter : RecyclerView.Adapter<Adapter.MyViewHolder?>() {
    private val dataList: MutableList<ProgressionModel>? = ArrayList()
    fun setDataList(position: Int, data: ProgressionModel) {
        if (dataList!!.size <= position) {
            dataList.add(data)
        } else {
            dataList[position] = data
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList!![position])
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProgress: AppCompatTextView
        private val tvDownload: AppCompatTextView
        private val tvUpload: AppCompatTextView
        fun bind(progressionModel: ProgressionModel) {
            tvProgress.setText("" + progressionModel.progressTotal)
            tvDownload.setText("" + progressionModel.downloadSpeed)
            tvUpload.setText("" + progressionModel.uploadSpeed)
        }

        init {
            tvProgress = itemView.findViewById(R.id.tvProgress)
            tvDownload = itemView.findViewById(R.id.tvDownload)
            tvUpload = itemView.findViewById(R.id.tvUpload)
        }
    }
}