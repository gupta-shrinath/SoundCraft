package com.droid.soundcraft.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.droid.soundcraft.core.AudioPlayer
import com.droid.soundcraft.core.AudioRecorder

const val TAG = "SoundCraftViewModel"

class SoundCraftViewModel : ViewModel() {

    fun getAudioRecorder(context: Context): AudioRecorder {
        Log.d(TAG, "getAudioRecorder: ")
        return AudioRecorder(context)
    }

    fun getAudioPlayer(filePath: String): AudioPlayer {
        Log.d(TAG, "getAudioPlayer: ")
        return AudioPlayer(filePath)
    }
}