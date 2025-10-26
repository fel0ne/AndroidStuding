package com.example.myapp


import android.Manifest
import android.media.MediaPlayer
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
import android.content.pm.PackageManager
import android.os.Environment
import java.io.File


class PlayerActivity : AppCompatActivity() {
    lateinit var musicPath: String
    lateinit var mediaPlayer: MediaPlayer
    lateinit var nextButton: Button
    lateinit var backButton: Button
    lateinit var playButton: Button
    lateinit var seekBar: SeekBar

    var musicFiles: List<File> = emptyList()
    var currentTrackIndex: Int = 0


    var updateTimer: Timer? = null
    fun stopUpdatingSeekBar() {
        updateTimer?.cancel()
        updateTimer = null
    }
    fun startUpdatingSeekBar() {
        stopUpdatingSeekBar()

        updateTimer = timer(period = 500) {
            if (mediaPlayer.isPlaying) {
                runOnUiThread {
                    seekBar.progress = mediaPlayer.currentPosition
                }
            }
        }
    }


    private fun loadTrack() {
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

            mediaPlayer.start()
            mediaPlayer.pause()
            seekBar.max = mediaPlayer.duration

            Toast.makeText(this, "Playing: ${trackFile.name}", Toast.LENGTH_SHORT).show()

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

        requestPermessionLauncher.launch(READ_MEDIA_IMAGES)
        requestPermessionLauncher.launch(READ_MEDIA_AUDIO)
        requestPermessionLauncher.launch(READ_EXTERNAL_STORAGE)

        musicPath = Environment.getExternalStorageDirectory().path + "/Music/"
        //Toast.makeText(this,musicPath, Toast.LENGTH_LONG).show()

        mediaPlayer = MediaPlayer()
        nextButton = findViewById(R.id.NextButton)
        backButton = findViewById(R.id.BackButton)
        playButton = findViewById(R.id.PlayButton)

        seekBar = findViewById(R.id.SeekBar)

        val musicDirectory = File(musicPath)
        val foundFiles = mutableListOf<File>()
        if (musicDirectory.exists() && musicDirectory.isDirectory) {
            val allFiles = musicDirectory.listFiles()


            if (allFiles != null) {
                for (file in allFiles) {

                    if (file.isFile && (file.extension ==  "mp3")) {
                        foundFiles.add(file) //
                    }
                }
            }
        }

        musicFiles = foundFiles.toList()
        Toast.makeText(this, "Files found: ${musicFiles.size}", Toast.LENGTH_LONG).show()

        if (musicFiles.isNotEmpty()) {
            loadTrack()
        } else {
            Toast.makeText(this, "No MP3 files found in $musicPath", Toast.LENGTH_LONG).show()
            playButton.isEnabled = false
        }

        nextButton.setOnClickListener {

            currentTrackIndex =currentTrackIndex + 1

            loadTrack()
            playButton.setText("Play")
        }

        backButton.setOnClickListener {
            currentTrackIndex = currentTrackIndex - 1

            loadTrack()
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
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {  }
        }


        seekBar.setOnSeekBarChangeListener(MySeekBarListener())


    }



    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
        playButton.setText("Play")
        stopUpdatingSeekBar()
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