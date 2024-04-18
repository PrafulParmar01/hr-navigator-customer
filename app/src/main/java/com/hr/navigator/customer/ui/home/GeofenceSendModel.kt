package com.hr.navigator.customer.ui.home

import com.hr.navigator.customer.ui.profile.CompanyModel


data class GeofenceSendModel(
    val enterTime :String="",
    val exitTime :String="",
    val companyModel: CompanyModel? = null,
)