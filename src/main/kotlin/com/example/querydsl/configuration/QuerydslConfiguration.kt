package com.example.querydsl.configuration

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class QuerydslConfiguration(
    @PersistenceContext
    val entityManager: EntityManager
) {
    @Bean
    fun jpqQueryFactory(): JPAQueryFactory =
        JPAQueryFactory(entityManager)
}