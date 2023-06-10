package com.example.hardbrain

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTheme(R.style.AppTheme)

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
        val button = findViewById<Button>(R.id.btn_go_to_share)

        // Добавляем обработчик для нажатия на кнопку
        button.setOnClickListener {
            val intent = Intent(this, ShareCollectionActivity::class.java)
            startActivity(intent)
            finish()
        }


        // Добавляем обработчик для нажатия на кнопку
        val buttoncard = findViewById<Button>(R.id.btn_go_cards)

        buttoncard.setOnClickListener {
            val intent = Intent(this, RememberCardActivity::class.java)
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

        // Добавляем обработчик для нажатия на кнопку
        val buttonplay = findViewById<Button>(R.id.btn_go_play)

        buttonplay.setOnClickListener {
            val intent = Intent(this, ShulteActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnSignOut = findViewById<Button>(R.id.sign_out)
        btnSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        // Получите экземпляр FirebaseAuth
        val firebaseAuth = FirebaseAuth.getInstance()

        // Выполните выход из учетной записи Google
        firebaseAuth.signOut()

        // Дополнительно, если вы хотите также отключиться от учетной записи Google,
        // можно использовать GoogleSignInClient для выполнения выхода из аккаунта Google:

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        // Перенаправьте пользователя на экран входа или другую соответствующую активность
        // в вашем приложении, чтобы завершить процесс выхода.
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
