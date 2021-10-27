package com.oatrice.internet_speed_testing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.internet_speed_testing.DownloadSpeedMeasurement
import com.example.internet_speed_testing.DownloadSpeedMeasurementBuilder
import com.example.internet_speed_testing.FinalMeasurementState
import com.example.internet_speed_testing.IntermediateMeasurementState
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
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        MainScope().launch {
            (1..5).forEach {
                val result = createMeasurementWithRecyclerViewUpdater(it)
                    .run()
                when (result) {
                    is FinalMeasurementState.FinishedSuccessfully -> {
                        result.overallBandwidth?.let { bandwidthResult ->
                            adapter.updateDownload(
                                it,
                                bandwidthResult,
                                FinishState.SUCCESS
                            )
                        }
                    }
                    is FinalMeasurementState.FinishedWithError -> {
                        result.overallBandwidth?.let { bandwidthResult ->
                            adapter.updateDownload(
                                it,
                                bandwidthResult,
                                FinishState.ERROR
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createMeasurementWithRecyclerViewUpdater(repetition: Int): DownloadSpeedMeasurement {

        return createMeasurementExecutor {
            adapter.updateDownload(
                repetition,
                it.overallBandwidth,
                FinishState.NOT_FINISHED
            )
        }
    }

    private fun createMeasurementExecutor(listener: (IntermediateMeasurementState) -> Unit): DownloadSpeedMeasurement {
        return DownloadSpeedMeasurementBuilder()
            .withUrl("https://leil.de/di/files/more/testdaten/10mb.test")
            .withProgressListener {
                withContext(Dispatchers.Main) {
                    listener(it)
                }
            }
            .build()
    }
}
