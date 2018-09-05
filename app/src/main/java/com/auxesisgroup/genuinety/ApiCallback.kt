package com.auxesisgroup.genuinety

interface ApiCallback {
    fun <T> onResponse(res: T)
    fun <T> onError(err: T)
}