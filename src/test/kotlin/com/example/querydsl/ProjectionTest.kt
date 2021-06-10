package com.example.querydsl

import com.example.querydsl.dto.ProductDto
import com.example.querydsl.dto.ProductDto2
import com.example.querydsl.dto.ProductDto3
import com.example.querydsl.dto.ProductDto4
import com.example.querydsl.dto.ProductOrderDto
import com.example.querydsl.dto.QProductDto4
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
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.annotation.Transactional
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
//        jpaProductRepository.deleteAll()
//        jpaOrderRepository.deleteAll()
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

        val result: List<ProductDto3> = queryFactory
            .select(
                Projections.fields(
                    ProductDto3::class.java,
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
        val result: List<ProductDto4> = queryFactory
            .select(QProductDto4(product.name, product.price, product.type))
            .from(product)
            .fetch()

        for (dto in result) {
            println("[dto] : $dto")
        }
    }

    @Test
    fun dynamicQueryBooleanBuilder() {
        // given
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        // request parameter
        val name: String? = "event-1"
        val price: Long? = null
        val type: Product.Type? = Product.Type.EVENT

        val builder = BooleanBuilder()
        name?.let { builder.and(product.name.eq(name)) }
        price?.let { builder.and(product.price.eq(price)) }
        type?.let { builder.and(product.type.eq(type)) }

        // when
        val result: List<Product> = queryFactory
            .selectFrom(product)
            .where(builder)
            .fetch()

        // then
        for (prod in result) {
            println("[product]: $prod")
        }
    }

    @Test
    fun dynamicQueryWhereParameter() {
        // given
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        // request parameter
        val name: String? = "event-1"
        val price: Long? = null
        val type: Product.Type? = Product.Type.EVENT

        // when
        val result: List<Product> = queryFactory
            .selectFrom(product)
            .where(
                productNameEq(name),
                productPriceEq(price),  // where 조건에서 null 값은 무시된다.
                productTypeEq(type)
            )
            .fetch()

        // then
        for (prod in result) {
            println("[product]: $prod")
        }
    }

    private fun productNameEq(name: String?): BooleanExpression? =
        name?.let { product.name.eq(name) }

    private fun productPriceEq(price: Long?): BooleanExpression? =
        price?.let { product.price.eq(price) }

    private fun productTypeEq(type: Product.Type?): BooleanExpression? =
        type?.let { product.type.eq(type) }

    // 수정, 삭제 벌크 연산
    @Test
    @Transactional
    @Rollback(value = false)
    fun updateTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        // bulk 연산은 영속성 컨텍스트(1차 캐시)를 무시하고 바로 DB의 값을 바꿔준다.
        val count = queryFactory.update(product)
            .set(product.type, Product.Type.BASIC)
            .where(
                product.type.eq(Product.Type.CUSTOM).or
                    (product.type.eq(Product.Type.EVENT))
            ).execute()


        println("[count]: $count")

        // [영속성 컨텍스트] Product(id=1, type=BASIC)    ->   [DB] Product(id=1, type=BASIC)
        // [영속성 컨텍스트] Product(id=2, type=CUSTOM)   ->   [DB] Product(id=2, type=BASIC)
        // [영속성 컨텍스트] Product(id=3, type=EVENT)    ->   [DB] Product(id=3, type=BASIC)
        // [영속성 컨텍스트]가 항상 우선권을 가진다.
        val beforeFlush = queryFactory.selectFrom(product)
            .fetch()

        println("[before]: $beforeFlush")
        // [before]: [Product(id=1, name='basic-1', type=BASIC), Product(id=2, name='custom-1', type=CUSTOM), Product(id=3, name='event-1', type=EVENT)]

        entityManager.flush();
        entityManager.clear();

        val afterFlush = queryFactory.selectFrom(product)
            .fetch()

        println("[after]: $afterFlush")
        // [after]: [Product(id=1, name='basic-1', type=BASIC), Product(id=2, name='custom-1', type=BASIC), Product(id=3, name='event-1', type=BASIC)]
    }

    @Test
    @Transactional
    @Rollback(value = false)
    fun deleteTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))

        val count = queryFactory.delete(product)
            .where(
                product.type.eq(Product.Type.CUSTOM).or
                    (product.type.eq(Product.Type.EVENT))
            ).execute()

        println("[count]: $count")

        entityManager.flush();
        entityManager.clear();

        val afterFlush = queryFactory.selectFrom(product)
            .fetch()

        println("[after]: $afterFlush")
    }

    @Test
    fun sqlFunctionReplaceSelectTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        val result: List<String> = queryFactory
            .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})", product.name, "-1", "-new"))
            .from(product)
            .fetch()

        for (str in result) {
            println("[result]: $str")
        }
    }

    @Test
    fun sqlFunctionUpperSelectTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        val result: List<String> = queryFactory
            .select(Expressions.stringTemplate("function('upper', {0})", product.name))
            .from(product)
            .fetch()

        for (str in result) {
            println("[result]: $str")
        }
    }

    @Test
    fun sqlFunctionUpperWhereTest() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "BASIC-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "CUSTOM-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        productRepository.save(Product(name = "EVENT-1", price = 1000, type = Product.Type.EVENT))
        val result: List<String> = queryFactory
            .select(product.name)
            .from(product)
            .where(
                product.name.eq(
                    Expressions.stringTemplate("function('upper', {0})", product.name)
                )
            ).fetch()

        for (str in result) {
            println("[result]: $str")
        }
    }

    @Test
    fun sqlFunctionUpperWhereTest2() {
        productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "BASIC-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "CUSTOM-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        productRepository.save(Product(name = "EVENT-1", price = 1000, type = Product.Type.EVENT))
        val result: List<String> = queryFactory
            .select(product.name)
            .from(product)
            .where(product.name.eq(product.name.upper()))
            .fetch()

        for (str in result) {
            println("[result]: $str")
        }
    }

    @Test
    fun queryProjectionDtoTest() {
        // given
        val product1 = productRepository.save(Product(name = "basic-1", price = 3000, type = Product.Type.BASIC))
        productRepository.save(Product(name = "custom-1", price = 2000, type = Product.Type.CUSTOM))
        productRepository.save(Product(name = "event-1", price = 1000, type = Product.Type.EVENT))
        orderRepository.save(OrderStub.getOrder(productId = product1.id))
        orderRepository.save(OrderStub.getOrder(productId = product1.id))
        orderRepository.save(OrderStub.getOrder(productId = product1.id))
        orderRepository.save(OrderStub.getOrder(productId = product1.id))
        orderRepository.save(OrderStub.getOrder(productId = product1.id))

        val builder = BooleanBuilder()
        val productId: Long = 1L
        val productName: String? = null
        val productType: Product.Type = Product.Type.BASIC
        val orderId: Long? = null
        val orderStatus: Order.Status? = null

        productId?.let { builder.and(product.id.eq(productId)) }
        productName?.let { builder.and(product.name.eq(productName)) }
        productType?.let { builder.and(product.type.eq(productType)) }
        orderId?.let { builder.and(order.id.eq(orderId)) }
        orderStatus?.let { builder.and(order.status.eq(orderStatus)) }

        // when
        val result: List<ProductOrderDto> = queryFactory
            .select(
                QProductOrderDto(
                    product.id,
                    product.name,
                    product.type,
                    order.id,
                    order.status
                )
            ).from(product)
            .leftJoin(order)
            .on(order.productId.eq(product.id))
            .where(builder)
            .fetch()

        // then
        for (dto in result) {
            println("[dto]: $dto")
        }
    }
}