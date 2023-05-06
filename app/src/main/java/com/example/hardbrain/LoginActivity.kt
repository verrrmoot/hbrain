package com.example.hardbrain

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Конфигурация клиента для входа через Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // Установка кнопки для входа через Google
        val signInButton = findViewById<Button>(R.id.btn_google_sign_in)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    // Начало процесса входа через Google
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startForResult.launch(signInIntent)
    }

    // Обработка результата входа через Google
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // Успешный вход, перенаправляем на MainActivity
            val account = task.getResult(ApiException::class.java)
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        } catch (e: ApiException) {
            // Неудачный вход, выводим сообщение об ошибке
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}