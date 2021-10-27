package com.oatrice.internet_speed_testing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internet_speed_testing.InternetSpeedBuilder
import com.example.internet_speed_testing.InternetSpeedBuilder.OnEventInternetSpeedListener
import com.example.internet_speed_testing.ProgressionModel

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)

        adapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter;
    }

    override fun onStart() {
        super.onStart()
        val builder = InternetSpeedBuilder(this)
        builder.setOnEventInternetSpeedListener(object : OnEventInternetSpeedListener {
            override fun onDownloadProgress(count: Int, progressModel: ProgressionModel) {}
            override fun onUploadProgress(count: Int, progressModel: ProgressionModel) {}
            override fun onTotalProgress(count: Int, progressModel: ProgressionModel) {
                adapter.setDataList(count, progressModel)
            }
        })
        builder.start("https://leil.de/di/files/more/testdaten/1mb.test", 20)
    }

}
