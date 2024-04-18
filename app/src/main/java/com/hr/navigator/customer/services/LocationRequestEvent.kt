package com.hr.navigator.customer.services

import android.location.Location

data class LocationRequestEvent(
    var isSuccess: Boolean = false,
    var location: Location?= null
)
