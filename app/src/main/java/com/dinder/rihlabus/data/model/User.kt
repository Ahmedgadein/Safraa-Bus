package com.dinder.rihlabus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val phoneNumber: String,
    val company: Company? = null,
    val location: Destination? = null,
    @ColumnInfo(defaultValue = "0")
    val verified: Boolean = false
) {
    fun toJson(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "phoneNumber" to phoneNumber,
        "company" to company?.toJson(),
        "location" to location?.toJson(),
        "verified" to verified
    )

    companion object {
        fun fromJson(json: Map<String, Any>): User =
            User(
                id = json["id"].toString(),
                name = json["name"].toString(),
                phoneNumber = json["phoneNumber"].toString(),
                company = Company.fromJson(json["company"] as Map<String, Any>),
                location = Destination.fromJson(json["location"] as Map<String, Any>),
                verified = json["verified"] as Boolean? ?: false
            )
    }
}
