package com.example.querydsl.dto

import com.example.querydsl.entity.Order
import com.example.querydsl.entity.Product

data class ProductOrderSearchCondition(
    val productId: Long? = null,
    val productName: String? = null,
    val productType: Product.Type? = null,
    val orderId: Long? = null,
    val orderStatus: Order.Status? = null
)
