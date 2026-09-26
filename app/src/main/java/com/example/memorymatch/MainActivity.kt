package com.example.memorymatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val nameInput = findViewById<EditText>(R.id.playerNameInput)
        val startButton = findViewById<Button>(R.id.startGameButton)

        startButton.setOnClickListener {
            val playerName = nameInput.text.toString().trim()

            if (playerName.isEmpty()) {
                nameInput.error = "Please enter your name"
                nameInput.requestFocus()
            } else {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("PLAYER_NAME", playerName)
                startActivity(intent)
            }
        }
    }
}