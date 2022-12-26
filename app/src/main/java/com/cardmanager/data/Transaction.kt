package com.cardmanager.data

data class Transaction(
    val amount: Double = 0.0,
    val cardId: String = "",
    val type: TRANSACTION_TYPE = TRANSACTION_TYPE.TOPUP,
    val employeeId: String = "",
    val timestamp: Long = 0,
    val attractionId: String = ""
)

enum class TRANSACTION_TYPE {
    TOPUP, SPEND, REFUND
}