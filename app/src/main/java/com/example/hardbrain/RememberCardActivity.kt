package com.example.hardbrain

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow

class RememberCardActivity: AppCompatActivity() {

    private lateinit var cardContainer: LinearLayout
    private lateinit var frontCard: FrameLayout
    private lateinit var backCard: FrameLayout
    private lateinit var frontText: TextView
    private lateinit var backText: TextView
    private lateinit var btnOne: Button
    private lateinit var btnTwo: Button
    private lateinit var btnThree: Button
    private lateinit var btnFour: Button
    private lateinit var btnFive: Button
    private lateinit var firebaseHelper: FirebaseHelper
    //private lateinit var previousDateString: String
    //private lateinit var previousDate: LocalDate

    private var isShowingFrontCard = true

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.remember_card)

        firebaseHelper = FirebaseHelper()
        // Переменная для хранения текущего индекса карточки
        var currentIndex = 0
        var rating = 0
        //var previousInterval: Long = 0
        //var factor: Double = 1.0


        cardContainer = findViewById(R.id.card_container)
        frontCard = findViewById(R.id.front_card)
        backCard = findViewById(R.id.back_card)
        btnOne = findViewById(R.id.btnOne)
        btnTwo = findViewById(R.id.btnTwo)
        btnThree = findViewById(R.id.btnThree)
        btnFour = findViewById(R.id.btnFour)
        btnFive = findViewById(R.id.btnFive)
        frontText = findViewById(R.id.txtFrontSide)
        backText = findViewById(R.id.txtBackSide)

        val flipToFrontAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_to_front)
        val flipToBackAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_to_back)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        firebaseHelper.getAllCollectionCards { cards ->
            Log.d("cards", cards.toString())
            // Показать первую карточку при запуске активности
            showCardAtIndex(currentIndex, cards)


            // Обработчик нажатия кнопки "0"
            btnOne.setOnClickListener {
                rating = 1
                updateDate(cards[currentIndex], formatter, rating)
                currentIndex++
                if (currentIndex < cards.size) {
                    showCardAtIndex(currentIndex,cards)
                } else {
                    theEndToday()
                }
            }

            // Обработчик нажатия кнопки "2"
            btnTwo.setOnClickListener {
                rating = 2
                updateDate(cards[currentIndex], formatter, rating)
                currentIndex++
                if (currentIndex < cards.size) {
                    showCardAtIndex(currentIndex, cards)
                } else {
                    theEndToday()
                }
            }
            // Обработчик нажатия кнопки "3"
            btnThree.setOnClickListener {
                rating = 3
                updateDate(cards[currentIndex], formatter, rating)
                currentIndex++
                if (currentIndex < cards.size) {
                    showCardAtIndex(currentIndex, cards)
                } else {
                    theEndToday()
                }
            }
            // Обработчик нажатия кнопки "4"
            btnFour.setOnClickListener {
                rating = 4
                updateDate(cards[currentIndex], formatter, rating)
                currentIndex++
                if (currentIndex < cards.size) {
                    showCardAtIndex(currentIndex, cards)
                } else {
                    theEndToday()
                }
            }
            // Обработчик нажатия кнопки "5"
            btnFive.setOnClickListener {
                rating = 5
                updateDate(cards[currentIndex], formatter, rating)
                currentIndex++
                if (currentIndex < cards.size) {
                    showCardAtIndex(currentIndex, cards)
                } else {
                    theEndToday()
                }
            }
        }


        cardContainer.setOnClickListener {
            if (isShowingFrontCard) {
                frontCard.startAnimation(flipToBackAnimation)
                frontCard.visibility = View.GONE
                backCard.visibility = View.VISIBLE
            } else {
                backCard.startAnimation(flipToFrontAnimation)
                backCard.visibility = View.GONE
                frontCard.visibility = View.VISIBLE
            }

            isShowingFrontCard = !isShowingFrontCard
        }


    }

    // Функция для отображения карточки по указанному индексу
    fun showCardAtIndex(index: Int, cards: List<Card>) {
        val currentCard = cards[index]
        // Отобразить карточку на экране
        // Например, установить текст на TextView для отображения содержимого карточки
        frontText.text = currentCard.front
        backText.text = currentCard.back
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateNextReviewDate(previousDate: LocalDate, interval: Long): LocalDate {
        return previousDate.plusDays(interval)
    }

    fun calculateInterval(previousInterval: Long, factor: Double, rating: Int): Long {
        val exponent = rating - 1
        val newInterval = previousInterval * factor.pow(exponent)
        return newInterval.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(card: Card, formatter: DateTimeFormatter, rating: Int){
        val previousInterval = card.interval.toLong()
        var factor = card.factor //1.0
        Log.d("factor", factor.toString())
        Log.d("id", card.id.toString())
        Log.d("collectionId", card.collectionId.toString())
        if (rating == 1 || rating == 2){
            factor = 1.0
        }
        else if (rating == 4){
            factor += 0.15
        }
        else if (rating == 5){
            factor += 0.3
        }
        val previousDateString = card.date
        Log.d("previousDateString", previousDateString.toString())
        val previousDate = LocalDate.parse(previousDateString, formatter)
        Log.d("previousDate", previousDate.toString())

        val newInterval = calculateInterval(previousInterval, factor, rating)
        val nextReviewDate = calculateNextReviewDate(previousDate, newInterval)
        val nextReviewDateString = nextReviewDate.format(formatter)

        val editedCard = Card(card.id, card.front, card.back,
            nextReviewDateString, card.collectionId,
            newInterval.toInt(), 1.0, card.color)

        card.collectionId?.let {
            firebaseHelper.updateCard(editedCard, it) { success ->
                if (success) {
                    Log.d("updateDateCard", nextReviewDateString)
                } else {
                    Toast.makeText(this,"Ошибка при редактировании карточки", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun theEndToday(){
        frontText.text = "Сегодня Вы повторили все карточки! Приходите завтра:)"
        backText.text = "Сегодня Вы повторили все карточки! Приходите завтра:)"
        btnOne.visibility = View.GONE
        btnTwo.visibility = View.GONE
        btnThree.visibility = View.GONE
        btnFour.visibility = View.GONE
        btnFive.visibility = View.GONE
    }
}
