package com.droid.soundcraft.ui.screens

import android.net.Uri
import kotlinx.serialization.Serializable

object Screens {

    @Serializable
    object Home

    @Serializable
    object Recording

    @Serializable
    data class Playback(val filePath: String)

}