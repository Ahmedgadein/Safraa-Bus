package com.dinder.rihlabus.data.model

data class User(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val company: Company
) {
    fun toJson(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "phoneNumber" to phoneNumber,
        "company" to company.toJson()
    )

    companion object {
        fun fromJson(json: Map<String, Any>): User =
            User(
                id = json["id"].toString(),
                name = json["name"].toString(),
                phoneNumber = json["phoneNumber"].toString(),
                company = Company.fromJson(json["company"] as Map<String, String>)
            )
    }
}
