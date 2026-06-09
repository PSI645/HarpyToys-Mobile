package com.harpytoys.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.harpytoys.R
import com.harpytoys.utils.SessionManager

class ProfileActivity : BaseActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadUserData()
        setupMenuClicks()
    }

    private fun loadUserData() {
        findViewById<TextView>(R.id.tvName).text = sessionManager.getUsername()
        findViewById<TextView>(R.id.tvEmail).text = sessionManager.getEmail()
    }

    private fun setupMenuClicks() {
        findViewById<LinearLayout>(R.id.menuInicio).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuAjuda).setOnClickListener {
            // TODO: abrir tela de ajuda
        }

        findViewById<LinearLayout>(R.id.menuPedidos).setOnClickListener {
            // TODO: abrir tela de pedidos
        }

        findViewById<LinearLayout>(R.id.menuPreferencias).setOnClickListener {
            // TODO: abrir tela de preferências
        }

        findViewById<LinearLayout>(R.id.menuPedidos).setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.menuSair).setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}