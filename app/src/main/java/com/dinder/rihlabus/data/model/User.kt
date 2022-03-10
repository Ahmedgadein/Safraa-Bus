package com.dinder.rihlabus.data.model

data class User(
    val id: String,
    val name: String,
    val phone: String,
    val company: Company?
)