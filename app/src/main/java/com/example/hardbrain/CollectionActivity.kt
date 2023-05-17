package com.example.hardbrain

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CollectionActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollectionAdapter
    private lateinit var firebaseHelper: FirebaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collections)

        recyclerView = findViewById(R.id.recycler_view)
        adapter = CollectionAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        firebaseHelper = FirebaseHelper()



        // Загружаем все карточки из Firebase и отображаем их в RecyclerView
       // firebaseHelper.getAllCards { cards ->
         //   adapter.cards = cards as MutableList<Card>
         //   adapter.notifyItemRangeChanged(0, adapter.itemCount)

        //}

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