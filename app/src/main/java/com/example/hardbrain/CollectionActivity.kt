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

class CollectionActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter
    private lateinit var firebaseHelper: FirebaseHelper
    var bool: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = CollectionAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        firebaseHelper = FirebaseHelper()


        firebaseHelper.getAllCollections { collections ->
            // Обновляем список коллекций в адаптере
            adapter.updateCollections(collections)
        }

        // Обработчик нажатия на кнопку добавления коллекции
        val addCollectionButton = findViewById<FloatingActionButton>(R.id.addCollectionButton)
        addCollectionButton.setOnClickListener {
            val intent = Intent(this, EditCollectionActivity::class.java)
            startActivity(intent)
            finish()
        }

        // добавляем обработчик долгого нажатия на коллекцию
        adapter.setOnLongClickListener { collection, holder ->
            // показываем чекбоксы
            adapter.showCheckBoxes(recyclerView)
            bool = true
            updateOptionsMenu()
            // возвращаем true, чтобы показать контекстное меню (если оно есть)
            true
        }

    }

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
        super.onBackPressed()
        updateOptionsMenu()
    }
    public fun onDeleteSelectedCardsButtonClick(item: MenuItem){
        // получаем список выбранных карточек
        adapter.selectedCollections.forEach { collection ->
            val position = adapter.getPosition(collection)
            if (position != -1) {
                // удаляем выбранные карточки из БД
                collection.id?.let {
                    // удаляем выбранные карточки из списка
                    adapter.collections.removeAt(position)
                    // обновляем позицию элементов, находящихся после удаленного элемента
                    adapter.notifyItemRangeChanged(0, adapter.collections.size)
                    // уведомляем адаптер об изменениях в списке
                    adapter.notifyItemRemoved(position)
                    firebaseHelper.deleteCollections(it) { isSuccess ->
                        if (isSuccess) {
                            Log.d("collections", adapter.collections.toString())

                        } else {
                            // обработка ошибки удаления из БД
                            Toast.makeText(this, "Failed to delete selected collections", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
        adapter.hideCheckBoxes(recyclerView)
        adapter.selectedCollections.clear()
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
        bool = false
        updateOptionsMenu()
    }
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