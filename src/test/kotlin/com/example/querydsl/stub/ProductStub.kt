package com.example.querydsl.stub

import com.example.querydsl.entity.Product

object ProductStub {
    fun getBasicProduct(name: String): Product =
        Product(
            name = name,
            price = 1000,
            type = Product.Type.BASIC
        )

    fun getCustomProduct(name: String): Product =
        Product(
            name = name,
            price = 2000,
            type = Product.Type.CUSTOM
        )

    fun getEventProduct(name: String): Product =
        Product(
            name = name,
            price = 0,
            type = Product.Type.EVENT
        )
}