package com.vanmove.passesger.interfaces

import android.content.DialogInterface

interface OnClickTwoButtonsAlertDialog2 {
    fun clickPositiveDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    )

    fun clickNegativeDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    )

    fun clickNeutralButtonDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    )

}