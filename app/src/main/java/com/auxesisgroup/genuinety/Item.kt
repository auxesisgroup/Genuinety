package com.auxesisgroup.genuinety

data class Item(
    val id: Int = 0,
    val client_id: Int = 0,
    val code: String = "",
    val name: String = "",
    val merchant: String = "",
    val url: String = "",
    val scAddress: String = "",
    val scTxHash: String = "",
    val details: List<Detail> = listOf()
)

data class Detail(
    val id: Int = 0,
    val heading: String = "",
    val content: String = "",
    val item_id: Int = 0
)