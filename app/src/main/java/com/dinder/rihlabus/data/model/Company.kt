package com.dinder.rihlabus.data.model

data class Company(
    val name: String,
    val arabicName: String? = null
) {
    fun toJson(): Map<String, Any> = mapOf(
        "name" to name,
        "arabicName" to name
    )

    companion object {
        fun fromJson(json: Map<String, Any>): Company {
            return Company(
                name = json["name"].toString(),
                arabicName = json["arabicName"].toString()
            )
        }
    }
}
