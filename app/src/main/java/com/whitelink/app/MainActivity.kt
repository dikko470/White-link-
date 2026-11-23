package com.whitelink.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "WHITE LINK"
            textSize = 32f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Sadarwa ba tare da Internet ba"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        val message = EditText(this).apply {
            hint = "Rubuta saƙonka..."
        }

        val send = Button(this).apply {
            text = "Aika saƙo"
            setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "An rubuta saƙo: ${message.text}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(message)
        layout.addView(send)

        setContentView(layout)
    }
}￼Enter
