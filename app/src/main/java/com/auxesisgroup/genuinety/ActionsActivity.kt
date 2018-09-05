package com.auxesisgroup.genuinety

import android.graphics.Color
import android.graphics.Typeface.DEFAULT_BOLD
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
import org.jetbrains.anko.support.v4.space
import kotlin.properties.Delegates


class ActionsActivity: AppCompatActivity(), ApiCallback {

    private val explorerUrl = "https://testnet.auxledger.org/#/transaction/"

    private val clientId = 100023
    private var contractAddress = ""
    private var contractTxHash = ""
    private var item: Item = Item()
    private var itemCode: String by Delegates.notNull()
    private var isRemoveCall = false
    private var isAddedAlready = false
    private var isUpdateCall = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actions)

        pd = indeterminateProgressDialog("Executing network request..Please wait...")

        itemCode = intent.extras["itemCode"].toString()

        WebService.getItem("$clientId", itemCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { res ->
                            when (res) {
                                is Item -> {
                                    item = Item(res.id, res.client_id, res.code, res.name, res.merchant, res.url, res.scAddress, res.scTxHash, res.details)
                                    when (itemCode) {
                                        res.code -> {
                                            Log.e("isCreated", "TAG is created")
                                            val details: String = try {
                                                res.details[0].content
                                            } catch (ioobe: IndexOutOfBoundsException) {
                                                "..."
                                            }
                                            contractAddress = res.scAddress
                                            contractTxHash = res.scTxHash
                                            initView()
                                            btnDeploy.visibility = View.VISIBLE
                                            btnUpdateDetails.visibility = View.VISIBLE
                                            btnViewDetails.visibility = View.VISIBLE
                                            tagView.visibility = View.VISIBLE
                                            tagDetails.text = """
                                                Item Code: ${res.code}
                                                Item Name: ${res.name}
                                                Merchant Name: ${res.merchant}
                                                URL: ${res.url}
                                                SC Address: ${res.scAddress}
                                                SC TxHash: ${res.scTxHash}
                                                Details: $details""".trimIndent()
                                        }
                                    }
                                }
                            }
                        },
                        { err ->
                            Log.e("isCreated","TAG is not created")
                            Log.e("isCreated","Attempting to create")
                            dismissProgressBar()
                            val itemToCreate = Item(itemCode.toInt(), clientId, itemCode, item.name, item.merchant, item.url, item.scAddress, item.scTxHash, listOf(Detail(0,"...", "...", itemCode.toInt())))
                            WebService.addItem(itemToCreate)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            { resFirst ->
                                                initView()
                                                Log.d(" Add First Response", resFirst.toJSONLike())
                                            },
                                            { errFirst ->
                                                Log.e("Add First Error", errFirst.toJSONLike())
                                            }
                                    )
                            Log.e("Other Error", err.toJSONLike())
                        }
                )

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
            showProgressBar()
            isRemoveCall = false
            isUpdateCall = false
            isAddedAlready = false
            WebService.getItemForUpdate("$clientId", itemCode, this@ActionsActivity)
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

                        val content = textInputLayout {
                            hint = "Details Value"
                            textInputEditText()
                        }

                        positiveButton("Update Details") {
                            val itemUpdate = Item(item.id, clientId, item.code, name.getInput(), merchant.getInput(), linkUrl.getInput(), contractAddress, contractTxHash,
                                    listOf(Detail(item.details[0].id, "Details", content.getInput(), item.id)))

                            Log.e("ITEM UPDATE", itemUpdate.toJSONLike())
                            showProgressBar()
                            WebService.updateItemObservable(itemUpdate)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            { res ->
                                                Log.e("Details Updated", res.toJSONLike())
                                            },
                                            { err ->
                                                Log.e("Error", err.toJSONLike())
                                            }
                                    )
                            isUpdateCall = true
                            setItemDetails(contractAddress, name.getInput(), merchant.getInput(), linkUrl.getInput(), content.getInput())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            { res ->
                                                dismissProgressBar()
                                                alert {
                                                    customView {
                                                        title = "Item Details Updated Successfully"
                                                        verticalLayout {
                                                            padding = dip(20)
                                                            textView("Your transaction id is :\n${res.transactionHash}") {
                                                                typeface = DEFAULT_BOLD
                                                                textSize = sp(6).toFloat()
                                                            }
                                                            space().lparams(height = dip(16))

                                                            button("View on Auxledger Blockchain") {
                                                                textSize = sp(8).toFloat()
                                                                textColor = Color.WHITE
                                                                background = ContextCompat.getDrawable(ctx, R.drawable.button_rounded_blue)
                                                                onClick { browse("$explorerUrl${res.transactionHash}") }
                                                            }
                                                        }
                                                    }
                                                    positiveButton("GO BACK") {}
                                                }.show()
                                                Log.d("TxHash", res.transactionHash)
                                            },
                                            { err ->
                                                Log.e("Error", err.toJSONLike())
                                            }
                                    )
                        }
                    }
                }
            }.show()
        }

        btnViewDetails.onClick {
            showProgressBar()
            getItemDetails(contractAddress)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { res ->
                                dismissProgressBar()
                                alert {
                                    customView {
                                        scrollView {
                                            verticalLayout {
                                                padding = dip(20)
                                                textView("Data from Blockchain") {
                                                    typeface = DEFAULT_BOLD
                                                    textColor = Color.BLUE
                                                    textSize = sp(8).toFloat()
                                                }
                                                space().lparams(height = dip(6))
                                                textView("Item Code: ${res?.value2}") {textSize = sp(6).toFloat()}
                                                textView("Item Name: ${res?.value3}") {textSize = sp(6).toFloat()}
                                                textView("Merchant Name: ${res?.value4}") {textSize = sp(6).toFloat()}
                                                textView("URL: ${res?.value5}") {textSize = sp(6).toFloat()}
                                                textView("Details: ${res?.value6}") {textSize = sp(6).toFloat()}

                                                space().lparams(height = dip(30))

                                                textView("Data from Genuinety") {
                                                    typeface = DEFAULT_BOLD
                                                    textColor = Color.parseColor("#008000")
                                                    textSize = sp(8).toFloat()
                                                }
                                                space().lparams(height = dip(6))
                                                textView("Item Code: ${res?.value2}") {textSize = sp(6).toFloat()}
                                                textView("Item Name: ${item.name}") {textSize = sp(6).toFloat()}
                                                textView("Merchant Name: ${item.merchant}") {textSize = sp(6).toFloat()}
                                                textView("URL: ${item.url}") {textSize = sp(6).toFloat()}
                                                textView("Details: ${item.details[0].content}") {textSize = sp(6).toFloat()}

                                                space().lparams(height = dip(30))

                                                textView("Contract Address : $contractAddress") {
                                                    textSize = sp(6).toFloat()
                                                }
                                                space().lparams(height = dip(8))
                                                textView("Contract TxHash : $contractTxHash") {
                                                    textSize = sp(6).toFloat()
                                                }

                                                space().lparams(height = dip(16))
                                                button("View on Auxledger Blockchain") {
                                                    padding = dip(2)
                                                    textSize = sp(8).toFloat()
                                                    textColor = Color.WHITE
                                                    background = ContextCompat.getDrawable(ctx, R.drawable.button_rounded_blue)
                                                    onClick { browse("$explorerUrl$contractTxHash") }
                                                }
                                            }
                                        }
                                    }
                                }.show()
                                Log.d("Item Details", res.toString())
                            },
                            { err ->
                                toast("Error: Missing details. Try adding details first.")
                                Log.e("Error", err.toJSONLike())
                            }
                    )
        }

        if (contractAddress.contains("0x")) {
            btnViewDetailsInitial.visibility = View.VISIBLE
            btnViewDetailsInitial.onClick {
                showProgressBar()
                getItemDetails(contractAddress)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { res ->
                                    dismissProgressBar()
                                    alert {
                                        customView {
                                            scrollView {
                                                verticalLayout {
                                                    padding = dip(20)
                                                    textView("Data from Blockchain") {
                                                        typeface = DEFAULT_BOLD
                                                        textColor = Color.BLUE
                                                        textSize = sp(8).toFloat()
                                                    }
                                                    space().lparams(height = dip(6))
                                                    textView("Item Code: ${res?.value2}") {textSize = sp(6).toFloat()}
                                                    textView("Item Name: ${res?.value3}") {textSize = sp(6).toFloat()}
                                                    textView("Merchant Name: ${res?.value4}") {textSize = sp(6).toFloat()}
                                                    textView("URL: ${res?.value5}") {textSize = sp(6).toFloat()}
                                                    textView("Details: ${res?.value6}") {textSize = sp(6).toFloat()}

                                                    space().lparams(height = dip(30))

                                                    textView("Data from Genuinety") {
                                                        typeface = DEFAULT_BOLD
                                                        textColor = Color.parseColor("#008000")
                                                        textSize = sp(8).toFloat()
                                                    }
                                                    space().lparams(height = dip(6))
                                                    textView("Item Code: ${res?.value2}") {textSize = sp(6).toFloat()}
                                                    textView("Item Name: ${item.name}") {textSize = sp(6).toFloat()}
                                                    textView("Merchant Name: ${item.merchant}") {textSize = sp(6).toFloat()}
                                                    textView("URL: ${item.url}") {textSize = sp(6).toFloat()}
                                                    textView("Details: ${item.details[0].content}") {textSize = sp(6).toFloat()}

                                                    space().lparams(height = dip(30))

                                                    textView("Contract Address : $contractAddress") {
                                                        textSize = sp(6).toFloat()
                                                    }
                                                    space().lparams(height = dip(8))
                                                    textView("Contract TxHash : $contractTxHash") {
                                                        textSize = sp(6).toFloat()
                                                    }

                                                    space().lparams(height = dip(16))
                                                    button("View on Auxledger Blockchain") {
                                                        padding = dip(2)
                                                        textSize = sp(8).toFloat()
                                                        textColor = Color.WHITE
                                                        background = ContextCompat.getDrawable(ctx, R.drawable.button_rounded_blue)
                                                        onClick { browse("$explorerUrl$contractTxHash") }
                                                    }
                                                }
                                            }
                                        }
                                    }.show()
                                    Log.d("Item Details", res.toString())
                                },
                                { err ->
                                    toast("Error: Missing details. Try adding details first.")
                                    Log.e("Error", err.toJSONLike())
                                }
                        )
            }
        }
    }

    override fun <T> onResponse(res: T) {
        when {
            !isRemoveCall -> when (res) {
                is Item -> {

                    item = Item(res.id, res.client_id, res.code, res.name, res.merchant, res.url, res.scAddress, res.scTxHash, res.details)

                    when {
                        item.scAddress.contains("0x") -> {

                            dismissProgressBar()
                            contractAddress = item.scAddress
                            contractTxHash = item.scTxHash
                            btnUpdateDetailsLayout.visibility = View.VISIBLE
                            btnViewDetails.visibility = View.VISIBLE

                            isAddedAlready = true

                            when {
                                !isUpdateCall ->
                                    when {
                                        isAddedAlready -> alert {
                                            title = "Already Added to Blockchain"
                                            okButton {  }
                                            negativeButton("Remove contract address") {
                                                isRemoveCall = true
                                                val updatedItemUpdate = item.copy(scAddress = "...", scTxHash = "...", details = listOf(Detail(0, "...", "...", item.id)))
                                                WebService.updateItem(updatedItemUpdate, this@ActionsActivity)
                                                showProgressBar()
                                            }
                                        }.show()
                                    }
                                else -> showProgressBar()
                            }

                        }
                        else -> deployGenuinetySC(clientId.toBigInteger(), itemCode.toBigInteger())
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

                                            val updatedItemUpdateAfterDeploy = item.copy(scAddress = contractAddress, scTxHash = contractTxHash, details = listOf(Detail(0, "...", "...", item.id)))
                                            WebService.updateItemObservable(updatedItemUpdateAfterDeploy)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(
                                                            { res ->
                                                                Log.e("Updated after deploy", res.toJSONLike())
                                                            },
                                                            { err ->
                                                                Log.e("Error", err.toJSONLike())
                                                            }
                                                    )
                                        },
                                        { err ->
                                            Log.e("Error", err.toJSONLike())
                                        }
                                )
                    }
                }
                else -> alert {
                    title = "Response"
                    message = res.toJSONLike()
                    okButton {  }
                }.show()
            }
            else -> alert {
                dismissProgressBar()
                title = "Removal Successful!"
                okButton {  }
            }.show()
        }
    }

    override fun <T> onError(err: T) {
        toast(err.toString())
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