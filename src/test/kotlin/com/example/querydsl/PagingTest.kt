package com.example.querydsl

import com.example.querydsl.dto.ProductOrderDto
import com.example.querydsl.dto.ProductOrderSearchCondition
import com.example.querydsl.dto.QProductOrderDto
import com.example.querydsl.entity.Order
import com.example.querydsl.entity.Product
import com.example.querydsl.entity.QOrder.order
import com.example.querydsl.entity.QProduct.product
import com.example.querydsl.repository.JpaOrderRepository
import com.example.querydsl.repository.JpaProductRepository
import com.example.querydsl.repository.OrderRepository
import com.example.querydsl.repository.ProductRepository
import com.example.querydsl.stub.OrderStub
import com.querydsl.core.QueryResults
import com.querydsl.jpa.impl.JPAQueryFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.test.context.TestConstructor
import javax.persistence.EntityManager

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PagingTest(
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
    fun searchTest() {
        // given
        val product1 = productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        val product2 = productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        for(i in 1..100) {
            orderRepository.save(OrderStub.getOrder(productId = product1.id))
            orderRepository.save(OrderStub.getOrder(productId = product2.id))
        }

        val condition = ProductOrderSearchCondition(
            productId = product1.id,
            productType = product1.type,
            orderStatus = Order.Status.PURCHASED
        )

        val pageable = PageRequest.of(0, 10)

        // when
        val result = searchPage(condition, pageable)

        // then
        for (dto in result.content) {
            println("[content] $dto")
        }
        println("[total elements]: ${result.totalElements}")
        println("[total pages]: ${result.totalPages}")
    }

    private fun searchPage(condition: ProductOrderSearchCondition, pageable: Pageable): Page<ProductOrderDto> {
        val results: QueryResults<ProductOrderDto> = queryFactory
            .select(QProductOrderDto(
                product.id,
                product.name,
                product.type,
                order.id,
                order.status
            ))
            .from(product)
            .innerJoin(order)
//            .leftJoin(order)
            .on(product.id.eq(order.productId))
            .where(
                productIdEq(condition.productId),
                productNameEq(condition.productName),
                productTypeEq(condition.productType),
                orderIdEq(condition.orderId),
                orderStatusEq(condition.orderStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(order.id.desc())
            .fetchResults()

        val content: List<ProductOrderDto> = results.results
        val total = results.total

        return PageImpl(content, pageable, total)
    }

    private fun productIdEq(productId: Long?) =
        productId?.let { product.id.eq(it) }

    private fun productNameEq(productName: String?) =
        productName?.let { product.name.eq(it) }

    private fun productTypeEq(productType: Product.Type?) =
        productType?.let { product.type.eq(it) }

    private fun orderIdEq(orderId: Long?) =
        orderId?.let { order.id.eq(it) }

    private fun orderStatusEq(orderStatus: Order.Status?) =
        orderStatus?.let { order.status.eq(it) }


    @Test
    fun countQueryTuningTest() {
        // given
        val product1 = productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        val product2 = productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        // total: 20
        for(i in 1..10) {
            orderRepository.save(OrderStub.getOrder(productId = product1.id))
            orderRepository.save(OrderStub.getOrder(productId = product2.id))
        }

        val condition = ProductOrderSearchCondition(
            productId = product1.id,
            productType = product1.type,
            orderStatus = Order.Status.PURCHASED
        )

        /**
         * [case1] count query가 실행됨
         */
//        val pageable = PageRequest.of(0, 15)

        /**
         * [case2] count query가 실행되지 않음
         *  - 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
         */
//        val pageable = PageRequest.of(0, 100)
        /**
         * [case3] count query가 실행되지 않음
         *  - 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈를 계산함)
         */
        val pageable = PageRequest.of(2, 4)

        // when
        val result = searchPageCountQueryTuning(condition, pageable)

        // then
        for (dto in result.content) {
            println("[content] $dto")
        }
        println("[total elements]: ${result.totalElements}")
        println("[total pages]: ${result.totalPages}")
    }

    private fun searchPageCountQueryTuning(condition: ProductOrderSearchCondition, pageable: Pageable): Page<ProductOrderDto> {
        val content: List<ProductOrderDto> = queryFactory
            .select(QProductOrderDto(
                product.id,
                product.name,
                product.type,
                order.id,
                order.status
            ))
            .from(product)
            .innerJoin(order)
            .on(product.id.eq(order.productId))
            .where(
                productIdEq(condition.productId),
                productNameEq(condition.productName),
                productTypeEq(condition.productType),
                orderIdEq(condition.orderId),
                orderStatusEq(condition.orderStatus)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(order.id.desc())
            .fetch()

        // count query
        val countQuery = queryFactory
            .select(QProductOrderDto(
                product.id,
                product.name,
                product.type,
                order.id,
                order.status
            ))
            .from(product)
            .innerJoin(order)
            .on(product.id.eq(order.productId))
            .where(
                productIdEq(condition.productId),
                productNameEq(condition.productName),
                productTypeEq(condition.productType),
                orderIdEq(condition.orderId),
                orderStatusEq(condition.orderStatus)
            )

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount)
    }
}















//