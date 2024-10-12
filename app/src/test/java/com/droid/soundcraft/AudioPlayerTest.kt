import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import com.droid.soundcraft.core.AudioPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.ShadowMediaPlayer.MediaInfo
import org.robolectric.shadows.util.DataSource
import java.io.File
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AudioPlayerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockMediaPlayer = mock<MediaPlayer>()
    private val filePath = "test_file_path.mp3"
    private lateinit var audioPlayer: AudioPlayer

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Create a fake file path as media source
        val file = File(filePath)
        file.createNewFile() // Ensure the file exists for Robolectric to use


        ShadowMediaPlayer.setMediaInfoProvider { dataSource: DataSource? ->
            MediaInfo(
                1,
                0
            )
        }

        audioPlayer = AudioPlayer(filePath)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should setDataSource and prepare mediaPlayer`() {
        verify(mockMediaPlayer).setDataSource(filePath)
        verify(mockMediaPlayer).prepare()
    }

    @Test
    fun `start should call start on mediaPlayer`() {
        audioPlayer.start()
        verify(mockMediaPlayer).start()
    }

    @Test
    fun `pause should call pause on mediaPlayer`() {
        audioPlayer.pause()
        verify(mockMediaPlayer).pause()
    }

    @Test
    fun `stop should call stop on mediaPlayer`() {
        audioPlayer.stop()
        verify(mockMediaPlayer).stop()
    }

    @Test
    fun `release should call release on mediaPlayer`() {
        audioPlayer.release()
        verify(mockMediaPlayer).release()
    }

    @Test
    fun `getDuration should return mediaPlayer duration`() {
        whenever(mockMediaPlayer.duration).thenReturn(5000)
        assertEquals(5000, audioPlayer.getDuration())
    }

    @Test
    fun `getCurrentPosition should emit current position`() = runTest {
        whenever(mockMediaPlayer.isPlaying).thenReturn(true)
        whenever(mockMediaPlayer.currentPosition).thenReturn(1500)

        val currentPosition = audioPlayer.getCurrentPosition().first()
        assertEquals(1500, currentPosition)
    }

    @Test
    fun `getIsPlaying should emit isPlaying status`() = runTest {
        whenever(mockMediaPlayer.isPlaying).thenReturn(true)

        val isPlaying = audioPlayer.getIsPlaying().first()
        assertEquals(true, isPlaying)
    }

    @Test
    fun `getAmplitudes should emit amplitude list when mediaPlayer is playing`() = runTest {
        // Mock MediaPlayer to be in playing state
        whenever(mockMediaPlayer.isPlaying).thenReturn(true)
        // Mock Visualizer to return some waveform data
        val mockVisualizer = mock<Visualizer>()
        val amplitudeFlow = audioPlayer.getAmplitudes()

        // Test first emitted amplitude
        assertEquals(listOf(128, 130, 132), amplitudeFlow.first())
    }

    @Test
    fun `getProgress should emit current progress as a percentage`() = runTest {
        whenever(mockMediaPlayer.isPlaying).thenReturn(true)
        whenever(mockMediaPlayer.currentPosition).thenReturn(2000)
        whenever(mockMediaPlayer.duration).thenReturn(4000)

        val progress = audioPlayer.getProgress().first()
        assertEquals(50f, progress)
    }

    @Test
    fun `start should log error when IllegalStateException is thrown`() {
        whenever(mockMediaPlayer.start()).thenThrow(IllegalStateException("test exception"))

        audioPlayer.start()

        // Verify that the log message is printed. You could also use LogcatTestRule for Android.
    }

    @Test
    fun `stop should log error when IllegalStateException is thrown`() {
        whenever(mockMediaPlayer.stop()).thenThrow(IllegalStateException("test exception"))

        audioPlayer.stop()

        // Verify that the log message is printed. You could also use LogcatTestRule for Android.
    }
}
