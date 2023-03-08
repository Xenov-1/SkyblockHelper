package com.ovi.skyblock.activites;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.widget.TextView;

import com.ovi.skyblock.R;
import com.ovi.skyblock.dialogs.LoadingDialog;

public class DataSet {

    public static SharedPreferences sp;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static String Auction;
    public static String Fetchur;
    public static String SBUpdates;

    public static void setData(Context context) {
        sp = context.getSharedPreferences("Data",MODE_PRIVATE);
        if (sp.getAll().isEmpty()) {
            LoadingDialog.showLoadingDialog(context);
        } else {
            Auction = sp.getString("ActiveAuction", null);
            Fetchur = sp.getString("FetchurToday", null);
            SBUpdates = sp.getString("SkyblockUpdates", null);

            TextView auctions_box = ((Activity)context).findViewById(R.id.ActiveAuctions);
            TextView fetchur_box = ((Activity)context).findViewById(R.id.FetchurToday);
            TextView updates_box = ((Activity)context).findViewById(R.id.Ndata);

            if (Auction != null) {
                auctions_box.setText(Html.fromHtml(Auction));
            }

            if (Fetchur != null) {
                fetchur_box.setText(Html.fromHtml(Fetchur));
            }

            if (SBUpdates != null) {
                updates_box.setText(Html.fromHtml(SBUpdates));
            }




        }
    }
}
