package com.example.myapp


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
import android.os.Environment

class PlayerActivity : AppCompatActivity() {
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()
            }
        }
        requestPermessionLauncher.launch(READ_MEDIA_IMAGES)
        requestPermessionLauncher.launch(READ_MEDIA_AUDIO)
        requestPermessionLauncher.launch(READ_EXTERNAL_STORAGE)

        var musicPath: String = Environment.getExternalStorageDirectory().path + "/Music/"
        Toast.makeText(this,musicPath, Toast.LENGTH_LONG).show()

        var mediaPlayer : MediaPlayer = MediaPlayer()
        val nextButton: Button = findViewById(R.id.NextButton)
        val backButton: Button = findViewById(R.id.BackButton)
        val playButton: Button = findViewById(R.id.PlayButton)

        val seekBar: SeekBar = findViewById(R.id.SeekBar)
        var updateTimer: Timer? = null






        mediaPlayer.setDataSource(musicPath+"Music.mp3")
        mediaPlayer.prepare()

        seekBar.max = mediaPlayer.duration

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

}