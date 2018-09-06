package com.auxesisgroup.genuinety

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.jetbrains.anko.startActivity


class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
    }

    override fun handleResult(result: Result?) {
        val itemCode = "${result?.text}"
        finish()
        startActivity<ActionsActivity>("itemCode" to itemCode)
    }

    public override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    public override fun onPause() {
        scannerView?.stopCamera()
        super.onPause()
    }
}