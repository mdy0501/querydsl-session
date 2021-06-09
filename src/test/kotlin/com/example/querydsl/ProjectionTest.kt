package com.example.querydsl

import com.example.querydsl.dto.ProductDto
import com.example.querydsl.dto.ProductDto2
import com.example.querydsl.dto.ProductDto3
import com.example.querydsl.dto.QProductDto3
import com.example.querydsl.entity.Product
import com.example.querydsl.entity.QProduct.product
import com.example.querydsl.repository.JpaOrderRepository
import com.example.querydsl.repository.JpaProductRepository
import com.example.querydsl.repository.OrderRepository
import com.example.querydsl.repository.ProductRepository
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import javax.persistence.EntityManager


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProjectionTest(
    private val entityManager: EntityManager,
    private val queryFactory: JPAQueryFactory,
    private val jpaOrderRepository: JpaOrderRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val jpaProductRepository: JpaProductRepository
) {

    @AfterEach
    fun afterEach() {
        jpaProductRepository.deleteAll()
        jpaOrderRepository.deleteAll()
    }

    @Test
    fun `dto test in pure JPA`() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val result = entityManager.createQuery(
            "select new com.example.querydsl.dto.ProductDto(p.name, p.price, p.type) " +
                    "from Product p", ProductDto::class.java
        ).resultList

        for (dto in result) {
            println("[dto] : $dto")
        }
    }

    @Test
    fun findDtoBySetter() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val result: List<ProductDto2> = queryFactory
            .select(
                Projections.bean(
                    ProductDto2::class.java,
                    product.name.`as`("name"),
                    product.price.`as`("price"),
                    product.type.`as`("type")
                )
            )
            .from(product)
            .fetch()

        for (dto in result) {
            println("[dto] : $dto")
        }
    }

    @Test
    fun findDtoByField() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val result: List<ProductDto2> = queryFactory
            .select(
                Projections.fields(
                    ProductDto2::class.java,
                    product.name,
                    product.price,
                    product.type
                )
            )
            .from(product)
            .fetch()

        for (dto in result) {
            println("[dto] : $dto")
        }
    }

    @Test
    fun findDtoByConstructor() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val result: List<ProductDto> = queryFactory
            .select(
                Projections.constructor(
                    ProductDto::class.java,
                    product.name,
                    product.price,
                    product.type
                )
            )
            .from(product)
            .fetch()

        for (dto in result) {
            println("[dto] : $dto")
        }
    }

    @Test
    fun findDtoByQueryProjectionConstructor() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        // @QueryProjection
        val result: List<ProductDto3> = queryFactory
            .select(QProductDto3(product.name, product.price, product.type))
            .from(product)
            .fetch()

        for (dto in result) {
            println("[dto] : $dto")
        }
    }


    @Test
    fun test() {

    }
}