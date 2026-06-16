package com.harpytoys.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harpytoys.R
import com.harpytoys.model.Order

class OrderAdapter(
    private var orders: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val tvOrderId: TextView = itemView.findViewById(R.id.tvProductName)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderPrice)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = order.description
        holder.tvOrderDate.text = order.createdAt.take(10)
        holder.tvOrderTotal.text = "R$ %.2f".format(order.total)
        holder.tvOrderStatus.text = order.status

        if (!order.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(order.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivProductImage)
        }
    }

    override fun getItemCount() = orders.size
}