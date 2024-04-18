package com.hr.navigator.customer.ui.home

import com.hr.navigator.customer.ui.profile.CompanyModel

data class GeofenceEntryModel(
    val firstName :String="",
    val lastName :String="",
    val designation :String="",
    val phone :String="",
    val latitude :String="",
    val longitude :String="",
    val addressLine :String="",
    val entryTime : String = "",
    val exitTime : String = "",
    val companyModel: CompanyModel? = null,
)