package com.example.hardbrain

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RememberCardActivity: AppCompatActivity() {

    private lateinit var cardContainer: LinearLayout
    private lateinit var frontCard: FrameLayout
    private lateinit var backCard: FrameLayout
    private lateinit var frontText: TextView
    private lateinit var backText: TextView
    private lateinit var btnRemember: Button
    private lateinit var btnNotRemember: Button
    private lateinit var firebaseHelper: FirebaseHelper
    //private lateinit var cards: List<Card>

    private var isShowingFrontCard = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.remember_card)

        firebaseHelper = FirebaseHelper()
        // Переменная для хранения текущего индекса карточки
        var currentIndex = 0
        //val cards = emptyList<Card>()

        cardContainer = findViewById(R.id.card_container)
        frontCard = findViewById(R.id.front_card)
        backCard = findViewById(R.id.back_card)
        btnRemember = findViewById(R.id.btnRemember)
        btnNotRemember = findViewById(R.id.btnNotRemember)
        frontText = findViewById(R.id.txtFrontSide)
        backText = findViewById(R.id.txtBackSide)

        val flipToFrontAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_to_front)
        val flipToBackAnimation = AnimationUtils.loadAnimation(this, R.anim.flip_to_back)

        firebaseHelper.getAllCollectionCards { cards ->
            Log.d("cards", cards.toString())
            // Показать первую карточку при запуске активности
            showCardAtIndex(currentIndex, cards)


            // Обработчик нажатия кнопки "запомнил"
            btnRemember.setOnClickListener {
                currentIndex++
                if (currentIndex < cards.size) {
                    showCardAtIndex(currentIndex,cards)
                } else {
                    frontText.text = "Сегодня Вы повторили все карточки! Приходите завтра:)"
                    backText.text = "Сегодня Вы повторили все карточки! Приходите завтра:)"
                    btnRemember.visibility = View.GONE
                    btnNotRemember.visibility = View.GONE
                }
            }

// Обработчик нажатия кнопки "не запомнил"
            btnNotRemember.setOnClickListener {
                currentIndex++
                if (currentIndex < cards.size) {
                    showCardAtIndex(currentIndex, cards)
                } else {
                    frontText.text = "Сегодня Вы повторили все карточки! Приходите завтра:)"
                    backText.text = "Сегодня Вы повторили все карточки! Приходите завтра:)"
                    btnRemember.visibility = View.GONE
                    btnNotRemember.visibility = View.GONE
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
}
