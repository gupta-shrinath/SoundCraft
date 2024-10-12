package com.droid.soundcraft

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.droid.soundcraft.core.Recordings
import com.droid.soundcraft.ui.screens.HomeScreen
import com.droid.soundcraft.ui.screens.PlayBackScreen
import com.droid.soundcraft.ui.screens.RecordingScreen
import com.droid.soundcraft.ui.screens.Screens
import com.droid.soundcraft.ui.theme.SoundCraftTheme
import com.droid.soundcraft.ui.viewmodel.SoundCraftViewModel
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavController
    private val viewModel by viewModels<SoundCraftViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val audioRecordingPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    navController.navigate(Screens.Recording)
                } else {
                    Toast.makeText(
                        this,
                        "Mic permission required to record",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        val fileLoad =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                uri?.let {
                    val contentResolver = this.contentResolver
                    val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri)) ?: return@let
                    val file = File(this.cacheDir,"temp.$ext")
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    navController.navigate(Screens.Playback(file.absolutePath))
                }
            }
        enableEdgeToEdge()
        setContent {
            SoundCraftTheme {
                navController = rememberNavController()
                val context = LocalContext.current
                NavHost(
                    navController = navController as NavHostController,
                    startDestination = Screens.Home
                ) {
                    composable<Screens.Home> {
                        HomeScreen(
                            recordingFilesPath = Recordings.getAllRecordingFiles(context),
                            onRecordButtonClick = {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    navController.navigate(Screens.Recording)
                                } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.RECORD_AUDIO)) {
                                    Toast.makeText(
                                        context,
                                        "Mic permission required to record",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    audioRecordingPermission.launch(android.Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            navigateToPlayback = {
                                navController.navigate(Screens.Playback(it))
                            },
                            onFABClick = {
                                fileLoad.launch(arrayOf("audio/*"))
                            }
                        )
                    }
                    composable<Screens.Recording> {
                        val audioRecorder by remember {
                            mutableStateOf(viewModel.getAudioRecorder(context))
                        }
                        RecordingScreen(
                            audioRecorder = audioRecorder,
                            onBackPress = { navController.popBackStack() }
                        )
                    }

                    composable<Screens.Playback> {
                        val args = it.toRoute<Screens.Playback>()
                        val audioPlayer by remember {
                            mutableStateOf(viewModel.getAudioPlayer(args.filePath))
                        }
                        PlayBackScreen(
                            audioPlayer = audioPlayer,
                            onBackPress = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SoundCraftTheme {
        Greeting("Android")
    }
}