package com.harpytoys.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.harpytoys.R
import com.harpytoys.adapter.ProductAdapter
import com.harpytoys.model.Product
import com.harpytoys.network.RetrofitClient
import com.harpytoys.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)

        findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId = R.id.nav_home

        setupHeader()
        setupRecyclerView()
        loadProducts()
    }

    private fun setupHeader() {
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnFavorites).setOnClickListener {
            // TODO: abrir favoritos
        }

        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)
        etSearch.setOnEditorActionListener { _, _, _ ->
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) searchProducts(query)
            false
        }
    }

    private fun setupRecyclerView() {
        val rvProducts = findViewById<RecyclerView>(R.id.rvProducts)
        rvProducts.layoutManager = GridLayoutManager(this, 2)

        productAdapter = ProductAdapter(
            emptyList(),
            onAddToCart = { product -> addToCart(product) },
            onFavorite  = { product ->
                Toast.makeText(this, "${product.name} adicionado aos favoritos!", Toast.LENGTH_SHORT).show()
            }
        )
        rvProducts.adapter = productAdapter
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getProducts()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { productAdapter.updateProducts(it) }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getProducts()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val filtered = response.body()?.filter {
                            it.name.contains(query, ignoreCase = true)
                        } ?: emptyList()
                        productAdapter.updateProducts(filtered)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Erro na busca", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addToCart(product: Product) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Faça login para adicionar ao carrinho", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = mapOf("productId" to product.id)
                val response = RetrofitClient.apiService.addToCart(
                    sessionManager.getUserId(),
                    body
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@HomeActivity, "${product.name} adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}