package com.dinder.rihlabus.data.model

data class Company(
    val name: String,
    val arabicName: String? = null,
    val location: String
) {
    fun toJson(): Map<String, Any> = mapOf(
        "name" to name,
        "arabicName" to name,
        "location" to location
    )

    companion object {
        fun fromJson(json: Map<String, String>): Company {
            return Company(
                name = json["name"].toString(),
                arabicName = json["arabicName"].toString(),
                location = json["location"].toString()
            )
        }
    }
}
