package com.auxesisgroup.genuinety

import android.app.ProgressDialog
import android.support.design.widget.TextInputLayout
import io.reactivex.Observable
import org.web3j.protocol.core.RemoteCall
import java.lang.Exception
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

fun <T> RemoteCall<T>.newObservable() : Observable<T> {
    return Observable.create { subscriber ->
        try {
            subscriber.onNext(this.send())
            subscriber.onComplete()
        } catch (e: Exception) {
            subscriber.onError(Throwable(e.message))
        }
    }
}