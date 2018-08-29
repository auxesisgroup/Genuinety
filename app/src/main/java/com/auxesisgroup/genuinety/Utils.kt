package com.auxesisgroup.genuinety

import android.app.ProgressDialog
import android.support.design.widget.TextInputLayout
import kotlin.properties.Delegates

var pd: ProgressDialog by Delegates.notNull()

fun showProgressBar() {
    pd.show()
}

fun hideProgressBar() {
    pd.hide()
}

fun dismissProgressBar() {
    pd.dismiss()
}

fun <T> T.toJSONLike() = this.toString().replace("=", ":")

fun TextInputLayout.getInput(): String = this.editText?.editableText.toString().trim()