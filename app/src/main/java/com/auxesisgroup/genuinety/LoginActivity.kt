package com.auxesisgroup.genuinety

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.sensorberg.permissionbitte.BitteBitte
import com.sensorberg.permissionbitte.PermissionBitte
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import kotlin.properties.Delegates

class LoginActivity: AppCompatActivity(), BitteBitte {
    private var login : LinearLayout by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pd = indeterminateProgressDialog("Executing network request..Please wait...")
        hideProgressBar()

        PermissionBitte.ask(this,this)

        login = verticalLayout {

            gravity = Gravity.CENTER_HORIZONTAL
            padding = dip(30)

            imageView(R.drawable.logo_genuinety_transparent).lparams {
                leftMargin = dip(36)
                rightMargin = dip(36)
                topMargin = dip(16)
                bottomMargin = dip(32)
            }

            val name = textInputLayout {
                hint = "Username"
                textInputEditText {
                    maxLines = 1
                    singleLine = true
                }
            }
            val pass = textInputLayout {
                hint = "Password"
                textInputEditText {
                    maxLines = 1
                    singleLine = true
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }

            space().lparams(height = dip(60))

            button("Login") {
                textSize = sp(10).toFloat()
                textColor = android.graphics.Color.WHITE
                background = ContextCompat.getDrawable(ctx, R.drawable.button_rounded_blue)
                onClick {
                    if (name.getInput() == "a" && pass.getInput() == "a") {
                        login.visibility = View.GONE
                        setContentView(R.layout.activity_login)

                        btnScan.onClick {
                            startActivity<ScanActivity>()
                        }

                    } else {
                        longToast("Incorrect Credentials")
                        pass.editText?.error = "Check Your Credentials"
                    }
                }
            }

            textView("Powered By"){
                textSize = sp(4).toFloat()
                typeface = Typeface.DEFAULT_BOLD
            }.lparams {
                gravity = Gravity.CENTER
                topMargin = dip(48)
            }

            linearLayout {
                orientation = LinearLayout.VERTICAL

                imageView(R.drawable.logo_auxesis_name_transparent).lparams {
                    gravity = Gravity.CENTER
                    height = 130
                    width = 340
                }

            }
        }

    }

    override fun askNicer() {}

    override fun noYouCant() {}

    override fun yesYouCan() {}

    override fun onPause() {
        dismissProgressBar()
        super.onPause()
    }
}