package com.example.internet_speed_testing

import java.math.BigDecimal

typealias MeasurementListener = suspend (IntermediateMeasurementState) -> Unit

data class IntermediateMeasurementState(
    val overallBandwidth: BandwidthResult
)

sealed class FinalMeasurementState(
    open val overallBandwidth: BandwidthResult?
) {

    data class FinishedSuccessfully(
        override val overallBandwidth: BandwidthResult?
    ) : FinalMeasurementState(overallBandwidth)

    data class FinishedWithError(
        override val overallBandwidth: BandwidthResult?
    ) : FinalMeasurementState(overallBandwidth)
}

data class BandwidthResult(
    val percentage: Float,
    val bandwidth: Bandwidth
)

data class Bandwidth(
    val bitsPerSecond: BigDecimal
)

class DownloadSpeedMeasurementBuilder {

    private var url: String? = null
    private var measurementListener: MeasurementListener? = null

    fun withUrl(url: String): DownloadSpeedMeasurementBuilder {
        this.url = url
        return this
    }

    fun withProgressListener(listener: MeasurementListener): DownloadSpeedMeasurementBuilder {
        this.measurementListener = listener
        return this
    }

    fun build(): DownloadSpeedMeasurement {
        url?.let { _url ->
            measurementListener?.let { _listener ->
                return DownloadSpeedMeasurement(
                    _url,
                    _listener
                )
            }

        } ?: throw IllegalStateException("url has to be set for testing download speed")
    }
}
