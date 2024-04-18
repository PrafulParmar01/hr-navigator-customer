package com.hr.navigator.customer.ui.profile

data class CompanyModel(
    var companyName: String = "",
    var email: String = "",
    var phone: String = "",
    var address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var addressLine: String = "",
    var companyId: String = "",
    var type: String = "",
) {
    // Default no-argument constructor required by Firebase
    constructor() : this("", "", "", "", "", "", "", "", "")
}