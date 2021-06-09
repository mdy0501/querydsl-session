package com.example.querydsl.dto

import com.example.querydsl.entity.Product

data class ProductDto(
    private val name: String,
    private val price: Long,
    private val type: Product.Type
)