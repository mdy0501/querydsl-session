package com.example.querydsl.stub

import com.example.querydsl.entity.Order

object OrderStub {
    fun getOrder(productId: Long) = Order(
        productId = productId,
        status = Order.Status.PURCHASED
    )
}