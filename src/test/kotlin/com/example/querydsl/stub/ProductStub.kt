package com.example.querydsl.stub

import com.example.querydsl.entity.Product

object ProductStub {
    fun getBasicProduct(name: String): Product =
        Product(
            name = name,
            type = Product.Type.BASIC
        )

    fun getCustomProduct(name: String): Product =
        Product(
            name = name,
            type = Product.Type.CUSTOM
        )

    fun getEventProduct(name: String): Product =
        Product(
            name = name,
            type = Product.Type.EVENT
        )
}