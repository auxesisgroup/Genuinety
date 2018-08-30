package com.auxesisgroup.genuinety

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.huanhailiuxin.coolviewpager.CoolViewPager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_actions.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import kotlin.properties.Delegates


class ActionsActivity: AppCompatActivity() {

    private val explorerUrl = "https://testnet.auxledger.org/#/transaction/"

    private val clientId = 100023
    private var contractAddress = ""
    private var contractTxHash = ""
    private var itemCode: String by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actions)

        pd = indeterminateProgressDialog("Executing network request..Please wait...")

        itemCode = intent.extras["itemCode"].toString()

        initView()
        btnDeploy.visibility = View.VISIBLE
        btnUpdateDetails.visibility = View.VISIBLE
        btnViewDetails.visibility = View.VISIBLE

    }

    private fun initView() {
        dismissProgressBar()
        val views = mutableListOf<View>()

        views.add(0, btnDeployLayout)
        views.add(1, btnUpdateDetailsLayout)
        views.add(2, btnViewDetailsLayout)

        val btnAdapter = CoolAdapter(views)

        cvp.setScrollMode(CoolViewPager.ScrollMode.HORIZONTAL)
        cvp.adapter = btnAdapter

        cvp.addOnPageChangeListener(object : CoolViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(position: Int, p1: Float, p2: Int) {
                when (position) {
                    0 -> {
                        toolbarTitle.text = "Admin View"
                        toolbarTitle.textColor = Color.parseColor("#FF8000")
                        btnDeployLayout.visibility = View.VISIBLE
                        btnUpdateDetailsLayout.visibility = View.GONE
                        btnViewDetailsLayout.visibility = View.GONE
                    }
                    1 -> {
                        toolbarTitle.text = "Merchant View"
                        toolbarTitle.textColor = Color.parseColor("#0000FF")
                        btnDeployLayout.visibility = View.GONE
                        btnUpdateDetailsLayout.visibility = View.VISIBLE
                        btnViewDetailsLayout.visibility = View.GONE
                    }
                    2 -> {
                        toolbarTitle.text = "Customer View"
                        toolbarTitle.textColor = Color.parseColor("#008000")
                        btnDeployLayout.visibility = View.GONE
                        btnUpdateDetailsLayout.visibility = View.GONE
                        btnViewDetailsLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onPageSelected(p0: Int) {}

        })

        btnDeploy.onClick {
            deployGenuinetySC(clientId.toBigInteger(), itemCode.toBigInteger())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { scRes ->
                                dismissProgressBar()
                                contractAddress = scRes.contractAddress
                                contractTxHash = scRes.transactionReceipt.transactionHash

                                alert {
                                    title = "Added Successfully to Blockchain"
                                    message = "Contract Address : $contractAddress\n\nTxHash : $contractTxHash"
                                    customView {
                                        verticalLayout {
                                            padding = dip(20)
                                            button("View on Auxledger Blockchain") {
                                                padding = dip(2)
                                                textSize = sp(8).toFloat()
                                                textColor = Color.WHITE
                                                background = ContextCompat.getDrawable(ctx, R.drawable.button_rounded_blue)
                                                onClick { browse("$explorerUrl$contractTxHash") }
                                            }
                                        }
                                    }

                                }.show()
                                Log.d("Contract Address", contractAddress)
                                Log.d("TxHash", scRes.transactionReceipt.transactionHash)
                            },
                            { err ->
                                Log.e("Error", err.toJSONLike())
                            }
                    )
        }

        btnUpdateDetails.onClick {
            alert {
                customView {
                    verticalLayout {
                        padding = dip(20)

                        val name = textInputLayout {
                            hint = "Item Name"
                            textInputEditText()
                        }
                        val merchant = textInputLayout {
                            hint = "Merchant Name"
                            textInputEditText()
                        }

                        val linkUrl = textInputLayout {
                            hint = "URL"
                            textInputEditText()
                        }

                        /*val linkText = textInputLayout {
                            hint = "URL Text"
                            textInputEditText()
                        }*/

                        /*val heading = textInputLayout {
                            hint = "Details Key"
                            textInputEditText()
                        }*/
                        val content = textInputLayout {
                            hint = "Details Value"
                            textInputEditText()
                        }

                        positiveButton("Update Details") {
                            showProgressBar()
                        }
                    }
                }
            }.show()
        }

        btnViewDetails.onClick {
            showProgressBar()
        }
    }

    override fun onPause() {
        dismissProgressBar()
        super.onPause()
    }

    internal class CoolAdapter(private val views: List<View>) : PagerAdapter() {

        override fun getCount(): Int {
            return views.size
        }

        override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
            return view === `object`
        }

        @NonNull
        override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
            if (views[position].parent != null) {
                (views[position].parent as ViewGroup).removeView(views[position])
            }
            container.addView(views[position])
            return views[position]
        }

        override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}