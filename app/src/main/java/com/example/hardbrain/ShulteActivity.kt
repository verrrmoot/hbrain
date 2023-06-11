package com.example.hardbrain

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ShulteActivity : AppCompatActivity() {
    private lateinit var timerText: TextView
    private lateinit var numberText: TextView
    private lateinit var buttonGrid: Array<Array<Button>>
    private lateinit var startTime: Date
    private var currentNumber: Int = 1
    private var gameStarted: Boolean = false
    private var gameMode: Int = 3 // 1 - Классическая таблица, 2 - Буквенная таблица, 3 - Таблица с перемешиванием
    private var handler: Handler = Handler()
    private var updateTimeRunnable: Runnable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shulte)

        timerText = findViewById(R.id.timerText)
        numberText = findViewById(R.id.numberText)

        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            startGame()
        }

        val modeButton: Button = findViewById(R.id.modeButton)
        modeButton.setOnClickListener {
            showNextMode()
            setNumberText(currentNumber)
        }

        buttonGrid = Array(5) { row ->
            Array(5) { column ->
                val buttonId = resources.getIdentifier("button$row$column", "id", packageName)
                findViewById<Button>(buttonId).apply {
                    setOnClickListener {
                        onButtonClicked(row, column)
                    }
                }
            }
        }

        setGameModeText()
        showNextMode()
        setTimerText(0)

    }

    private fun startGame() {
        if (gameStarted) return

        gameStarted = true
        currentNumber = 1
        setNumberText(currentNumber)
        shuffleButtons()
        startTime = Date()

        updateTimeRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = Date().time - startTime.time
                setTimerText(elapsedTime)

                if (currentNumber > 25) {
                    gameStarted = false
                    handler.removeCallbacks(this)
                    showGameFinished()
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        }

        handler.postDelayed(updateTimeRunnable as Runnable, 1000)
    }


    private fun shuffleButtons() {
        val numbers = mutableListOf<Int>()
        when (gameMode) {
            1 -> numbers.addAll(1..25)
            2 -> numbers.addAll('A'.code..'Y'.code)
            3 -> { numbers.addAll(1..25)

            }
        }

        numbers.shuffle()

        for (row in 0 until 5) {
            for (column in 0 until 5) {
                val number = numbers[row * 5 + column]
                val button = buttonGrid[row][column]
                button.text = getButtonText(number)
                button.isEnabled = true
            }
        }
    }

    private fun shuffleEnabledButtons() {
        val enabledButtons = buttonGrid.flatten().filter { it.isEnabled }

        val enabledButtonTexts = enabledButtons.map { it.text.toString() } as MutableList
        enabledButtonTexts.shuffle()

        for ((index, button) in enabledButtons.withIndex()) {
            button.text = enabledButtonTexts[index]
        }
    }


    private fun onButtonClicked(row: Int, column: Int) {
        val button = buttonGrid[row][column]
        val buttonNumber = getButtonNumber(button.text.toString())
        if (buttonNumber == currentNumber) {
            button.isEnabled = false
            currentNumber++

            if (currentNumber > 25) {
                gameStarted = false
                handler.removeCallbacksAndMessages(null)
                showGameFinished()
            } else {
                if (gameMode == 3) {shuffleEnabledButtons()}
                setNumberText(currentNumber)
            }
        }
    }

    private fun showNextMode() {
        gameMode = (gameMode % 3) + 1
        setGameModeText()
        resetGame()

    }

    private fun setGameModeText() {
        val modeText: TextView = findViewById(R.id.modeText)
        val modeName = when (gameMode) {
            1 -> "Классическая таблица"
            2 -> "Буквенная таблица"
            3 -> "Таблица с перемешиванием"
            else -> ""
        }
        modeText.text = "Режим: $modeName"
    }

    private fun setTimerText(elapsedTime: Long) {
        val seconds = elapsedTime / 1000
        timerText.text = "Время: $seconds сек"
    }

    private fun setNumberText(number: Int) {
        val num = (number + 'A'.code - 1).toChar().toString()
        return when (gameMode) {
            1 -> numberText.text = "Текущее число: $number"
            2 -> numberText.text = "Текущий символ: $num"
            3 -> numberText.text = "Текущее число: $number"
            else -> numberText.text = ""
        }

    }

    private fun showGameFinished() {
        // Ваш код для отображения окончания игры
    }

    private fun getButtonText(number: Int): String {
        return when (gameMode) {
            1 -> number.toString()
            2 -> number.toChar().toString()
            3 -> number.toString()
            else -> number.toString()
        }
    }

    private fun getButtonNumber(text: String): Int {
        return when (gameMode) {
            1 -> text.toInt()
            2 -> text[0].code - 'A'.code + 1
            3 -> text.toInt()
            else -> 0
        }
    }

    private fun resetGame() {
        currentNumber = 1
        shuffleButtons()
        setTimerText(0)
        setNumberText(1)
        // Остановка и сброс таймера
        if (gameStarted) {
            updateTimeRunnable?.let { handler.removeCallbacks(it) }
            gameStarted = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    // указание элементов меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val deleteItem = menu.findItem(R.id.delete)
        deleteItem.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    fun updateOptionsMenu() {
        invalidateOptionsMenu()
    }

    public fun onBackClick(item: MenuItem){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        updateOptionsMenu()
    }

    // добавляем обработчик клика на кнопку удаления выбранных карточек
    public fun onDeleteSelectedCardsButtonClick(item: MenuItem) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                // Обработка нажатия кнопки "Назад"
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

