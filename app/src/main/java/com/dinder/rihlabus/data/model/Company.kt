package com.dinder.rihlabus.data.model

import com.dinder.rihlabus.common.DropDownItem

data class Company(
    override val name: String,
    override val arabicName: String
) : DropDownItem {
    fun toJson(): Map<String, Any> = mapOf(
        "name" to name,
        "arabicName" to arabicName
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
