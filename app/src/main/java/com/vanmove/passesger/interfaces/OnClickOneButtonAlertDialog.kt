package com.vanmove.passesger.interfaces

import android.content.DialogInterface

interface OnClickOneButtonAlertDialog {
    fun clickPositiveDialogButton(
        dialog_name: String?,
        dialog: DialogInterface?
    )
}