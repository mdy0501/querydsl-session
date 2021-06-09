package com.example.querydsl.entity

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val productId: Long,
    @Enumerated(value = EnumType.STRING)
    val status: Status
) {
    enum class Status {
        PURCHASED, // 구매 완료 상태
        PENDING, // 구매 요청 상태
        CANCELLED
    }
}