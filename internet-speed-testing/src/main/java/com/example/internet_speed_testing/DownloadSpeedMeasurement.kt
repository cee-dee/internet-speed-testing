package com.example.internet_speed_testing

import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DownloadSpeedMeasurement(
    private val url: String,
    private val repetitions: Int,
    private val listener: BandwidthListener
) {

    private var countTestSpeed = 0

    suspend fun run() = withContext(Dispatchers.IO) {

        val scope = this
        (1..repetitions).forEach {
            countTestSpeed = it
            val speedTestSocket = SpeedTestSocket()
            val deferredResult = CompletableDeferred<FinishState>()
            // add a listener to wait for speedtest completion and progress
            speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

                override fun onCompletion(report: SpeedTestReport) {
                    Log.v("speedtest Download $countTestSpeed", "[COMPLETED] rate in octet/s : ${report.transferRateOctet}")
                    Log.v("speedtest Download $countTestSpeed" , "[COMPLETED] rate in bit/s   : ${report.transferRateBit}")
                    deferredResult.complete(FinishState.COMPLETED)
                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    Log.v("speedtest Download $countTestSpeed", "[FAILED]")
                    deferredResult.complete(FinishState.FAILED)
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    // called to notify download/upload progress
                    Log.v("speedtest Download $countTestSpeed", "[PROGRESS] progress : $percent%")
                    Log.v("speedtest Download $countTestSpeed", "[PROGRESS] rate in octet/s : ${report.transferRateOctet}")
                    Log.v("speedtest Download $countTestSpeed", "[PROGRESS] rate in bit/s   : ${report.transferRateBit}")

                    scope.launch {
                        listener(
                            BandwidthResult(
                                countTestSpeed,
                                percent,
                                Bandwidth(
                                    report.transferRateBit
                                )
                            )
                        )
                    }
                }
            })

            speedTestSocket.startDownload(url)

            val result = deferredResult.await()
            Log.v("finished run $countTestSpeed", "")

            if (FinishState.FAILED.equals(result)) {
                throw IOException("speed test failed")
            }
        }
    }

    enum class FinishState {
        COMPLETED,
        FAILED
    }

}