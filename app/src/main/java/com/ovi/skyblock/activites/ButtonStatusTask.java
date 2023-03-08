package com.ovi.skyblock.activites;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.ovi.skyblock.R;

public class ButtonStatusTask {

    public static void StatusTask(Context context) {
        CardView Auctions = ((Activity) context).findViewById(R.id.Auctions);
        Auctions.setVisibility(ButtonStatusManager.getButtonStatus("auction_toggle", context) ? View.VISIBLE : View.GONE);
        CardView Fetchur = ((Activity) context).findViewById(R.id.Fetchur);
        Fetchur.setVisibility(ButtonStatusManager.getButtonStatus("fetchur_toggle", context) ? View.VISIBLE : View.GONE);
        CardView SBUpdates = ((Activity) context).findViewById(R.id.SBUpdatesButton);
        SBUpdates.setVisibility(ButtonStatusManager.getButtonStatus("SBUpdates_toggle", context) ? View.VISIBLE : View.GONE);

    }
}


