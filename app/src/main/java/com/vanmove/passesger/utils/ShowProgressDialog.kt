package com.vanmove.passesger.utils

import android.content.Context
import com.kaopiz.kprogresshud.KProgressHUD

object ShowProgressDialog {


    private var progressDialog: KProgressHUD? = null

    fun showDialog2(
        context: Context?
    ) {
        try {
            progressDialog = DialogUtils.showProgressDialog(context!!, cancelable = false)
            progressDialog!!.show()

        } catch (error: Exception) {
            print(error)
        }
    }

    fun closeDialog() {
        try {
            progressDialog!!.dismiss()
        } catch (error: Exception) {
            print(error)
        }
    }
}