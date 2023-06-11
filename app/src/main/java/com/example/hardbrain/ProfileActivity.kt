package com.example.hardbrain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var textViewUsername: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewStats: TextView
    private lateinit var imageViewAvatar: ImageView

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Инициализация элементов интерфейса
        textViewUsername = findViewById(R.id.text_username)
        textViewEmail = findViewById(R.id.text_email)
        textViewStats = findViewById(R.id.text_stats)
        imageViewAvatar = findViewById(R.id.imageViewAvatar)

        // Получение экземпляра Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance()

        // Получение текущего пользователя
        currentUser = FirebaseAuth.getInstance().currentUser!!



        // Загрузка данных профиля пользователя
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // Получение ссылки на узел профиля пользователя в Firebase Database
        val userRef = firebaseDatabase.reference.child("users").child(currentUser.uid)

        // Получение URL аватара пользователя
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val photoUrl = account?.photoUrl

        // Загрузка и отображение аватара
        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl)
                .circleCrop()
                .into(imageViewAvatar)
        }

        // Чтение данных профиля пользователя
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Получение данных профиля пользователя
                val username = account!!.displayName
                val email = account!!.email
                //val stats = dataSnapshot.child("stats").getValue(String::class.java)

                // Обновление элементов интерфейса с полученными данными
                textViewUsername.text = username
                textViewEmail.text = email
                //textViewStats.text = stats
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибки при чтении данных профиля пользователя
                Log.e("ProfileActivity", "Error loading user profile", databaseError.toException())
            }
        })
    }

    // указание элементов меню
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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        updateOptionsMenu()
    }

    // добавляем обработчик клика на кнопку удаления выбранных карточек
    public fun onDeleteSelectedCardsButtonClick(item: MenuItem) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                // Обработка нажатия кнопки "Назад"
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
