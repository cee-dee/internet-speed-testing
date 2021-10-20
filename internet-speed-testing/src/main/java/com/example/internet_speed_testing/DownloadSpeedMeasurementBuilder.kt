package com.example.internet_speed_testing

import java.math.BigDecimal

typealias BandwidthListener = suspend (BandwidthResult) -> Unit

data class BandwidthResult(
    val repetition: Int,
    val percentage: Float,
    val bandwidth: Bandwidth
)

data class Bandwidth(
    val bitsPerSecond: BigDecimal
)

class DownloadSpeedMeasurementBuilder() {

    private var url: String? = null
    private var repetitions = 3
    private var listener: BandwidthListener? = null

    fun withUrl(url: String): DownloadSpeedMeasurementBuilder {
        this.url = url
        return this
    }

    fun withRepetitionCount(repetitions: Int): DownloadSpeedMeasurementBuilder {
        this.repetitions = repetitions
        return this
    }

    fun withProgressListener(listener: BandwidthListener): DownloadSpeedMeasurementBuilder {
        this.listener = listener
        return this
    }

    fun build(): DownloadSpeedMeasurement {
        url?.let { _url ->
            listener?.let { _listener ->
                return DownloadSpeedMeasurement(
                    _url,
                    repetitions,
                    _listener
                )
            }

        } ?: throw IllegalStateException("url has to be set for testing download speed")

    }

}
