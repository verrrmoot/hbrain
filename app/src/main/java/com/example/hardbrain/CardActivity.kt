package com.example.hardbrain

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class CardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var firebaseHelper: FirebaseHelper


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)


        recyclerView = findViewById(R.id.recycler_view)
        adapter = CardAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        firebaseHelper = FirebaseHelper()


        // Загружаем все карточки из Firebase и отображаем их в RecyclerView
        firebaseHelper.getAllCards { cards ->
            adapter.cards = cards as MutableList<Card>
            adapter.notifyItemRangeChanged(0, adapter.itemCount)

        }



        // Обработчик нажатия на кнопку добавления карточки
        val addCardButton = findViewById<FloatingActionButton>(R.id.addCardButton)
        addCardButton.setOnClickListener {

            val intent = Intent(this, EditCardActivity::class.java)
            startActivity(intent)
            finish()
        }

    }




    // указание элементов меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    public fun onDeleteSelectedCardsButtonClick(item: MenuItem) {
        for (card in adapter.cards) {
            if (adapter.selectedCards.contains(card)) {
                card.id?.let {
                    firebaseHelper.deleteCard(it) { isSuccess ->
                        if (isSuccess) {
                            Log.d(TAG, "Card with id ${card.id} was deleted from Firebase")
                        } else {
                            Log.e(TAG, "Error deleting card with id ${card.id} from Firebase")
                        }
                    }
                }
            }
        }

        adapter.cards.removeAll(adapter.selectedCards)
        adapter.selectedCards.clear()
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
    }



}
