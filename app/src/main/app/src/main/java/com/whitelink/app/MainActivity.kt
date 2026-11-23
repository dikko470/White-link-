package com.whitelink.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(24, 24, 24, 24)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "WHITE LINK"
        title.textSize = 32f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "Sadarwa ba tare da Internet ba"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.DKGRAY)
        subtitle.gravity = Gravity.CENTER

        val connect = Button(this)
        connect.text = "Haɗa da waya kusa da kai"

        val message = EditText(this)
        message.hint = "Rubuta saƙonka..."

        val send = Button(this)
        send.text = "Aika saƙo"

        val status = TextView(this)
        status.text = "Ba a haɗa da wata waya ba."
        status.textSize = 14f
        status.setTextColor(Color.GRAY)

        connect.setOnClickListener {
            status.text = "Za mu ƙara tsarin haɗin waya a mataki na gaba."
        }

        send.setOnClickListener {
            status.text = "An shirya saƙonka: ${message.text}"
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(connect)
        layout.addView(message)
        layout.addView(send)
        layout.addView(status)

        setContentView(layout)
    }
}￼Enter
