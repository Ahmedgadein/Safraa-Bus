package com.dinder.rihlabus.utils

import androidx.room.TypeConverter
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.google.gson.Gson

class CompanyConverter {
    @TypeConverter
    fun companyToJson(company: Company): String =
        Gson().toJson(company)

    @TypeConverter
    fun jsonToCompany(json: String): Company =
        Gson().fromJson(json, Company::class.java)
}

class DestinationConverter {
    @TypeConverter
    fun destinationToJson(destination: Destination): String =
        Gson().toJson(destination)

    @TypeConverter
    fun jsonToDestination(json: String): Destination =
        Gson().fromJson(json, Destination::class.java)
}
