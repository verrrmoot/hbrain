package com.example.hardbrain


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var collectionId: String
    var bool: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        collectionId = intent.getStringExtra("collectionId")!!
        recyclerView = findViewById(R.id.recycler_view)
        adapter = CardAdapter(mutableListOf(), collectionId)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        firebaseHelper = FirebaseHelper()

        Log.d("collectionID", collectionId)
        // Загружаем все карточки из Firebase и отображаем их в RecyclerView
            firebaseHelper.getAllCards(collectionId) { cards ->
                adapter.cards = cards as MutableList<Card>
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            }

        // Обработчик нажатия на кнопку добавления карточки
        val addCardButton = findViewById<FloatingActionButton>(R.id.addCardButton)
        addCardButton.setOnClickListener {
            val intent = Intent(this, EditCardActivity::class.java)
            intent.putExtra("collectionId", collectionId)
            startActivity(intent)
            finish()
        }

        // добавляем обработчик долгого нажатия на карточку
            adapter.setOnLongClickListener { card, holder ->
            // показываем чекбоксы
                adapter.showCheckBoxes(recyclerView)
                bool = true
                updateOptionsMenu()
            // возвращаем true, чтобы показать контекстное меню (если оно есть)
            true
        }
    }

    // указание элементов меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val deleteItem = menu.findItem(R.id.delete)
        deleteItem.isVisible = bool
        return super.onPrepareOptionsMenu(menu)
    }

    fun updateOptionsMenu() {
        invalidateOptionsMenu()
    }

    public fun onBackClick(item: MenuItem){
        val intent = Intent(this, CollectionActivity::class.java)
        startActivity(intent)
        finish()
        updateOptionsMenu()
    }

    // добавляем обработчик клика на кнопку удаления выбранных карточек
    public fun onDeleteSelectedCardsButtonClick(item: MenuItem) {
        // получаем список выбранных карточек
        adapter.selectedCards.forEach { card ->
            val position = adapter.getPosition(card)
            if (position != -1) {
            // удаляем выбранные карточки из БД
            card.id?.let {
                // удаляем выбранные карточки из списка
                adapter.cards.removeAt(position)
                // обновляем позицию элементов, находящихся после удаленного элемента
                adapter.notifyItemRangeChanged(0, adapter.cards.size)
                // уведомляем адаптер об изменениях в списке
                adapter.notifyItemRemoved(position)
                firebaseHelper.deleteCard(it, collectionId) { isSuccess ->
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
        bool = false
        updateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                // Обработка нажатия кнопки "Назад"
                val intent = Intent(this, CollectionActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
