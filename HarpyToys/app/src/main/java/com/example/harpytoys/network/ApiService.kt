package com.harpytoys.network

import com.harpytoys.model.CartItem
import com.harpytoys.model.Order
import com.harpytoys.model.Product
import com.harpytoys.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Auth ---
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<User>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<User>

    // --- Produtos ---
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Long): Response<Product>

    // --- Carrinho ---
    @GET("cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): Response<List<CartItem>>

    @POST("cart/{userId}/add")
    suspend fun addToCart(
        @Path("userId") userId: Long,
        @Body body: Map<String, Long>
    ): Response<Void>

    @DELETE("cart/{userId}/remove/{itemId}")
    suspend fun removeFromCart(
        @Path("userId") userId: Long,
        @Path("itemId") itemId: Long
    ): Response<Void>

    // --- Pedidos ---
    @POST("orders/{userId}/checkout")
    suspend fun checkout(@Path("userId") userId: Long): Response<Order>

    @GET("orders/{userId}")
    suspend fun getOrders(@Path("userId") userId: Long): Response<List<Order>>

    // --- Perfil ---
    @GET("users/{id}")
    suspend fun getProfile(@Path("id") id: Long): Response<User>

    @PUT("users/{id}")
    suspend fun updateProfile(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    ): Response<User>
}