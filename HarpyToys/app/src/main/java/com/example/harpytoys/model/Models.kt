package com.harpytoys.model

data class User(
    val id: Long,
    val name: String,
    val email: String
)

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
)

data class CartItem(
    val id: Long,
    val product: Product,
    val quantity: Int
)

data class Order(
    val id: Long,
    val user: User,
    val total: Double,
    val status: String,
    val createdAt: String,
    val description: String
)
