package com.example.querydsl.entity

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val price: Long,
    @Enumerated(value = EnumType.STRING)
    val type: Type = Type.BASIC
) {
    enum class Type {
        BASIC,
        CUSTOM,
        EVENT
    }

    override fun toString(): String {
        return "Product(id=$id, name='$name', type=$type)"
    }
}
