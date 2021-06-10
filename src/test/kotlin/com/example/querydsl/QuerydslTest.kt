package com.example.querydsl

import com.example.querydsl.entity.Product
import com.example.querydsl.entity.QOrder.order
import com.example.querydsl.entity.QProduct
import com.example.querydsl.entity.QProduct.product
import com.example.querydsl.repository.JpaOrderRepository
import com.example.querydsl.repository.JpaProductRepository
import com.example.querydsl.repository.OrderRepository
import com.example.querydsl.repository.ProductRepository
import com.example.querydsl.stub.OrderStub
import com.example.querydsl.stub.ProductStub
import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class QuerydslTest(
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
    fun subqueryEqualTest() {
        // given
        val product1 = productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        val productSub = QProduct("productSub")

        // when
        val productList: List<Product> = queryFactory.selectFrom(product)
            .where(
                product.price.eq(
                    JPAExpressions
                        .select(productSub.price.max())
                        .from(productSub)
                )
            )
            .fetch()

        // then
        Assertions.assertThat(productList)
            .extracting("name")
            .containsExactly(product1.name)
    }

    @Test
    fun subqueryGoeTest() {
        // given
        val product1 = productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        val product2 = productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        val productSub = QProduct("productSub")

        // when
        val productList: List<Product> = queryFactory.selectFrom(product)
            .where(
                product.price.goe(
                    JPAExpressions
                        .select(productSub.price.avg())
                        .from(productSub)
                )
            )
            .fetch()

        // then
        Assertions.assertThat(productList)
            .extracting("name")
            .containsExactly(
                product1.name,
                product2.name
            )
    }

    @Test
    fun subqueryInTest() {
        // given
        val product1 = productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        val product2 = productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        val product3 = productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        val productSub = QProduct("productSub")

        // when
        val productList: List<Product> = queryFactory.selectFrom(product)
            .where(
                product.price.`in`(
                    JPAExpressions
                        .select(productSub.price)
                        .from(productSub)
                        .where(productSub.price.gt(500))
                )
            )
            .fetch()

        // then
        Assertions.assertThat(productList)
            .extracting("name")
            .containsExactly(
                product1.name,
                product2.name,
                product3.name
            )
    }

    @Test
    fun selectSubqueryTest() {
        // given
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        val productSub = QProduct("productSub")

        // when
        val result: List<Tuple> = queryFactory.select(
            product.name,
            JPAExpressions
                .select(productSub.price.avg())
                .from(productSub)
        ).from(product)
            .fetch()

        for (tuple in result) {
            println("[res] = $tuple")
        }
    }

    @Test
    fun inner_join_test() {
        // given
        val product1 = productRepository.save(ProductStub.getBasicProduct(name = "basic-1"))
        val product2 = productRepository.save(ProductStub.getBasicProduct(name = "basic-2"))
        val order1 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order2 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order3 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order4 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order5 = orderRepository.save(OrderStub.getOrder(productId = product1.id))

        val innerJoinResult: List<Tuple> = queryFactory
            .select(product, order)
            .from(product)
            .innerJoin(order).on(product.id.eq(order.productId))
            .fetch()

        for (tuple in innerJoinResult) {
            println("[inner join test]: $tuple")
        }
    }

    @Test
    fun left_join_test() {
        // given
        val product1 = productRepository.save(ProductStub.getBasicProduct(name = "basic-1"))
        val product2 = productRepository.save(ProductStub.getBasicProduct(name = "basic-2"))
        val order1 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order2 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order3 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order4 = orderRepository.save(OrderStub.getOrder(productId = product1.id))
        val order5 = orderRepository.save(OrderStub.getOrder(productId = product1.id))

        val leftJoinResult: List<Tuple> = queryFactory
            .select(product, order)
            .from(product)
            .leftJoin(order).on(product.id.eq(order.productId))
            .fetch()

        for (tuple in leftJoinResult) {
            println("[left join test] : $tuple")
        }
    }

    @Test
    fun basicCase() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val result: List<String> = queryFactory
            .select(
                product.price
                    .`when`(1000).then("천원")
                    .`when`(2000).then("이천원")
                    .`when`(3000).then("삼천원")
                    .otherwise("기타")
            ).from(product)
            .fetch()

        for (str in result) {
            println("[result] = $str")
        }
    }

    @Test
    fun complexCase() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val result: List<String> = queryFactory
            .select(
                CaseBuilder()
                    .`when`(product.price.eq(1000)).then("천원")
                    .`when`(product.price.eq(2000)).then("이천원")
                    .`when`(product.price.eq(3000)).then("삼천원")
                    .otherwise("기타")
            ).from(product)
            .fetch()

        for (str in result) {
            println("[result] = $str")
        }
    }

    @Test
    fun caseBuilderTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        productRepository.save(Product(name = "event-2", price = 500, type = Product.Type.EVENT))

        val rankPath: NumberExpression<Int> = CaseBuilder()
            .`when`(product.price.eq(1000)).then(4)
            .`when`(product.price.eq(2000)).then(3)
            .`when`(product.price.eq(3000)).then(2)
            .otherwise(1)

        val result: List<Tuple> = queryFactory
            .select(product.name, product.price, product.type)
            .from(product)
            .orderBy(rankPath.desc())
            .fetch()

        for (tuple in result) {
            println("$tuple")
        }
    }

    @Test
    fun constantTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val result: List<Tuple> = queryFactory
            .select(product.name, Expressions.constant("A"))
            .from(product)
            .fetch()

        for (tuple in result) {
            println("[res] : $tuple")
        }
    }

    @Test
    fun addLettersTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        // {name}_{price}
        val result: List<String> = queryFactory
            .select(product.name.concat("_").concat(product.price.stringValue()))
            .from(product)
            .fetch()

        for (str in result) {
            println("[res] : $str")
        }
    }
}