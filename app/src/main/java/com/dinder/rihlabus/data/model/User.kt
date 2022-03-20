package com.dinder.rihlabus.data.model

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val company: Company
)

fun User.toJson():Map<String,Any> = mapOf(
    "id" to id,
    "name" to name,
    "phoneNumber" to phone,
    "company" to company.toJson()
)