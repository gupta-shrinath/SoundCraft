package com.droid.soundcraft.core

import android.content.Context
import java.io.File

object Recordings {

    fun getAllRecordingFiles(context: Context): List<File> {
        val directory = context.getExternalFilesDir("Recordings")
        return directory?.listFiles()?.toList() ?: emptyList()
    }
}