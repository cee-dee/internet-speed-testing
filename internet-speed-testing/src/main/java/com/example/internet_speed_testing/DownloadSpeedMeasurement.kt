package com.example.internet_speed_testing

import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadSpeedMeasurement(
    private val url: String,
    private val measurementListener: MeasurementListener
) {

    suspend fun run(): FinalMeasurementState {

        return withContext(Dispatchers.IO) {

            val currentBandwidth: MutableStateFlow<BandwidthResult?> = MutableStateFlow(null)
            val deferredResult = CompletableDeferred<FinalMeasurementState>()

            val speedTestSocket = createSpeedTest(currentBandwidth, deferredResult)
            speedTestSocket.startDownload(url)

            deferredResult.await()
        }

    }

    private fun CoroutineScope.createSpeedTest(
        currentBandwidth: MutableStateFlow<BandwidthResult?>,
        deferredResult: CompletableDeferred<FinalMeasurementState>
    ): SpeedTestSocket {

        val scope = this
        return SpeedTestSocket().apply {

            addSpeedTestListener(object : ISpeedTestListener {

                override fun onCompletion(report: SpeedTestReport) {
                    deferredResult.complete(
                        FinalMeasurementState.FinishedSuccessfully(
                            overallBandwidth = currentBandwidth.value
                        )
                    )
                }

                override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                    deferredResult.complete(
                        FinalMeasurementState.FinishedWithError(
                            overallBandwidth = currentBandwidth.value
                        )
                    )
                }

                override fun onProgress(percent: Float, report: SpeedTestReport) {
                    scope.launch {
                        currentBandwidth.value = reportIntermediate(percent, report)
                    }
                }
            })
        }
    }

    private suspend fun reportIntermediate(
        percent: Float,
        report: SpeedTestReport
    ): BandwidthResult {
        val bandwidthResult = BandwidthResult(
            percent,
            Bandwidth(
                report.transferRateBit
            )
        )
        measurementListener(
            IntermediateMeasurementState(
                overallBandwidth = bandwidthResult
            )
        )

        return bandwidthResult
    }
}