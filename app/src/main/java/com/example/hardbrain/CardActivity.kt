package com.example.hardbrain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class CardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)


        recyclerView = findViewById(R.id.recyclerView)
        adapter = CardAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        firebaseHelper = FirebaseHelper()


        // Загружаем все карточки из Firebase и отображаем их в RecyclerView
        firebaseHelper.getAllCards { cards ->
            adapter.cards = cards as MutableList<Card>
            adapter.notifyDataSetChanged()
        }


        // Обработчик нажатия на кнопку добавления карточки
        val addCardButton = findViewById<FloatingActionButton>(R.id.addCardButton)
        addCardButton.setOnClickListener {
            /*
            // Создаем новую карточку с пустым фронтальным и обратным текстом
            val newCard = Card(UUID.randomUUID().toString(), "1", "13")

            // Добавляем карточку в Firebase
            firebaseHelper.addCard(newCard) { success ->
                if (success) {
                    // Если добавление прошло успешно, обновляем список карточек
                    adapter.cards.add(newCard)
                    adapter.notifyItemInserted(adapter.cards.size - 1)
                } else {
                    // Если добавление не удалось, выводим сообщение об ошибке
                    Toast.makeText(this, "Failed to add card", Toast.LENGTH_SHORT).show()
                }
            } */

            val intent = Intent(this, EditCardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
