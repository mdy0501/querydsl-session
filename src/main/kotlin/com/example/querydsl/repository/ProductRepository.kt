package com.example.querydsl.repository

import com.example.querydsl.entity.Product
import com.example.querydsl.entity.QProduct.product
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(
    private val jpaProductRepository: JpaProductRepository,
    private val queryFactory: JPAQueryFactory
) {

    fun findAllByRequest(name: String?, type: Product.Type?): List<Product> =
        queryFactory
            .select(product)
            .from(product)
            .apply {
                name?.let { where(product.name.eq(name)) }
                type?.let { where(product.type.eq(type)) }
            }
            .fetch()

    fun findByType(type: Product.Type): List<Product> =
        queryFactory
            .select(product)
            .from(product)
            .where(product.type.eq(type))
            .orderBy(product.id.asc())
            .fetch()

    fun findAll(): List<Product> =
        queryFactory
            .select(product)
            .from(product)
            .orderBy(product.id.desc())
            .fetch()

    fun save(product: Product): Product = jpaProductRepository.save(product)
}

interface JpaProductRepository : JpaRepository<Product, Long>