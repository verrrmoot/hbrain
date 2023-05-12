package com.example.hardbrain

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    //private lateinit var cardView: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        mContext = this // инициализация контекста
        adapter = CardAdapter(mutableListOf())
        firebaseHelper = FirebaseHelper()
        //val itemCardView = layoutInflater.inflate(R.layout.item_card, null)
        var color: Int = ContextCompat.getColor(this, R.color.white) // цвет по умолчанию

        btnColorWhite = findViewById(R.id.btn_color_white)
        btnColorBlue = findViewById(R.id.btn_color_blue)
        btnColorRed = findViewById(R.id.btn_color_red)
        btnColorGreen = findViewById(R.id.btn_color_green)
        btnColorYellow = findViewById(R.id.btn_color_yellow)

        val etFront = findViewById<TextView>(R.id.edit_text_front)
        val etBack = findViewById<TextView>(R.id.edit_text_back)
        val btnSave = findViewById<TextView>(R.id.button_save_card)
        //cardView = itemCardView.findViewById(R.id.my_card_view)



        btnColorWhite.setOnClickListener { color = getColorByButton(it, this) }
        btnColorBlue.setOnClickListener { color = getColorByButton(it, this) }
        btnColorRed.setOnClickListener { color = getColorByButton(it, this) }
        btnColorGreen.setOnClickListener { color = getColorByButton(it, this) }
        btnColorYellow.setOnClickListener { color = getColorByButton(it, this) }

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
            val newCard = Card(UUID.randomUUID().toString(), front, back, color)
            firebaseHelper.addCard(newCard) { success ->
                if (success) {
                    adapter.cards.add(newCard)
                    adapter.notifyItemInserted(adapter.cards.size - 1)

                    val intent = Intent(this, CardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Ошибка при добавлении карточки", Toast.LENGTH_SHORT)
                        .show()
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

}

