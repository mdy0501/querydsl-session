package com.example.querydsl.service

import com.example.querydsl.entity.Product
import com.example.querydsl.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    fun getProductListByType(type: Product.Type): List<Product> = productRepository.findByType(type)
}