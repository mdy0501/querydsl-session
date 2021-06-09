package com.example.querydsl.dto

import com.example.querydsl.entity.Product

data class ProductDto2(
    var name: String? = null,
    var price: Long? = null,
    var type: Product.Type? = null
)
