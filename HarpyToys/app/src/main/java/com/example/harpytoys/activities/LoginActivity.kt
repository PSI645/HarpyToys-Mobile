package com.harpytoys.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.harpytoys.R
import com.harpytoys.network.RetrofitClient
import com.harpytoys.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        sessionManager.clearSession()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        val etEmail    = findViewById<TextInputEditText>(R.id.editTextTextEmailAddress2)
        val etPassword = findViewById<TextInputEditText>(R.id.editTextTextPassword2)
        val btnLogin   = findViewById<Button>(R.id.btnCadastrar)
        val btnRegister = findViewById<Button>(R.id.buttonCadastro)

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf("email" to email, "password" to password)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.login(body)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val user = response.body()!!
                            sessionManager.saveSession(user.id, user.name, user.email)
                            navigateToHome()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "E-mail ou senha inválidos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Erro de conexão com o servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}