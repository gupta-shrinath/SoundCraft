package com.droid.soundcraft.core

import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import java.io.IOException

private const val TAG = "AudioPlayer"

class AudioPlayer(filePath: String) {

    private val mediaPlayer = MediaPlayer()

    init {
        mediaPlayer.apply {
            try {
                setDataSource(filePath)
                prepare()
            } catch (e: IllegalStateException) {
                Log.d(TAG, "Prepare called in init failed due to ${e.message}")
            } catch (e: IOException) {
                Log.d(TAG, "Prepare called in init failed due to ${e.message}")
            }
        }
    }

    fun start() {
        try {
            mediaPlayer.start()
        } catch (e: java.lang.IllegalStateException) {
            Log.d(TAG, "start failed due to ${e.message}")
        }
    }

    fun pause() {
        try {
            mediaPlayer.pause()
        } catch (e: java.lang.IllegalStateException) {
            Log.d(TAG, "pause failed due to ${e.message}")
        }
    }

    fun stop() {
        try {
            mediaPlayer.stop()
        } catch (e: IllegalStateException) {
            Log.d(TAG, "stop failed due to ${e.message}")
        }
    }

    fun release() {
        mediaPlayer.release()
    }

    fun getDuration():Int  {
        try {
            return mediaPlayer.duration
        } catch (e:Exception) {

        }
        return 0
    }

    fun getCurrentPosition() = callbackFlow {
        val updateInterval = 100L // Update interval in milliseconds

        // Start a coroutine to emit current position while mediaPlayer is playing
        while (isActive) {
            try {
                if (mediaPlayer.isPlaying) {
                    send(mediaPlayer.currentPosition) // Emit current position in milliseconds
                }
            } catch (e: Exception) {
            }
            delay(updateInterval) // Check and emit at every interval
        }
        awaitClose { /* Cleanup if needed */ }
    }

    val updateInterval = 100L // Update interval in milliseconds

    fun getIsPlaying() = callbackFlow {

        // Start a coroutine to emit current position while mediaPlayer is playing
        while (isActive) {
            try {
                send(mediaPlayer.isPlaying) // Emit current position in milliseconds
            } catch (e: Exception) {
            }
            delay(updateInterval) // Check and emit at every interval
        }
        awaitClose { /* Cleanup if needed */ }
    }

    fun getAmplitudes(updateInterval: Long = 100L) = callbackFlow<List<Int>> {
        val audioSessionId = mediaPlayer.audioSessionId
        if (audioSessionId == Visualizer.ERROR_BAD_VALUE) {
            close(IllegalStateException("Invalid audio session ID"))
            return@callbackFlow
        }

        val visualizer = Visualizer(audioSessionId).apply {
            captureSize = Visualizer.getCaptureSizeRange()[1]
            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    visualizer: Visualizer,
                    waveform: ByteArray,
                    samplingRate: Int
                ) {
                    val amplitudes = waveform.map { it.toInt() and 0xFF }
                    try {


                        if (mediaPlayer.isPlaying) {
                            Log.d(TAG, "onWaveFormDataCapture: Sent ${amplitudes.size}")
                            trySend(amplitudes)
                        }
                    } catch (e: IllegalStateException) {
                        Log.d(TAG, "getAmplitudes failed due to ${e.message}")
                    }
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer,
                    fft: ByteArray,
                    samplingRate: Int
                ) {
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false)

            enabled = true // Enable after setting the listener
        }

        while (isActive) {
            try {
                if (mediaPlayer.isPlaying) {
                    delay(updateInterval)
                }
            } catch (e: IllegalStateException) {
                Log.d(TAG, "getAmplitudes failed due to ${e.message}")
            }

        }

        awaitClose {
            visualizer.release()
        }
    }

    fun getProgress() = callbackFlow {
        while (isActive) {
            mediaPlayer.setOnCompletionListener {
                trySend(100f)
            }
            try {
                if (mediaPlayer.isPlaying) {
                    Log.d(
                        TAG,
                        "getProgress: ${(mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()) * 100}"
                    )
                    send(((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat())) * 100)
                }

            } catch (e: Exception) {
            }
            delay(updateInterval)

        }
        awaitClose{}
    }
}

fun Int.formatMillisecondsToHMS(): String {
    val hours = (this / (1000 * 60 * 60)) % 24
    val minutes = (this / (1000 * 60)) % 60
    val seconds = (this / 1000) % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
