package com.droid.soundcraft.core

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "AudioRecorder"

class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }

    private var isRecording = false

    init {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
    }

    fun start() {
        try {
            recorder.setOutputFile(getRecordingFilePath(context))
            recorder.prepare()
            recorder.start()
            isRecording = true
        } catch (e: IllegalStateException) {
            Log.d(TAG, "start failed due to ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Prepare called in init failed due to ${e.message}")
        }

    }

    fun pause() {
        try {
            recorder.pause()
            isRecording = false
        } catch (e: IllegalStateException) {
            Log.d(TAG, "pause failed due to ${e.message}")
        }
    }

    fun stop() {
        try {
            recorder.stop()
            isRecording = false
        } catch (e: IllegalStateException) {
            Log.d(TAG, "stop failed due to ${e.message}")
        }
    }

    fun release() {
        try {
            recorder.stop()
            recorder.release()
            isRecording = false
        } catch (e: IllegalStateException) {
            Log.d(TAG, "stop failed due to ${e.message}")
        }
    }

    fun getRecordingFilePath(context: Context): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "REC_$timestamp.3gp"
        val storageDir = context.getExternalFilesDir("Recordings")
        val file = File(storageDir, fileName)
        Log.d(TAG, "RecordingFilePath: ${file.absolutePath}")
        return file.absolutePath
    }
    val updateInterval = 100L // Update interval in milliseconds

    fun getIsRecording()  = callbackFlow {

        while (isActive) {
            try {
                send(isRecording) // Emit current position in milliseconds
            } catch (e: Exception) {
                Log.d(TAG, "getIsRecording: failed due to ${e.message}")
            }
            delay(updateInterval) // Check and emit at every interval
        }
        awaitClose { /* Cleanup if needed */ }
    }

    fun getAmplitudes() = callbackFlow<Int> {
        while (isActive) {
            try {
                if(isRecording) {
                    send(recorder.maxAmplitude) // Emit current position in milliseconds
                }
            } catch (e: Exception) {
                Log.d(TAG, "getIsRecording: failed due to ${e.message}")
            }
            delay(updateInterval) // Check and emit at every interval
        }
        awaitClose { /* Cleanup if needed */ }
    }
}