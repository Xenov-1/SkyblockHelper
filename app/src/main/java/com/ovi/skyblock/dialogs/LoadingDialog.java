package com.ovi.skyblock.dialogs;

import static com.ovi.skyblock.dialogs.SetAPIKeyDialog.builder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.ovi.skyblock.R;


public class LoadingDialog {
    public static AlertDialog dialog;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static void showLoadingDialog(Context context) {
        LoadingDialog.context = context;
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.loading_screen, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }
}
