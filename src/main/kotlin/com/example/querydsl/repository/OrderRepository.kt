package com.example.querydsl.repository

import com.example.querydsl.entity.Order
import com.example.querydsl.entity.QOrder.order
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class OrderRepository(
    private val jpaOrderRepository: JpaOrderRepository,
    private val queryFactory: JPAQueryFactory
) {

    fun findAll(): List<Order> =
        queryFactory
            .select(order)
            .from(order)
            .orderBy(order.id.desc())
            .fetch()

    fun save(order: Order) = jpaOrderRepository.save(order)

    // fetchFirst == limit(1).fetchOne()
    @Transactional(readOnly = true)
    fun exist(orderId: Long): Boolean {
        val fetchOne = queryFactory
            .selectOne()
            .from(order)
            .where(order.id.eq(orderId))
            .fetchFirst()

        return fetchOne != null
    }
}

interface JpaOrderRepository : JpaRepository<Order, Long>