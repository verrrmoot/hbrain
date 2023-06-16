package com.example.hardbrain

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class EditCardActivity : AppCompatActivity() {

    private lateinit var adapter: CardAdapter
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var mContext: Context

    private lateinit var btnColorWhite: Button
    private lateinit var btnColorBlue: Button
    private lateinit var btnColorRed: Button
    private lateinit var btnColorGreen: Button
    private lateinit var btnColorYellow: Button
    private lateinit var collectionId: String
    private lateinit var cardId: String
    private lateinit var imageUri: Uri
    private var imageUrl: String? = null


    companion object {
        private const val REQUEST_IMAGE_PICK = 4
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        val isNewCard = intent.getBooleanExtra("isNewCard", true)
        cardId = intent.getStringExtra("card_id").toString()
        val position = intent.getIntExtra("position", -1)
        collectionId = intent.getStringExtra("collectionId")!!

        mContext = this // инициализация контекста
        adapter = CardAdapter(mutableListOf(), collectionId)
        firebaseHelper = FirebaseHelper()
        var color: Int = ContextCompat.getColor(this, R.color.white) // цвет по умолчанию
        val NowDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateString = NowDate.format(formatter)

        btnColorWhite = findViewById(R.id.btn_color_white)
        btnColorBlue = findViewById(R.id.btn_color_blue)
        btnColorRed = findViewById(R.id.btn_color_red)
        btnColorGreen = findViewById(R.id.btn_color_green)
        btnColorYellow = findViewById(R.id.btn_color_yellow)

        val etFront = findViewById<TextView>(R.id.edit_text_front)
        val etBack = findViewById<TextView>(R.id.edit_text_back)
        val btnSave = findViewById<TextView>(R.id.button_save_card)

        btnColorWhite.setOnClickListener { color = getColorByButton(it, this) }
        btnColorBlue.setOnClickListener { color = getColorByButton(it, this) }
        btnColorRed.setOnClickListener { color = getColorByButton(it, this) }
        btnColorGreen.setOnClickListener { color = getColorByButton(it, this) }
        btnColorYellow.setOnClickListener { color = getColorByButton(it, this) }

            if (isNewCard) {
            // Обработчик нажатия на кнопку сохранения карточки
            btnSave.setOnClickListener {
                // Считываем данные из полей формы
                val front = etFront.text.toString().trim()
                val back = etBack.text.toString().trim()

                // Проверяем, что оба поля заполнены
                if (front.isEmpty() || back.isEmpty()) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Создаем новую карточку
                val newCard = Card(UUID.randomUUID().toString(), front, back, dateString, collectionId, 1, 1.0, color)
                if (imageUrl != null){
                    newCard.imageUrl = imageUrl
                }
                firebaseHelper.addCard(newCard, collectionId) { success ->
                    if (success) {
                        adapter.cards.add(newCard)
                        adapter.notifyItemInserted(adapter.cards.size - 1)

                        val intent = Intent(this, CardActivity::class.java)
                        intent.putExtra("collectionId", collectionId)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Ошибка при добавлении карточки", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }
        else{
            cardId?.let {
                firebaseHelper.getCardById(it, collectionId) { card ->
                    if (card != null) {
                        // Редактируем существующую карточку
                        etFront.text = card.front
                        etBack.text = card.back
                        color = card.color

                        Log.d("Id", cardId)
                        Log.d("Position", position.toString())
                        btnSave.setOnClickListener {

                            val front = etFront.text.toString().trim()
                            val back = etBack.text.toString().trim()
                            // Проверяем, что оба поля заполнены
                            if (front.isEmpty() || back.isEmpty()) {
                                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            val editedCard = Card(cardId, front, back, card.date, card.collectionId, card.interval, card.factor, color)
                            if (imageUrl != null){
                                editedCard.imageUrl = imageUrl
                            }
                            Log.d("Id editedCard", editedCard.id.toString())
                            Log.d("collectionId", collectionId)

                            firebaseHelper.updateCard(editedCard, collectionId) { success ->
                                if (success) {
                                    //adapter.cards[position] = editedCard
                                    adapter.notifyItemChanged(position)
                                    val intent = Intent(this, CardActivity::class.java)
                                    intent.putExtra("collectionId", collectionId)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this,"Ошибка при редактировании карточки",Toast.LENGTH_SHORT).show()
                                }


                            }

                        }
                    }
                    else {
                        Toast.makeText(this,"Карточка с таким идентификатором не найдена",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && imageUri != null) {
            firebaseHelper.uploadImageToFirestore(imageUri, collectionId, cardId) { imageUrl ->
                if (imageUrl != null) {
                    this.imageUrl = imageUrl
                } else {
                    //
                }
            }
        }

    }


    fun getColorByButton(view: View, context: Context): Int {
        return when (view.id) {
            R.id.btn_color_blue -> ContextCompat.getColor(context, R.color.blue)
            R.id.btn_color_red -> ContextCompat.getColor(context, R.color.red)
            R.id.btn_color_green -> ContextCompat.getColor(context, R.color.green)
            R.id.btn_color_yellow -> ContextCompat.getColor(context, R.color.yellow)
            else -> ContextCompat.getColor(context, R.color.white) // цвет по умолчанию
        }
    }

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
        super.onBackPressed()
        updateOptionsMenu()
    }
    public fun onDeleteSelectedCardsButtonClick(item: MenuItem){}
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                super.onBackPressed() // Обработка нажатия кнопки "Назад"
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

