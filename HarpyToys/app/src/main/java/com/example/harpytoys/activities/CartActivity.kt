package com.harpytoys.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.harpytoys.R
import com.harpytoys.adapter.CartAdapter
import com.harpytoys.model.CartItem
import com.harpytoys.network.RetrofitClient
import com.harpytoys.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : BaseActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var cartAdapter: CartAdapter
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId = R.id.nav_cart

        sessionManager = SessionManager(this)
        tvTotal = findViewById(R.id.tvTotal)

        setupRecyclerView()
        setupCheckout()
        loadCart()
    }

    private fun setupRecyclerView() {
        val rvCart = findViewById<RecyclerView>(R.id.rvCartItems)
        rvCart.layoutManager = LinearLayoutManager(this)

        cartAdapter = CartAdapter(emptyList()) { item ->
            removeFromCart(item)
        }
        rvCart.adapter = cartAdapter
    }

    private fun loadCart() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getCart(sessionManager.getUserId())
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            cartAdapter.updateItems(it)
                            tvTotal.text = "R$ %.2f".format(cartAdapter.getTotal())
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Erro ao carregar carrinho", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun removeFromCart(item: CartItem) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.apiService.removeFromCart(sessionManager.getUserId(), item.id)
                withContext(Dispatchers.Main) {
                    loadCart()
                    Toast.makeText(this@CartActivity, "Item removido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "Erro ao remover item", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupCheckout() {
        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            if (cartAdapter.getTotal() == 0.0) {
                Toast.makeText(this, "Carrinho vazio!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putExtra("total", cartAdapter.getTotal())
                putExtra("description", cartAdapter.getDescription())
            }
            startActivity(intent)
        }
    }
}