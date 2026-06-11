package com.harpytoys.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.harpytoys.R
import com.harpytoys.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        val etName            = findViewById<EditText>(R.id.etName)
        val etEmail           = findViewById<EditText>(R.id.etEmail)
        val etDdd             = findViewById<EditText>(R.id.etDdd)
        val etPhone           = findViewById<EditText>(R.id.etPhone)
        val etPassword        = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister       = findViewById<Button>(R.id.btnRegister)
        val btnVoltar         = findViewById<Button>(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnRegister.setOnClickListener {
            val name     = etName.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val ddd      = etDdd.text.toString().trim()
            val phone    = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm  = etConfirmPassword.text.toString().trim()

            // Validações
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || ddd.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf(
                "name"     to name,
                "email"    to email,
                "password" to password,
                "phone"    to "($ddd) $phone"
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.register(body)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Cadastro realizado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Erro: e-mail já cadastrado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Erro de conexão com o servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}