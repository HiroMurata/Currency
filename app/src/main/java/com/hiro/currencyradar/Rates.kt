package com.hiro.currencyradar

import java.util.*

data class Rates(
    val base: String,
    val date: String,
    val rates:  List<Rate>
)

data class Rate (
//    val currency: Currency,
    val currencyCode: String,
    val value: Double

)
