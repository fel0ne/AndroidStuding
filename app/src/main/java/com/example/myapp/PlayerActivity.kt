package com.example.myapp


import android.media.MediaPlayer
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
//import android.R.attr.delay
//import kotlin.concurrent.thread
import kotlin.concurrent.timer
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import java.io.File


class PlayerActivity : AppCompatActivity() {
    lateinit var musicPath: String
    lateinit var mediaPlayer: MediaPlayer

    lateinit var retriver : MediaMetadataRetriever
    lateinit var nextButton: Button
    lateinit var backButton: Button
    lateinit var playButton: Button
    lateinit var seekBar: SeekBar


    lateinit var songTitle: TextView

    lateinit var songAuthor: TextView

    lateinit var currentDur: TextView

    lateinit var  maxDur : TextView

    lateinit var loopBtn : Switch

    lateinit var autoPause_btn : Switch

    lateinit var sortBtn : Switch


    lateinit var trackListView : ListView

    lateinit var volumeSeekBar: SeekBar

    var isLooped : Int = 0

    var isAutoPaused : Int = 0

    var musicFiles: List<File> = emptyList()
    var currentTrackIndex: Int = 0

    var updateTimer: Timer? = null
    fun stopUpdatingSeekBar() {
        updateTimer?.cancel()
        updateTimer = null
    }
    fun startUpdatingSeekBar() {
        stopUpdatingSeekBar()

        updateTimer = timer(period = 100) {
            if (mediaPlayer.isPlaying) {
                runOnUiThread {
                    seekBar.progress = mediaPlayer.currentPosition
                    val totalSeconds = mediaPlayer.currentPosition / 1000
                    val minutes = totalSeconds / 60
                    val seconds = totalSeconds % 60


                    if (seconds < 10) {
                        currentDur.setText(minutes.toString() + ":0" + seconds.toString())
                    } else {
                        currentDur.setText(minutes.toString() + ":" + seconds.toString())
                    }
                }
            }
        }
    }


    private fun loadTrack(play_flag: Int) {
        if (musicFiles.isEmpty()) {
            Toast.makeText(this, "No music files found!", Toast.LENGTH_LONG).show()
            playButton.isEnabled = false
            return
        }


        if (currentTrackIndex < 0){
            currentTrackIndex = 0
        }
        if (currentTrackIndex > musicFiles.size-1){
            currentTrackIndex = musicFiles.size-1
        }
        val trackFile = musicFiles[currentTrackIndex]

        try {

            mediaPlayer.reset()
            mediaPlayer.setDataSource(trackFile.absolutePath)


            mediaPlayer.prepare()
            retriver.setDataSource(trackFile.absolutePath)



            var title = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            var author = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR)
            var artist =  retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            if (author != null && author != "") {
                songAuthor.text = author
            } else if (artist != null && artist != "") {
                songAuthor.text = artist
            } else {
                songAuthor.text = "Unknown artist"
            }

            if (title != null && title != "") {
                songTitle.text = title
            } else {
                songTitle.text = trackFile.nameWithoutExtension
            }


            mediaPlayer.start()
            mediaPlayer.pause()
            Toast.makeText(this, "Duration: ${mediaPlayer.duration}", Toast.LENGTH_SHORT).show()


            seekBar.max = mediaPlayer.duration
            val totalSeconds = mediaPlayer.duration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            if (seconds < 10) {
                maxDur.setText(minutes.toString() + ":0" + seconds.toString())
            } else {
                maxDur.setText(minutes.toString() + ":" + seconds.toString())
            }
            if (play_flag == 1){
                mediaPlayer.start()
                playButton.setText("Pause")

                startUpdatingSeekBar()
            }


        } catch (e: Exception) {
            Toast.makeText(this, "Error loading track: ${trackFile.name}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            playButton.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val requestPermessionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
               // Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
               // Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()
            }
        }
//----------------------------------------------------------------------------------------------
        requestPermessionLauncher.launch(READ_MEDIA_IMAGES)
        requestPermessionLauncher.launch(READ_MEDIA_AUDIO)
        requestPermessionLauncher.launch(READ_EXTERNAL_STORAGE)

        musicPath = Environment.getExternalStorageDirectory().path + "/Music/"
        //Toast.makeText(this,musicPath, Toast.LENGTH_LONG).show()

        mediaPlayer = MediaPlayer()
        retriver = MediaMetadataRetriever()
        nextButton = findViewById(R.id.NextButton)
        backButton = findViewById(R.id.BackButton)
        playButton = findViewById(R.id.PlayButton)

        seekBar = findViewById(R.id.SeekBar)
        volumeSeekBar = findViewById(R.id.VolumeSeekBar)




        songTitle = findViewById(R.id.SongTitle)
        songAuthor = findViewById(R.id.SongAuthor)

        currentDur = findViewById(R.id.currentDur)
        maxDur = findViewById(R.id.maxDur)


        loopBtn = findViewById(R.id.Loop_btn)
        autoPause_btn = findViewById(R.id.AutoPause_btn)
        sortBtn = findViewById(R.id.SortBtn)

        trackListView = findViewById(R.id.TrackListView)
        val musicDirectory = File(musicPath)
        val foundFiles = mutableListOf<File>()
        if (musicDirectory.exists() && musicDirectory.isDirectory) {
            val allFiles = musicDirectory.listFiles()


            if (allFiles != null) {
                for (file in allFiles) {

                    if (file.isFile && (file.extension ==  "mp3" || file.extension ==  "flac")) {
                        foundFiles.add(file)
                    }
                }
            }
        }

        musicFiles = foundFiles.toList()



        val trackList = mutableListOf<String>()

        for(track in musicFiles){
            trackList.add(track.nameWithoutExtension)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, trackList)
        trackListView.adapter = adapter
            trackListView.setOnItemClickListener { parent, view, position, id ->
                currentTrackIndex = position
                //Toast.makeText(this, "Playing: $selectedTrack", Toast.LENGTH_SHORT).show()
                loadTrack(1)
        }

        sortBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            trackList.clear()
            if (isChecked) {

                musicFiles = foundFiles.sortedBy { it.nameWithoutExtension }.toList()
                for(track in musicFiles){
                    trackList.add(track.nameWithoutExtension)
                }

            } else {

                musicFiles = foundFiles.toList()
                for(track in musicFiles){
                    trackList.add(track.nameWithoutExtension)
                }
            }
            adapter.notifyDataSetChanged()
        }

        Toast.makeText(this, "Files found: ${musicFiles.size}", Toast.LENGTH_LONG).show()

        if (musicFiles.isNotEmpty()) {
            loadTrack(0)
        } else {
            Toast.makeText(this, "No MP3 files found in $musicPath", Toast.LENGTH_LONG).show()
            playButton.isEnabled = false
        }

        nextButton.setOnClickListener {

            currentTrackIndex =currentTrackIndex + 1

            loadTrack(0)
            playButton.setText("Play")
        }

        backButton.setOnClickListener {
            currentTrackIndex = currentTrackIndex - 1

            loadTrack(0)
            playButton.setText("Play")
        }


        playButton.setOnClickListener {
            if(playButton.text == "Play") {
                mediaPlayer.start()
                playButton.setText("Pause")


                startUpdatingSeekBar()

            }
            else{
                mediaPlayer.pause()
                playButton.setText("Play")
                stopUpdatingSeekBar()
            }
        }
        class MySeekBarListener : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if(playButton.text == "Pause") {
                    mediaPlayer.pause()
                    playButton.setText("Play")

                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if(playButton.text == "Play") {
                    mediaPlayer.start()
                    playButton.setText("Pause")
                    startUpdatingSeekBar()



                }
            }
        }



        volumeSeekBar.max = 100
        volumeSeekBar.setProgress(50)
        mediaPlayer.setVolume(0.5f,0.5f)
        class VolumeSeekBarListener : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val floatProgress: Float = progress.toFloat()/100
                    mediaPlayer.setVolume(floatProgress,floatProgress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        }
        volumeSeekBar.setOnSeekBarChangeListener(VolumeSeekBarListener())


        autoPause_btn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isAutoPaused = 1
            } else {
                isAutoPaused = 0
            }
        }

        loopBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isLooped = 1
            } else {
                isLooped = 0
            }
        }




        seekBar.setOnSeekBarChangeListener(MySeekBarListener())

        mediaPlayer.setOnCompletionListener {
            if(isLooped == 0) {
                currentTrackIndex++
            }
            if (currentTrackIndex >= musicFiles.size) {
                currentTrackIndex = 0
            }
            loadTrack(1)
        }


    }



    override fun onPause() {
        super.onPause()
        if (isAutoPaused == 1){
            mediaPlayer.pause()
            playButton.setText("Play")
            stopUpdatingSeekBar()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingSeekBar()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

}