package com.example.memorymatch
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameActivity : AppCompatActivity() {

    private lateinit var cardGrid: GridLayout
    private lateinit var scoreText: TextView
    private lateinit var attemptsText: TextView
    private lateinit var statusText: TextView
    private lateinit var restartButton: Button

    private val buttons = mutableListOf<Button>()
    private val handler = Handler(Looper.getMainLooper())

    private var cards = intArrayOf()
    private var matched = BooleanArray(12)
    private var firstCard = -1
    private var secondCard = -1
    private var attemptsLeft = 20
    private var playerName = "Player"

    private val symbols = arrayOf("🍎", "🍋", "🍇", "🍓", "🍒", "🍉")

    private val hideCards = Runnable {
        firstCard = -1
        secondCard = -1
        updateScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        val toolbar = findViewById<Toolbar>(R.id.gameToolbar)
        toolbar.title = "Memory Match"

        val helpItem = toolbar.menu.add("Help")
        helpItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        toolbar.setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setTitle("How to Play")
                .setMessage(
                    "Tap two cards to reveal their fruits.\n\n" +
                            "Matching pairs stay visible. Different cards hide again.\n\n" +
                            "Find all 6 pairs before your attempts run out. " +
                            "Each pair you select uses one attempt.\n\n" +
                            "Tap Restart Game to begin again, or Share Score " +
                            "to share your progress."
                )
                .setPositiveButton("Got it") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        cardGrid = findViewById(R.id.cardGrid)
        scoreText = findViewById(R.id.scoreText)
        attemptsText = findViewById(R.id.attemptsText)
        statusText = findViewById(R.id.statusText)
        restartButton = findViewById(R.id.restartButton)

        playerName = intent.getStringExtra("PLAYER_NAME") ?: "Player"

        findViewById<TextView>(R.id.playerNameText).text =
            "$playerName's Game"
        findViewById<TextView>(R.id.playerNameText).setOnLongClickListener {
            if (!gameOver()) {
                attemptsLeft += 5
                updateScreen()
                statusText.text = "Secret unlocked! You gained 5 attempts!"
            }
            true
        }

        if (savedInstanceState != null) {
            cards = savedInstanceState.getIntArray("cards")
                ?: shuffledCards()
            matched = savedInstanceState.getBooleanArray("matched")
                ?: BooleanArray(12)
            firstCard = savedInstanceState.getInt("firstCard", -1)
            secondCard = savedInstanceState.getInt("secondCard", -1)
            attemptsLeft = savedInstanceState.getInt("attemptsLeft", 20)
        } else {
            cards = shuffledCards()
        }

        createButtons()
        updateScreen()

        if (secondCard != -1) {
            handler.postDelayed(hideCards, 1000)
        }

        restartButton.setOnClickListener {
            restartGame()
        }
        findViewById<Button>(R.id.shareScoreButton).setOnClickListener {
            val pairsFound = matched.count { it } / 2

            val result = when {
                matched.all { it } -> "I won!"
                attemptsLeft == 0 -> "Game over!"
                else -> "Game in progress!"
            }

            val message = "$playerName's Memory Match score: " +
                    "$pairsFound out of 6 pairs, " +
                    "$attemptsLeft attempts left. $result"

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            startActivity(Intent.createChooser(shareIntent, "Share your score"))
        }
    }

    private fun shuffledCards(): IntArray {
        return (0..5)
            .flatMap { listOf(it, it) }
            .shuffled()
            .toIntArray()
    }

    private fun createButtons() {
        cardGrid.removeAllViews()
        buttons.clear()

        for (index in cards.indices) {
            val button = Button(this)

            button.layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = dp(90)
                columnSpec = GridLayout.spec(index % 3, 1f)
                rowSpec = GridLayout.spec(index / 3)
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }

            button.textSize = 28f
            button.isAllCaps = false
            button.setPadding(0, 0, 0, 0)

            button.setOnClickListener {
                selectCard(index)
            }

            buttons.add(button)
            cardGrid.addView(button)
        }
    }

    private fun selectCard(index: Int) {
        // Ignore taps while a mismatched pair is still visible.
        if (secondCard != -1 || gameOver()) return

        // A matched card or the same card cannot be picked again.
        if (matched[index] || index == firstCard) return

        val tappedCard = buttons[index]
        tappedCard.animate().cancel()
        tappedCard.scaleX = 0.85f
        tappedCard.scaleY = 0.85f
        tappedCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .start()

        if (firstCard == -1) {
            firstCard = index
        } else {
            secondCard = index
            attemptsLeft--

            if (cards[firstCard] == cards[secondCard]) {
                matched[firstCard] = true
                matched[secondCard] = true
                firstCard = -1
                secondCard = -1
            } else {
                handler.postDelayed(hideCards, 1000)
            }
        }

        updateScreen()
    }

    private fun updateScreen() {
        val pairsFound = matched.count { it } / 2

        scoreText.text = "Matches: $pairsFound / 6"
        attemptsText.text = "Attempts left: $attemptsLeft"

        statusText.text = when {
            pairsFound == 6 ->
                "You won, $playerName! All pairs matched!"
            attemptsLeft == 0 ->
                "Game over! You found $pairsFound of 6 pairs."
            secondCard != -1 ->
                "Not a match. Remember these cards!"
            firstCard != -1 ->
                "Choose one more card."
            else ->
                "Tap two cards to find a match!"
        }

        restartButton.text =
            if (gameOver()) "Play Again" else "Restart Game"

        for (index in cards.indices) {
            val revealed = matched[index] ||
                    index == firstCard || index == secondCard

            val button = buttons[index]
            button.text = if (revealed) symbols[cards[index]] else "?"

            val background = when {
                matched[index] -> "#C8E6C9"
                revealed -> "#EADDFF"
                else -> "#6750A4"
            }

            button.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(background))

            button.setTextColor(
                if (revealed) Color.parseColor("#302A3B")
                else Color.WHITE
            )

            button.isEnabled = !gameOver() &&
                    !matched[index] &&
                    index != firstCard &&
                    secondCard == -1
        }
    }

    private fun gameOver(): Boolean {
        return matched.all { it } || attemptsLeft == 0
    }

    private fun restartGame() {
        handler.removeCallbacks(hideCards)
        cards = shuffledCards()
        matched = BooleanArray(12)
        firstCard = -1
        secondCard = -1
        attemptsLeft = 20
        updateScreen()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray("cards", cards)
        outState.putBooleanArray("matched", matched)
        outState.putInt("firstCard", firstCard)
        outState.putInt("secondCard", secondCard)
        outState.putInt("attemptsLeft", attemptsLeft)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        handler.removeCallbacks(hideCards)
        super.onDestroy()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}