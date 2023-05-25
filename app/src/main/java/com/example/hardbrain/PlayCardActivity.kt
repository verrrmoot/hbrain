package com.example.hardbrain

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.CreationExtras

enum class DifficultyLevel {
    EASY,
    MEDIUM,
    HARD
}

class PlayCardActivity: AppCompatActivity(), View.OnClickListener {

        private val cardImagesEasy = listOf(
            R.drawable.card_image_1,
            R.drawable.card_image_2,
            R.drawable.card_image_3,
            R.drawable.card_image_4,
            R.drawable.card_image_5,
            R.drawable.card_image_6
        )
    private val cardImagesMedium = listOf(
        R.drawable.card_image_1,
        R.drawable.card_image_2,
        R.drawable.card_image_3,
        R.drawable.card_image_4,
        R.drawable.card_image_5,
        R.drawable.card_image_6,
        R.drawable.card_image_7,
        R.drawable.card_image_8,
        R.drawable.card_image_9
    )
    private val cardImagesHard = listOf(
        R.drawable.card_image_1,
        R.drawable.card_image_2,
        R.drawable.card_image_3,
        R.drawable.card_image_4,
        R.drawable.card_image_5,
        R.drawable.card_image_6,
        R.drawable.card_image_7,
        R.drawable.card_image_8,
        R.drawable.card_image_9,
        R.drawable.card_image_10,
        R.drawable.card_image_11,
        R.drawable.card_image_12
    )

    private var cardPairs = cardImagesEasy.size
    private val cards = mutableListOf<Int>()
    private var flippedCardsCount = 0
    private var firstCardIndex = -1
    private var secondCardIndex = -1
    private var cardCount = 0
    private lateinit var gridLayoutEasy: GridLayout
    private lateinit var gridLayoutMedium: GridLayout
    private lateinit var gridLayoutHard: GridLayout
    private lateinit var difficultyLevel: DifficultyLevel
    private lateinit var imageViews: List<ImageView>


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.play_card)

        gridLayoutEasy = findViewById(R.id.gridLayoutEasy)
        gridLayoutMedium = findViewById(R.id.gridLayoutMedium)
        gridLayoutHard = findViewById(R.id.gridLayoutHard)

        difficultyLevel = DifficultyLevel.EASY
        setGridLayout(difficultyLevel)
        // Перемешать картинки
        cards.addAll(cardImagesEasy)
        cards.addAll(cardImagesEasy)
        cards.shuffle()

        val btEasy = findViewById<Button>(R.id.bt_easy)
        val btMedium = findViewById<Button>(R.id.bt_medium)
        val btHard = findViewById<Button>(R.id.bt_hard)
        imageViews = FindViews(this, difficultyLevel)

        imageViews.forEach{element ->
            element.setOnClickListener(this)
            // Установить изображения для каждой карточки
            val index = getCardIndex(element, difficultyLevel)
            element.setImageResource(cards[index])
            flipCardBack(element, getCardIndex(element, difficultyLevel))
        }

        // Установка уровня сложности
        btEasy.setOnClickListener{
            difficultyLevel = DifficultyLevel.EASY
            setGridLayout(difficultyLevel)
            cardPairs = cardImagesEasy.size
            cards.clear()
            // Перемешать картинки
            cards.addAll(cardImagesEasy)
            cards.addAll(cardImagesEasy)
            cards.shuffle()

            imageViews = FindViews(this, difficultyLevel)

            imageViews.forEach{element ->
                element.setOnClickListener(this)
                // Установить изображения для каждой карточки
                val index = getCardIndex(element, difficultyLevel)
                element.setImageResource(cards[index])
                flipCardBack(element, getCardIndex(element, difficultyLevel))
            }
        }
        btMedium.setOnClickListener{
            difficultyLevel = DifficultyLevel.MEDIUM
            setGridLayout(difficultyLevel)
            cardPairs = cardImagesMedium.size
            cards.clear()
            // Перемешать картинки
            cards.addAll(cardImagesMedium)
            cards.addAll(cardImagesMedium)
            cards.shuffle()

            imageViews = FindViews(this, difficultyLevel)

            imageViews.forEach{element ->
                element.setOnClickListener(this)
                // Установить изображения для каждой карточки
                val index = getCardIndex(element, difficultyLevel)
                element.setImageResource(cards[index])
                flipCardBack(element, getCardIndex(element, difficultyLevel))
            }
        }
        btHard.setOnClickListener{
            difficultyLevel = DifficultyLevel.HARD
            setGridLayout(difficultyLevel)
            cardPairs = cardImagesHard.size
            cards.clear()
            // Перемешать картинки
            cards.addAll(cardImagesHard)
            cards.addAll(cardImagesHard)
            cards.shuffle()

            imageViews = FindViews(this, difficultyLevel)

            imageViews.forEach{element ->
                element.setOnClickListener(this)
                // Установить изображения для каждой карточки
                val index = getCardIndex(element, difficultyLevel)
                element.setImageResource(cards[index])
                flipCardBack(element, getCardIndex(element, difficultyLevel))
            }
        }

        cardCount = when (difficultyLevel) {
            DifficultyLevel.EASY -> 12
            DifficultyLevel.MEDIUM -> 18
            DifficultyLevel.HARD -> 24
        }


    }

        override fun onClick(view: View) {
            val clickedCard = view as ImageView
            val cardIndex = getCardIndex(clickedCard, difficultyLevel)

            if (isCardAlreadyFlipped(cardIndex, difficultyLevel)) {
                return
            }

            flipCard(clickedCard, cardIndex)

            if (firstCardIndex == -1) {
                firstCardIndex = cardIndex
            } else {
                secondCardIndex = cardIndex

                val firstCard = findViewById<ImageView>(getCardViewId(firstCardIndex,difficultyLevel ))
                val secondCard = findViewById<ImageView>(getCardViewId(secondCardIndex, difficultyLevel))

                if (cards[firstCardIndex] == cards[secondCardIndex]) {
                    // Совпадение! Обработка логики совпадения карточек

                    flippedCardsCount += 2
                    checkGameCompletion()
                } else {
                    // Несовпадение! Обработка логики несовпадения карточек

                    // Добавьте здесь задержку (Handler, Coroutine, Timer и т. д.) перед закрытием карточек
                    // Для примера, здесь используется задержка в 1 секунду с помощью Handler

                    val handler = Handler()
                    handler.postDelayed({
                        flipCardBack(firstCard, firstCardIndex)
                        flipCardBack(secondCard, secondCardIndex)
                    }, 1000)
                }

                firstCardIndex = -1
                secondCardIndex = -1

                // Добавьте проверку на победу, если достигнуто нужное количество карточек
                if (flippedCardsCount == cardCount) {

                }
            }

        }

    private fun FindViews (context: Context, difficultyLevel: DifficultyLevel): List<ImageView> {
        val cardList = mutableListOf<ImageView>()
        when (difficultyLevel) {
            DifficultyLevel.EASY -> {
                // Установить обработчик щелчков для всех карточек
                cardList.add(findViewById<ImageView>(R.id.e_card1))
                cardList.add(findViewById<ImageView>(R.id.e_card2))
                cardList.add(findViewById<ImageView>(R.id.e_card3))
                cardList.add(findViewById<ImageView>(R.id.e_card4))
                cardList.add(findViewById<ImageView>(R.id.e_card5))
                cardList.add(findViewById<ImageView>(R.id.e_card6))
                cardList.add(findViewById<ImageView>(R.id.e_card7))
                cardList.add(findViewById<ImageView>(R.id.e_card8))
                cardList.add(findViewById<ImageView>(R.id.e_card9))
                cardList.add(findViewById<ImageView>(R.id.e_card10))
                cardList.add(findViewById<ImageView>(R.id.e_card11))
                cardList.add(findViewById<ImageView>(R.id.e_card12))
                return (cardList)
            }
            DifficultyLevel.MEDIUM -> {
                // Установить обработчик щелчков для всех карточек
                cardList.add(findViewById<ImageView>(R.id.m_card1))
                cardList.add(findViewById<ImageView>(R.id.m_card2))
                cardList.add(findViewById<ImageView>(R.id.m_card3))
                cardList.add(findViewById<ImageView>(R.id.m_card4))
                cardList.add(findViewById<ImageView>(R.id.m_card5))
                cardList.add(findViewById<ImageView>(R.id.m_card6))
                cardList.add(findViewById<ImageView>(R.id.m_card7))
                cardList.add(findViewById<ImageView>(R.id.m_card8))
                cardList.add(findViewById<ImageView>(R.id.m_card9))
                cardList.add(findViewById<ImageView>(R.id.m_card10))
                cardList.add(findViewById<ImageView>(R.id.m_card11))
                cardList.add(findViewById<ImageView>(R.id.m_card12))
                cardList.add(findViewById<ImageView>(R.id.m_card13))
                cardList.add(findViewById<ImageView>(R.id.m_card14))
                cardList.add(findViewById<ImageView>(R.id.m_card15))
                cardList.add(findViewById<ImageView>(R.id.m_card16))
                cardList.add(findViewById<ImageView>(R.id.m_card17))
                cardList.add(findViewById<ImageView>(R.id.m_card18))
                return (cardList)
            }
            DifficultyLevel.HARD -> {
                // Установить обработчик щелчков для всех карточек
                cardList.add(findViewById<ImageView>(R.id.h_card1))
                cardList.add(findViewById<ImageView>(R.id.h_card2))
                cardList.add(findViewById<ImageView>(R.id.h_card3))
                cardList.add(findViewById<ImageView>(R.id.h_card4))
                cardList.add(findViewById<ImageView>(R.id.h_card5))
                cardList.add(findViewById<ImageView>(R.id.h_card6))
                cardList.add(findViewById<ImageView>(R.id.h_card7))
                cardList.add(findViewById<ImageView>(R.id.h_card8))
                cardList.add(findViewById<ImageView>(R.id.h_card9))
                cardList.add(findViewById<ImageView>(R.id.h_card10))
                cardList.add(findViewById<ImageView>(R.id.h_card11))
                cardList.add(findViewById<ImageView>(R.id.h_card12))
                cardList.add(findViewById<ImageView>(R.id.h_card13))
                cardList.add(findViewById<ImageView>(R.id.h_card14))
                cardList.add(findViewById<ImageView>(R.id.h_card15))
                cardList.add(findViewById<ImageView>(R.id.h_card16))
                cardList.add(findViewById<ImageView>(R.id.h_card17))
                cardList.add(findViewById<ImageView>(R.id.h_card18))
                cardList.add(findViewById<ImageView>(R.id.h_card19))
                cardList.add(findViewById<ImageView>(R.id.h_card20))
                cardList.add(findViewById<ImageView>(R.id.h_card21))
                cardList.add(findViewById<ImageView>(R.id.h_card22))
                cardList.add(findViewById<ImageView>(R.id.h_card23))
                cardList.add(findViewById<ImageView>(R.id.h_card24))
                return (cardList)
            }
        }
    }

        private fun getCardIndex(card: ImageView, difficultyLevel: DifficultyLevel): Int {
            when (difficultyLevel) {
                DifficultyLevel.EASY -> {
                    return when (card.id) {
                        R.id.e_card1 -> 0
                        R.id.e_card2 -> 1
                        R.id.e_card3 -> 2
                        R.id.e_card4 -> 3
                        R.id.e_card5 -> 4
                        R.id.e_card6 -> 5
                        R.id.e_card7 -> 6
                        R.id.e_card8 -> 7
                        R.id.e_card9 -> 8
                        R.id.e_card10 -> 9
                        R.id.e_card11 -> 10
                        R.id.e_card12 -> 11
                        else -> -1
                    }
                }
                DifficultyLevel.MEDIUM -> {
                    return when (card.id) {
                        R.id.m_card1 -> 0
                        R.id.m_card2 -> 1
                        R.id.m_card3 -> 2
                        R.id.m_card4 -> 3
                        R.id.m_card5 -> 4
                        R.id.m_card6 -> 5
                        R.id.m_card7 -> 6
                        R.id.m_card8 -> 7
                        R.id.m_card9 -> 8
                        R.id.m_card10 -> 9
                        R.id.m_card11 -> 10
                        R.id.m_card12 -> 11
                        R.id.m_card13 -> 12
                        R.id.m_card14 -> 13
                        R.id.m_card15 -> 14
                        R.id.m_card16 -> 15
                        R.id.m_card17 -> 16
                        R.id.m_card18 -> 17
                        else -> -1
                    }
                }
                DifficultyLevel.HARD -> {
                    return when (card.id) {
                        R.id.h_card1 -> 0
                        R.id.h_card2 -> 1
                        R.id.h_card3 -> 2
                        R.id.h_card4 -> 3
                        R.id.h_card5 -> 4
                        R.id.h_card6 -> 5
                        R.id.h_card7 -> 6
                        R.id.h_card8 -> 7
                        R.id.h_card9 -> 8
                        R.id.h_card10 -> 9
                        R.id.h_card11 -> 10
                        R.id.h_card12 -> 11
                        R.id.h_card13 -> 12
                        R.id.h_card14 -> 13
                        R.id.h_card15 -> 14
                        R.id.h_card16 -> 15
                        R.id.h_card17 -> 16
                        R.id.h_card18 -> 17
                        R.id.h_card19 -> 18
                        R.id.h_card20 -> 19
                        R.id.h_card21 -> 20
                        R.id.h_card22 -> 21
                        R.id.h_card23 -> 22
                        R.id.h_card24 -> 23
                        else -> -1
                    }
                }
            }

        }

        private fun getCardViewId(cardIndex: Int, difficultyLevel: DifficultyLevel): Int {
            when (difficultyLevel) {
                DifficultyLevel.EASY -> {
                    return when (cardIndex) {
                        0 -> R.id.e_card1
                        1 -> R.id.e_card2
                        2 -> R.id.e_card3
                        3 -> R.id.e_card4
                        4 -> R.id.e_card5
                        5 -> R.id.e_card6
                        6 -> R.id.e_card7
                        7 -> R.id.e_card8
                        8 -> R.id.e_card9
                        9 -> R.id.e_card10
                        10 -> R.id.e_card11
                        11 -> R.id.e_card12
                        else -> -1
                    }
                }

                DifficultyLevel.MEDIUM -> {
                    return when (cardIndex) {
                        0 -> R.id.m_card1
                        1 -> R.id.m_card2
                        2 -> R.id.m_card3
                        3 -> R.id.m_card4
                        4 -> R.id.m_card5
                        5 -> R.id.m_card6
                        6 -> R.id.m_card7
                        7 -> R.id.m_card8
                        8 -> R.id.m_card9
                        9 -> R.id.m_card10
                        10 -> R.id.m_card11
                        11 -> R.id.m_card12
                        12 -> R.id.m_card13
                        13 -> R.id.m_card14
                        14 -> R.id.m_card15
                        15 -> R.id.m_card16
                        16 -> R.id.m_card17
                        17 -> R.id.m_card18
                        else -> -1
                    }
                }

                DifficultyLevel.HARD -> {
                    return when (cardIndex) {
                        0 -> R.id.h_card1
                        1 -> R.id.h_card2
                        2 -> R.id.h_card3
                        3 -> R.id.h_card4
                        4 -> R.id.h_card5
                        5 -> R.id.h_card6
                        6 -> R.id.h_card7
                        7 -> R.id.h_card8
                        8 -> R.id.h_card9
                        9 -> R.id.h_card10
                        10 -> R.id.h_card11
                        11 -> R.id.h_card12
                        12 -> R.id.h_card13
                        13 -> R.id.h_card14
                        14 -> R.id.h_card15
                        15 -> R.id.h_card16
                        16 -> R.id.h_card17
                        17 -> R.id.h_card18
                        18 -> R.id.h_card19
                        19 -> R.id.h_card20
                        20 -> R.id.h_card21
                        21 -> R.id.h_card22
                        22 -> R.id.h_card23
                        23 -> R.id.h_card24
                        else -> -1
                    }
                }
            }

        }

    private fun setGridLayout(difficultyLevel: DifficultyLevel) {
        when (difficultyLevel) {
            DifficultyLevel.EASY -> {
                gridLayoutEasy.visibility = View.VISIBLE
                gridLayoutMedium.visibility = View.GONE
                gridLayoutHard.visibility = View.GONE
            }
            DifficultyLevel.MEDIUM -> {
                gridLayoutEasy.visibility = View.GONE
                gridLayoutMedium.visibility = View.VISIBLE
                gridLayoutHard.visibility = View.GONE
            }
            DifficultyLevel.HARD -> {
                gridLayoutEasy.visibility = View.GONE
                gridLayoutMedium.visibility = View.GONE
                gridLayoutHard.visibility = View.VISIBLE
            }
        }
    }


    private fun isCardAlreadyFlipped(cardIndex: Int, difficultyLevel: DifficultyLevel): Boolean {
            val card = findViewById<ImageView>(getCardViewId(cardIndex, difficultyLevel))
            return card.tag != null && card.tag == "flipped"
        }

        private fun flipCard(card: ImageView, cardIndex: Int) {
            card.setImageResource(cards[cardIndex])
            card.tag = "flipped"
        }

        private fun flipCardBack(card: ImageView, cardIndex: Int) {
            card.setImageResource(R.drawable.card_back)
            card.tag = null
        }

        private fun checkGameCompletion() {
            if (flippedCardsCount == cardPairs) {
                // Игра завершена, выполните необходимые действия
            }
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
