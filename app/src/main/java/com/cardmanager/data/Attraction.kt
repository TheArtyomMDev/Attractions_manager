package com.cardmanager.data

import java.util.*

data class Attraction(
    var name: String = "",
    var price: Double = 0.0,
    var id: String = UUID.randomUUID().toString()
)