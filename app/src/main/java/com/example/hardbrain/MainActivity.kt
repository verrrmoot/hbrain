package com.example.hardbrain

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получение данных об учетной записи Google
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            // Аккаунт уже вошел, выполняем действия для зарегистрированного пользователя
            Toast.makeText(this, "Logged in as ${account.displayName}", Toast.LENGTH_SHORT).show()
        } else {
            // Аккаунт еще не вошел, перенаправляем на страницу входа
            val signInIntent = Intent(this, LoginActivity::class.java)
            startActivity(signInIntent)
            finish()
        }

        // Находим кнопку в макете
        val button = findViewById<Button>(R.id.btn_go_to_login)

        // Добавляем обработчик для нажатия на кнопку
        button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        // Добавляем обработчик для нажатия на кнопку
        val buttoncard = findViewById<Button>(R.id.btn_go_cards)

        buttoncard.setOnClickListener {
            val intent = Intent(this, CardActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Добавляем обработчик для нажатия на кнопку
        val buttoncollection = findViewById<Button>(R.id.btn_go_collections)

        buttoncollection.setOnClickListener {
            val intent = Intent(this, CollectionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
