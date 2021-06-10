package com.example.querydsl.dto

import com.example.querydsl.entity.Order
import com.example.querydsl.entity.Product
import com.querydsl.core.annotations.QueryProjection

data class ProductOrderDto @QueryProjection constructor(
    private val productId: Long,
    private val productName: String,
    private val productType: Product.Type,
    private val orderId: Long,
    private val orderStatus: Order.Status
)