package com.hr.navigator.customer.utils

object ApiConstant {

    const val BASE_URL = "https://api.dev.okboys.in/"
    const val API_GENERATE_OTP = "login/otp"
    const val API_VERIFY_OTP = "login/login"

    /*HOME VIEW MODEL*/
    const val API_GET_USER_BY_ID = "query/user/get-user/{id}"
    const val API_USER_DUTY_STATUS = "user/update-duty-status"

    var IS_TOKEN_PASSED = false

    // ORDER LIST
    const val API_GET_ORDER_LIST = "query/order/delivery-boy/get-broadcasted-orders"
    const val API_GET_ORDER_ACCEPTED_LIST = "query/order/delivery-boy/get-accepted-order"
    const val API_ORDER_ACCEPT_REJECT = "order/process"
    const val API_ORDER_HISTORY = "query/order/delivery-boy/history"

    //PAYMENT REQUEST
    const val API_PAYMENT_REQUEST = "order/process"
    const val API_PAYMENT_CONFIRMED = "order/process"
    const val API_UPLOAD_INVOICE_IMAGE_EVENT = "order/process"
    const val API_ORDER_DELIVERED_EVENT = "order/process"

    // TRACKING
    const val API_UPDATE_TRACKING_POSSITION = "tracking/position"

    // DOCUMENT UPLOAD
    const val API_DOCUMENT_GENERATE_PSURL = "document/generate-psurl"
    const val API_DOCUMENT_UPLOAD_INVOICE_IMAGE = "document/upload-invoice-image"

    // PREFERENCES
    const val API_PREFERENCES = "query/preference/get-preferences"


    //CHAT
    const val API_ADD_COMMENT = "document/add-comment"
    const val API_GET_COMMENT = "document/get-comment"
    //const val API_UPLOAD_IMAGE = "document/upload-image"
    const val API_UPLOAD_IMAGE = "document/upload-chunk"
    const val API_UPLOAD_PAYMENT_IMAGE = "document/upload-payment-image"


    const val APP_TYPE = "OK-BOYS-DELIVERY-BOY"

    /*ORDER REQUEST*/
    const val ORDER_ACCEPTED = "ORDER_ACCEPTED"
    const val ORDER_ACCEPT = "ORDER_ACCEPT"
    const val ORDER_REJECT = "ORDER_REJECT"
    const val ORDER_AT_PICKUP_POINT = "AT_PICKUP_POINT"
    const val ORDER_UPLOAD_INVOICE = "UPLOAD_INVOICE"
    const val ORDER_PAYMENT_REQUEST = "REQUEST_PAYMENT"
    const val ORDER_PAYMENT_STARTED = "PAYMENT_STARTED"
    const val ORDER_PAYMENT_CONFIRMED = "PAYMENT_CONFIRMED"
    const val ORDER_JOB_COMPLETED = "JOB_COMPLETED"
    const val ORDER_BROADCASTED = "BROADCASTED"
    const val ORDER_DELIVERED = "ORDER_DELIVERED"
    const val ORDER_RECEIVED = "ORDER_RECEIVED"
    const val ORDER_COMPLETED = "ORDER_COMPLETED"
    const val ORDER_AT_DELIVERY_POINT = "AT_DELIVERY_POINT"
    const val ORDER_PAYMENT_TO_DELIVERY_BOY = "PAYMENT_TO_DELIVERY_BOY"
    const val ORDER_PAY_SERVICE_FEE = "PAY_SERVICE_FEE"

    const val ORDER_PAYMENT_FAILED = "PAYMENT_FAILED"
    const val MAKE_MANUAL_PAYMENT = "MAKE_MANUAL_PAYMENT"
    const val JOB_CREATED = "JOB_CREATED"


    const val INTENT_PICK_CAMERA = 309
    const val INTENT_PICK_GALLERY = 310

    const val FASTEST_UPDATE_INTERVAL_MILLIS = 20000L
    const val FASTEST_UPDATE_DELAY_MILLIS = 20000L

    const val API_RETRY_DELAY = 5000
    const val API_RETRY_COUNT = 3
    const val TAKE_UNTIL_DELAY = 30L


    const val NO_CONNECTION = "NO_INTERNET_CONNECTION"
    const val IS_HISTORY = "HISTORY"
    const val IS_ORDER = "ORDER"
    const val IS_HOME = "HOME"


    //TODO... change upiHandle
    const val UPI_HANDLE = "sudarshanvnagaraj@okhdfcbank"

}