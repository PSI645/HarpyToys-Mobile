package com.harpytoys.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harpytoys.R
import com.harpytoys.model.CartItem

class CartAdapter(
    private var items: List<CartItem>,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvOrderPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvOrderDate)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.product.name
        holder.tvPrice.text = "R$ %.2f".format(item.product.price * item.quantity)
        holder.tvQuantity.text = "Qtd: ${item.quantity}"
        holder.btnRemove.setOnClickListener { onRemove(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getTotal(): Double = items.sumOf { it.product.price * it.quantity }

    fun getDescription(): String = items.joinToString(", ") { it.product.name }
}