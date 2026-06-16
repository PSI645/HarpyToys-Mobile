package com.harpytoys.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.harpytoys.R
import com.harpytoys.network.RetrofitClient
import com.harpytoys.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class CheckoutActivity : BaseActivity() {

    private lateinit var sessionManager: SessionManager
    private var currentAddress = ""
    private var cartTotal = 0.0
    private var cartDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        sessionManager = SessionManager(this)

        // Recebe total e descrição do carrinho
        cartTotal = intent.getDoubleExtra("total", 0.0)
        cartDescription = intent.getStringExtra("description") ?: ""

        setupHeader()
        setupCepSearch()
        setupUserInfo()
        setupSummary()
        setupFinalize()
    }

    private fun setupHeader() {
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun setupCepSearch() {
        val etCep = findViewById<TextInputEditText>(R.id.etCep)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarCep)
        val cardAddress = findViewById<CardView>(R.id.cardAddress)
        val tvAddress = findViewById<TextView>(R.id.tvAddress)

        btnBuscar.setOnClickListener {
            val cep = etCep.text.toString().trim().replace("-", "")
            if (cep.length != 8) {
                Toast.makeText(this, "CEP inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = "https://viacep.com.br/ws/$cep/json/"
                    val response = URL(url).readText()
                    val json = JSONObject(response)

                    if (json.has("erro")) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CheckoutActivity, "CEP não encontrado", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val logradouro = json.getString("logradouro")
                    val bairro = json.getString("bairro")
                    val cidade = json.getString("localidade")
                    val uf = json.getString("uf")
                    currentAddress = "$logradouro, $bairro - $cidade/$uf - CEP: $cep"

                    withContext(Dispatchers.Main) {
                        tvAddress.text = currentAddress
                        cardAddress.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CheckoutActivity, "Erro ao buscar CEP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupUserInfo() {
        findViewById<TextView>(R.id.tvUserName).text = sessionManager.getUsername()
        findViewById<TextView>(R.id.tvUserEmail).text = sessionManager.getEmail()
    }

    private fun setupSummary() {
        findViewById<TextView>(R.id.tvOrderDescription).text = cartDescription
        findViewById<TextView>(R.id.tvTotal).text = "R$ %.2f".format(cartTotal)
    }

    private fun setupFinalize() {
        val rgPayment = findViewById<RadioGroup>(R.id.rgPayment)
        val btnFinalize = findViewById<Button>(R.id.btnFinalize)
        val etCep = findViewById<TextInputEditText>(R.id.etCep)

        btnFinalize.setOnClickListener {
            val paymentId = rgPayment.checkedRadioButtonId
            if (paymentId == -1) {
                Toast.makeText(this, "Selecione uma forma de pagamento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentAddress.isEmpty()) {
                Toast.makeText(this, "Busque seu CEP primeiro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentMethod = if (paymentId == R.id.rbCartao) "CARTAO" else "PIX"
            val cep = etCep.text.toString().trim()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val body = mapOf(
                        "paymentMethod" to paymentMethod,
                        "cep" to cep,
                        "address" to currentAddress
                    )
                    val response = RetrofitClient.apiService.checkout(
                        sessionManager.getUserId(),
                        body
                    )
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@CheckoutActivity,
                                "Pedido realizado com sucesso!",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@CheckoutActivity,
                                "Erro ao finalizar pedido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CheckoutActivity,
                            "Erro de conexão",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}