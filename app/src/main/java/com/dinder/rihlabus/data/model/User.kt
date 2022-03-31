package com.dinder.rihlabus.data.model

data class User(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val company: String = "",
    val location: String = ""
) {
    fun toJson(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "phoneNumber" to phoneNumber,
        "company" to company,
        "location" to location
    )

    companion object {
        fun fromJson(json: Map<String, Any>): User =
            User(
                id = json["id"].toString(),
                name = json["name"].toString(),
                phoneNumber = json["phoneNumber"].toString(),
                company = json["company"].toString(),
                location = json["location"].toString()
            )
    }
}
