package com.example.querydsl.dto

import com.example.querydsl.entity.Product
import com.querydsl.core.annotations.QueryProjection

class ProductDto4 @QueryProjection constructor(
    private val name: String,
    private val price: Long,
    private val type: Product.Type
) {
    override fun toString(): String {
        return "ProductDto4(name='$name', price=$price, type=$type)"
    }
}