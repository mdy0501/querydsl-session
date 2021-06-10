package com.example.querydsl.dto

import com.example.querydsl.entity.Product

class ProductDto3(
    private val name: String? = null,
    private val price: Long? = null,
    private val type: Product.Type? = null
) {
    override fun toString(): String {
        return "ProductDto3(name=$name, price=$price, type=$type)"
    }
}