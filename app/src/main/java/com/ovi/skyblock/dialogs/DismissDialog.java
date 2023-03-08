package com.ovi.skyblock.dialogs;

import android.app.AlertDialog;

public class DismissDialog {
    public static void dismissDialog(AlertDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

