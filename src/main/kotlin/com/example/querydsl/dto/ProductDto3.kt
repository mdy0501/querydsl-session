package com.example.querydsl.dto

import com.example.querydsl.entity.Product
import com.querydsl.core.annotations.QueryProjection

//data class ProductDto3(
//    private val name: String,
//    private val price: Long,
//    private val type: Product.Type
//)

class ProductDto3 {
    private var name: String? = null
    private var price: Long? = null
    private var type: Product.Type? = null

    constructor() {}

    @QueryProjection
    constructor(name: String?, price: Long?, type: Product.Type?) {
        this.name = name
        this.price = price
        this.type = type
    }

    override fun toString(): String {
        return "ProductDto3(name=$name, price=$price, type=$type)"
    }
}