package com.harpytoys.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.harpytoys.R
import com.harpytoys.adapter.OrderAdapter
import com.harpytoys.network.RetrofitClient
import com.harpytoys.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId = R.id.nav_profile

        setupHeader()
        setupRecyclerView()
    }

    private fun setupHeader() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val tilSearch = findViewById<TextInputLayout>(R.id.tilSearch)

        findViewById<ImageButton>(R.id.btnSearch).setOnClickListener {
            if (tilSearch.visibility == View.GONE) {
                tilSearch.visibility = View.VISIBLE
            } else {
                tilSearch.visibility = View.GONE
            }
        }

        findViewById<ImageButton>(R.id.btnFilter).setOnClickListener {
            // TODO: abrir filtro por status/data
        }
    }

    private fun setupRecyclerView() {
        val rvOrders = findViewById<RecyclerView>(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessionManager = SessionManager(this@OrdersActivity)
                val response = RetrofitClient.apiService.getOrders(sessionManager.getUserId())
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { orders ->
                            rvOrders.adapter = OrderAdapter(orders)
                        }
                    } else {
                        Toast.makeText(this@OrdersActivity, "Erro ao carregar pedidos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@OrdersActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}