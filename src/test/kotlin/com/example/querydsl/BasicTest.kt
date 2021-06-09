package com.example.querydsl

import com.example.querydsl.entity.Product
import com.example.querydsl.entity.QProduct
import com.example.querydsl.repository.JpaProductRepository
import com.example.querydsl.repository.ProductRepository
import com.example.querydsl.stub.ProductStub
import com.querydsl.jpa.impl.JPAQueryFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import javax.persistence.EntityManager

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BasicTest(
    private val entityManager: EntityManager,
    private val queryFactory: JPAQueryFactory,
    private val productRepository: ProductRepository,
    private val jpaProductRepository: JpaProductRepository
) {

    @BeforeEach
    fun init() {
        productRepository.save(ProductStub.getBasicProduct(name = "basic-1"))
        productRepository.save(ProductStub.getBasicProduct(name = "basic-2"))
        productRepository.save(ProductStub.getCustomProduct(name = "custom-1"))
        productRepository.save(ProductStub.getCustomProduct(name = "custom-2"))
        productRepository.save(ProductStub.getEventProduct(name = "event-1"))
        productRepository.save(ProductStub.getEventProduct(name = "event-2"))
    }

    @AfterEach
    fun afterEach() {
        jpaProductRepository.deleteAll()
    }

    @Test
    fun testJPQL() {
        // given
        val queryString = "select p from Product p " +
                "where p.name = :name"
        val productName = "basic-1"

        // when
        val result = entityManager.createQuery(queryString, Product::class.java)
            .setParameter("name", productName)
            .singleResult

        // then
        assertEquals(productName, result.name)
    }

    @Test
    fun testQuerydsl() {
        // given
        val productName = "basic-1"

        // when
        val result = queryFactory.select(QProduct.product)
            .from(QProduct.product)
            .where(QProduct.product.name.eq(productName))
            .fetchOne()

        // then
        assertEquals(productName, result!!.name)
    }
}