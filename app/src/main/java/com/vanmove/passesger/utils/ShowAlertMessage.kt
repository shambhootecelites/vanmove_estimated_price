package com.vanmove.passesger.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.vanmove.passesger.interfaces.OnAlertDialogButtonClicks
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog2


object ShowAlertMessage {
    private var alertDialog_interface: OnAlertDialogButtonClicks? = null

    fun setOnAlertDialogButtonClicks(alertDialog_interfac: OnAlertDialogButtonClicks?) {
        alertDialog_interface = alertDialog_interfac
    }

    fun showAlertMessage(context: Context?, message: String?) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showAlertMessageWithTitle(
        context: Context?,
        title: String?,
        message: String?
    ) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setPositiveButton("Close") { dialog, which -> }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun showNetworkAlertMessage(context: Context) {
        val builder =
            AlertDialog.Builder(context)
        builder.setTitle("Network Connection Required")
        builder.setMessage("Please Enable Your Mobile Network!")
        builder.setPositiveButton("Setting") { dialog, which ->
            context.startActivity(
                Intent(
                    Settings.ACTION_WIFI_SETTINGS
                )
            )
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    fun showAlertMessageTwoButtons(
        context: Context?, title: String?, message: String?,
        btn_pos: String?, btn_neg: String?
    ) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(btn_pos) { dialog, which -> alertDialog_interface!!.onDialogPositiveButtonPressed() }
        builder.setNegativeButton(btn_neg) { dialog, which -> alertDialog_interface!!.onDialogNegativeButtonPressed() }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    fun showAlertMessageOneButtons(
        context: Context?, title: String?, message: String?,
        btn_name: String?
    ) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(btn_name) { dialog, which -> alertDialog_interface!!.onDialogPositiveButtonPressed() }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    fun showAlertMessageTwoButtons(
        context: Context?, title: String?, message: String?,
        btn_pos: String?, btn_neg: String?, dialog_name: String?,
        onClickTwoButtonsAlertDialog: OnClickTwoButtonsAlertDialog2
    ) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(btn_pos) { dialog, which ->
            onClickTwoButtonsAlertDialog.clickPositiveDialogButton(
                dialog_name,
                dialog
            )
        }
        builder.setNegativeButton(btn_neg) { dialog, which ->
            onClickTwoButtonsAlertDialog.clickNegativeDialogButton(
                dialog_name,
                dialog
            )
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    fun showAlertMessageThreeButtons(
        context: Context?,
        title: String?, message: String?,
        btn_pos: String?, btn_neg: String?, btn_net: String?,
        dialog_name: String?,
        onClickTwoButtonsAlertDialog: OnClickTwoButtonsAlertDialog2
    ) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(btn_pos) { dialog, which ->
            onClickTwoButtonsAlertDialog.clickPositiveDialogButton(
                dialog_name,
                dialog
            )
        }
        builder.setNegativeButton(btn_neg) { dialog, which ->
            onClickTwoButtonsAlertDialog.clickNegativeDialogButton(
                dialog_name,
                dialog
            )
        }
        builder.setNeutralButton(btn_net) { dialog, which ->
            onClickTwoButtonsAlertDialog.clickNeutralButtonDialogButton(
                dialog_name,
                dialog
            )
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }
}