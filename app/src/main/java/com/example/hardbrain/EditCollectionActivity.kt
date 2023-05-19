package com.example.hardbrain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.UUID

class EditCollectionActivity: AppCompatActivity() {

    private lateinit var adapter: CollectionAdapter
    private lateinit var firebaseHelper: FirebaseHelper

    private lateinit var btnColorWhite: Button
    private lateinit var btnColorBlue: Button
    private lateinit var btnColorRed: Button
    private lateinit var btnColorGreen: Button
    private lateinit var btnColorYellow: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_collection)

        adapter = CollectionAdapter(mutableListOf())
        firebaseHelper = FirebaseHelper()

        val isNewCollection = intent.getBooleanExtra("isNewCollection", true)
        val position = intent.getIntExtra("position", -1)
        val collectionId = intent.getStringExtra("collection_id")

        btnColorWhite = findViewById(R.id.btn_color_white)
        btnColorBlue = findViewById(R.id.btn_color_blue)
        btnColorRed = findViewById(R.id.btn_color_red)
        btnColorGreen = findViewById(R.id.btn_color_green)
        btnColorYellow = findViewById(R.id.btn_color_yellow)

        val btnSave = findViewById<Button>(R.id.button_save_collection)
        val colName = findViewById<TextView>(R.id.collect_name)
        var color: Int = ContextCompat.getColor(this, R.color.red) // цвет по умолчанию

        btnColorWhite.setOnClickListener { color = getColorByButton(it, this) }
        btnColorBlue.setOnClickListener { color = getColorByButton(it, this) }
        btnColorRed.setOnClickListener { color = getColorByButton(it, this) }
        btnColorGreen.setOnClickListener { color = getColorByButton(it, this) }
        btnColorYellow.setOnClickListener { color = getColorByButton(it, this) }

        if (isNewCollection) {
            // Обработчик нажатия на кнопку сохранения
            btnSave.setOnClickListener {
                // Считываем данные из полей формы
                val name = colName.text.toString().trim()

                // Проверяем, что оба поля заполнены
                if (name.isEmpty()) {
                    Toast.makeText(this, "Заполните пустое поле", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Создаем новую карточку
                val newCollection = Collection(UUID.randomUUID().toString(), name, false, color, hashMapOf())
                firebaseHelper.createCollection(newCollection) { success ->
                    if (success) {
                        adapter.notifyItemInserted(adapter.collections.size - 1)
                        val intent = Intent(this, CollectionActivity::class.java)
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
            collectionId?.let {
                firebaseHelper.getCollectionById(it){collection ->
                    if (collection != null) {
                        // Редактируем существующую коллекцию
                        colName.text = collection.name
                        color = collection.color

                        btnSave.setOnClickListener {
                            // Считываем данные из полей формы
                            val name = colName.text.toString().trim()

                            // Проверяем, что оба поля заполнены
                            if (name.isEmpty()) {
                                Toast.makeText(this, "Заполните пустое поле", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            // Создаем новую карточку
                            val editCollection = Collection(collectionId, name, collection.pressed, color, collection.cards)
                            firebaseHelper.updateCollection(editCollection) { success ->
                                if (success) {
                                    adapter.notifyItemChanged(position)
                                    val intent = Intent(this, CollectionActivity::class.java)
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
