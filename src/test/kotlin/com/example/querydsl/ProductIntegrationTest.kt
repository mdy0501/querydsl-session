package com.example.querydsl

import com.example.querydsl.entity.Product
import com.example.querydsl.repository.JpaProductRepository
import com.example.querydsl.repository.ProductRepository
import com.example.querydsl.service.ProductService
import com.example.querydsl.stub.ProductStub
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProductIntegrationTest(
    private val productService: ProductService,
    private val productRepository: ProductRepository,
    private val jpaProductRepository: JpaProductRepository
) {

    @AfterEach
    fun afterEach() {
        jpaProductRepository.deleteAll()
    }

    @Test
    fun `getProductListByType service method`() {
        // given
        val basicProduct1 = productRepository.save(ProductStub.getBasicProduct(name = "basic-1"))
        val basicProduct2 = productRepository.save(ProductStub.getBasicProduct(name = "basic-2"))
        val basicProduct3 = productRepository.save(ProductStub.getBasicProduct(name = "basic-3"))
        productRepository.save(ProductStub.getCustomProduct(name = "custom-1"))
        productRepository.save(ProductStub.getCustomProduct(name = "custom-2"))
        productRepository.save(ProductStub.getEventProduct(name = "event-1"))
        productRepository.save(ProductStub.getEventProduct(name = "event-2"))

        val givenType = Product.Type.BASIC

        // when
        val result = productService.getProductListByType(givenType)
        println("######## result: $result")

        // then
        assertEquals(result.size, 3)
        assertEquals(basicProduct1, result[0])
        assertEquals(basicProduct2, result[1])
        assertEquals(basicProduct3, result[2])
    }
}