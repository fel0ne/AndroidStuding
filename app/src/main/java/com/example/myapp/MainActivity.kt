package com.example.myapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp.R
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var tv : TextView = findViewById(R.id.textView)
        var b1: Button = findViewById(R.id.button)
        var b2: Button = findViewById(R.id.button2)
        var b3: Button = findViewById(R.id.button3)
        var b4: Button = findViewById(R.id.button4)
        var b5: Button = findViewById(R.id.button5)
        var b6: Button = findViewById(R.id.button6)
        var b7: Button = findViewById(R.id.button7)
        var b8: Button = findViewById(R.id.button8)
        var b9: Button = findViewById(R.id.button9)
        var b10: Button = findViewById(R.id.button10)
        var b11: Button = findViewById(R.id.button11)
        var b12: Button = findViewById(R.id.button12)
        var b13: Button = findViewById(R.id.button13)
        var b14: Button = findViewById(R.id.button14)
        var b15: Button = findViewById(R.id.button15)
        var b16: Button = findViewById(R.id.button16)


        var oper : Int = 0
        var flag : Int = 0
        var temp : Double = 0.0
        var strA : String = ""
        var strB : String = ""
        b1.setOnClickListener {
                if (flag == 0){
                    strA += "1"
                    tv.setText(strA)
                }
                else{
                    strB += "1"
                    tv.setText(strB)
                }
        }

        b2.setOnClickListener {
            if (flag == 0){
                strA += "2"
                tv.setText(strA)
            }
            else{
                strB += "2"
                tv.setText(strB)
            }
        }

        b3.setOnClickListener {
            if (flag == 0){
                strA += "3"
                tv.setText(strA)
            }
            else{
                strB += "3"
                tv.setText(strB)
            }
        }

        b4.setOnClickListener {
            if (flag == 0){
                strA += "4"
                tv.setText(strA)
            }
            else{
                strB += "4"
                tv.setText(strB)
            }
        }

        b5.setOnClickListener {
            if (flag == 0){
                strA += "5"
                tv.setText(strA)
            }
            else{
                strB += "5"
                tv.setText(strB)
            }
        }

        b6.setOnClickListener {
            if (flag == 0){
                strA += "6"
                tv.setText(strA)
            }
            else{
                strB += "6"
                tv.setText(strB)
            }
        }

        b7.setOnClickListener {
            if (flag == 0){
                strA += "7"
                tv.setText(strA)
            }
            else{
                strB += "7"
                tv.setText(strB)
            }
        }

        b8.setOnClickListener {
            if (flag == 0){
                strA += "8"
                tv.setText(strA)
            }
            else{
                strB += "8"
                tv.setText(strB)
            }
        }

        b9.setOnClickListener {
            if (flag == 0){
                strA += "9"
                tv.setText(strA)
            }
            else{
                strB += "9"
                tv.setText(strB)
            }
        }

        b10.setOnClickListener {
            if (flag == 0){
                strA += "0"
                tv.setText(strA)
            }
            else{
                strB += "0"
                tv.setText(strB)
            }
        }

        b11.setOnClickListener {
            flag = 1
            tv.setText(strB)
            oper = 1
        }

        b12.setOnClickListener {
            flag = 1
            tv.setText(strB)
            oper =2
        }

        b15.setOnClickListener {
            flag = 1
            tv.setText(strB)
            oper =3
        }

        b16.setOnClickListener {
            flag = 1
            tv.setText(strB)
            oper =4
        }

        b13.setOnClickListener {
            if (strA == "" && strB == "") {
                tv.setText("0")
            } else if (strA == "") {
                tv.setText(strB)
            } else if (strB == "") {
                tv.setText(strA)
            } else {
                if (oper == 1) {
                    temp = strA.toDouble() + strB.toDouble()
                }
                else if(oper == 2){
                    temp = strA.toDouble() - strB.toDouble()
                }
                else if(oper == 3){
                    temp = strA.toDouble() * strB.toDouble()
                }
                else if(oper == 4 && (strB != "" || strB == "0" || strA != "" || strA == "0")){
                    temp = strA.toDouble() / strB.toDouble()
                }
                else{
                    temp = 0.0
                }
                tv.setText(temp.toString())

            }
            strA = temp.toString()
            strB = ""
        }

        b14.setOnClickListener {
            strA = ""
            strB = ""
            flag = 0
            tv.setText("")
        }






    }
}