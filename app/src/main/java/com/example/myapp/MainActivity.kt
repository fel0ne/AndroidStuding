package com.example.myapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
//import kotlin.jvm.java


class MainActivity : AppCompatActivity() {
    lateinit var playerButton : Button
    lateinit var calcButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playerButton  = findViewById(R.id.PlayerButton)
        calcButton = findViewById(R.id.CalcButton)






    }
    override fun onResume(){
        super.onResume()
        calcButton.setOnClickListener {
            val CalcIntent = Intent(this,CalcActivity::class.java)
            startActivity(CalcIntent)
        }
        playerButton.setOnClickListener {
            val PlayerIntent = Intent(this,PlayerActivity::class.java)
            startActivity(PlayerIntent)
        }
   }
}