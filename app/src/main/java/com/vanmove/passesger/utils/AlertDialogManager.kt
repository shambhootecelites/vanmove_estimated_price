package com.vanmove.passesger.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnClickOneButtonAlertDialog
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog

object AlertDialogManager {
    private var click_two_btn_dialog_listener: OnClickTwoButtonsAlertDialog? = null
    private var click_one_btn_dialog_listener: OnClickOneButtonAlertDialog? = null
    fun showAlertMessage(context: Context?, message: String?) {
        val builder =
            AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.text_Ok) { dialog, which -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showAlertMessage_fixed(
        context: Context?, message: String?, dialog_name: String?,
        click_two_btn_listener: OnClickTwoButtonsAlertDialog?
    ) {
        val builder =
            AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
        builder.setTitle("Alert")
        builder.setMessage(message)
        click_two_btn_dialog_listener = click_two_btn_listener
        builder.setPositiveButton(R.string.text_Ok) { dialog, which ->
            click_two_btn_dialog_listener!!.clickPositiveDialogButton(
                dialog_name
            )
        }
        builder.setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showAlertMessageWithTitle(
        context: Context?,
        title: String?,
        message: String?
    ) {
        val builder =
            AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setPositiveButton("Ok") { dialog, which -> }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showAlertMessageWithTwoButtons(
        context: Context?,
        click_two_btn_listener: OnClickTwoButtonsAlertDialog?,
        dialog_name: String?, title: String?, message: String?,
        btn_pos_name: String?, btn_neg_name: String?
    ) {
        val builder =
            AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setCancelable(false)
        click_two_btn_dialog_listener = click_two_btn_listener
        builder.setPositiveButton(btn_pos_name) { dialog, which ->
            click_two_btn_dialog_listener!!.clickPositiveDialogButton(
                dialog_name
            )
        }
        builder.setNegativeButton(btn_neg_name) { dialog, which ->
            click_two_btn_dialog_listener!!.clickNegativeDialogButton(
                dialog_name
            )
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showAlertMessageWithOneButtons(
        context: Context?, click_one_btn_listener: OnClickOneButtonAlertDialog?,
        dialog_name: String?, title: String?, message: String?,
        btn_pos_name: String?
    ) {
        val builder =
            AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
        builder.setMessage(message)
        builder.setTitle(title)
        click_one_btn_dialog_listener = click_one_btn_listener
        builder.setPositiveButton(btn_pos_name) { dialog, which ->
            click_one_btn_dialog_listener!!.clickPositiveDialogButton(
                dialog_name,
                dialog
            )
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}