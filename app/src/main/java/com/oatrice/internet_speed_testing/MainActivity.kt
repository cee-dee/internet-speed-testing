package com.oatrice.internet_speed_testing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internet_speed_testing.DownloadSpeedMeasurementBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
        val measurement = DownloadSpeedMeasurementBuilder()
            .withUrl("https://leil.de/di/files/more/testdaten/1mb.test")
            .withRepetitionCount(20)
            .withProgressListener {
                withContext(Dispatchers.Main) {
                    adapter.updateDownload(it)
                }
            }
            .build()

        MainScope().launch {
            measurement.run()
        }
    }

}
