package com.example.hardbrain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

class EditCardActivity : AppCompatActivity() {

    private lateinit var adapter: CardAdapter
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)


        adapter = CardAdapter(mutableListOf())

        firebaseHelper = FirebaseHelper()

        val etFront = findViewById<TextView>(R.id.edit_text_front)
        val etBack = findViewById<TextView>(R.id.edit_text_back)
        val btnSave = findViewById<TextView>(R.id.button_save_card)


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

            val newCard = Card(UUID.randomUUID().toString(), front, back)
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
}

