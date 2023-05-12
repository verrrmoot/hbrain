package com.example.hardbrain

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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


        // добавляем обработчик долгого нажатия на карточку
            adapter.setOnLongClickListener { card, holder ->
            // показываем чекбоксы
                adapter.showCheckBoxes(recyclerView)
                /*if (adapter.selectedCards.contains(card)) {
                    adapter.selectedCards.remove(card)
                    holder.checkBox.isChecked = false
                } else {
                    adapter.selectedCards.add(card)
                    holder.checkBox.isChecked = true
                }*/

            // возвращаем true, чтобы показать контекстное меню (если оно есть)
            true
        }



    }

    // указание элементов меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }
    // добавляем обработчик клика на кнопку удаления выбранных карточек

    public fun onDeleteSelectedCardsButtonClick(item: MenuItem) {
        // получаем список выбранных карточек
        adapter.selectedCards.forEach { card ->
            val position = adapter.getPosition(card)
            Log.d("Position", position.toString())
            Log.d("Size", adapter.cards.size.toString())
            Log.d("Count", adapter.itemCount.toString())
            Log.d("SelectedCards", adapter.selectedCards.toString())
            if (position != -1) {
            // удаляем выбранные карточки из БД
            card.id?.let {
                Log.d("Cards2", adapter.cards.toString())
                // удаляем выбранные карточки из списка
                adapter.cards.removeAt(position)
                Log.d("Cards", adapter.cards.toString())

                // обновляем позицию элементов, находящихся после удаленного элемента
                adapter.notifyItemRangeChanged(0, adapter.cards.size)

                // уведомляем адаптер об изменениях в списке
                adapter.notifyItemRemoved(position)
                firebaseHelper.deleteCard(it) { isSuccess ->
                    if (isSuccess) {
                        Log.d("Cards1", adapter.cards.toString())

                    } else {
                        // обработка ошибки удаления из БД
                        Toast.makeText(this, "Failed to delete selected cards", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            }
        }
        adapter.hideCheckBoxes(recyclerView)
        adapter.selectedCards.clear()
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
    }

    /*
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
*/


}
