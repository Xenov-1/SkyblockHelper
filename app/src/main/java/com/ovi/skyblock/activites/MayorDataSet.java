package com.ovi.skyblock.activites;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import com.ovi.skyblock.R;
import com.ovi.skyblock.dialogs.LoadingDialog;

public class MayorDataSet {

    public static SharedPreferences sp;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static String CurrentMayorName;
    public static String NextMayorName;
    public static String CurrentMayorPerks;
    public static String NextMayorPerks;

    public static void setData(Context context) {
        sp = context.getSharedPreferences("MayorData",MODE_PRIVATE);
        if (sp.getAll().isEmpty()) {
            LoadingDialog.showLoadingDialog(context);
        } else {
            CurrentMayorName = sp.getString("CurrentMayorName", null);
            NextMayorName = sp.getString("NextMayorName", null);
            CurrentMayorPerks = sp.getString("CurrentMayorPerks", null);
            NextMayorPerks = sp.getString("NextMayorPerks", null);

            TextView currentMayorNameView = ((Activity)context).findViewById(R.id.current_mayor_name);
            ImageView currentMayorSkinView = ((Activity)context).findViewById(R.id.current_mayor_skin);
            TextView nextMayorNameView = ((Activity)context).findViewById(R.id.next_mayor_name);
            ImageView nextMayorSkinView = ((Activity)context).findViewById(R.id.next_mayor_skin);
            currentMayorNameView.setText(CurrentMayorName);

            @SuppressLint("DiscouragedApi") int currentMayor = context.getResources().getIdentifier(CurrentMayorName.toLowerCase(), "drawable", context.getPackageName());
            currentMayorSkinView.setImageResource(currentMayor);

            nextMayorNameView.setText(NextMayorName);
            @SuppressLint("DiscouragedApi") int nextMayor = context.getResources().getIdentifier(NextMayorName.toLowerCase(), "drawable", context.getPackageName());
            nextMayorSkinView.setImageResource(nextMayor);
        }
    }
}
