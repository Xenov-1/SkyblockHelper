package com.ovi.skyblock.dialogs;

import static com.ovi.skyblock.dialogs.SetAPIKeyDialog.builder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ovi.skyblock.R;
import com.ovi.skyblock.utilities.SkyblockEvent;
import com.ovi.skyblock.utilities.TimeFormatChanger;

public class MayorPerksDialog {
    public static AlertDialog dialog;
    private static final SkyblockEvent[] nextEvents = SkyblockEvent.SINGLE_NEXT_EVENTS;

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @SuppressLint("SetTextI18n")
    public static void showDialog(Context context, String perks, String name, String time) {
        MayorPerksDialog.context = context;
        builder = new AlertDialog.Builder(context);
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.mayor_perks, null);
        builder.setView(dialogView);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView mayor_perks = dialogView.findViewById(R.id.mayor_perks2);
        mayor_perks.setText(Html.fromHtml(perks));
        TextView mayor_perks_title = dialogView.findViewById(R.id.mayor_perks_title);
        mayor_perks_title.setText(name + " Perks");

        TextView timer = dialogView.findViewById(R.id.remaining_time);
        if (time.equals("Current")) {
            final Handler handler = new Handler();
            final Runnable updateTime = new Runnable() {
                @Override
                public void run() {
                    SkyblockEvent firstEvent = nextEvents[1];
                    String time = "<font color='#cc6c60'>Remaining Time: </font>"  + "<font color='#19bf8d'>" + TimeFormatChanger.convertTimestamp(firstEvent.getSecondsToNextOccurrence());
                    timer.setText(Html.fromHtml(time));
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(updateTime);
        }
        if (time.equals("Next")) {{
            final Handler handler = new Handler();
            final Runnable updateTime = new Runnable() {
                @Override
                public void run() {

                    SkyblockEvent[] nextEvents = SkyblockEvent.SINGLE_NEXT_EVENTS;
                    SkyblockEvent firstEvent = nextEvents[1];
                    String time = "<font color='#4287f5'>Mayor " + name + " in:  </font>"  + "<font color='#19bf8d'>" + TimeFormatChanger.convertTimestamp(firstEvent.getSecondsToNextOccurrence());

                    timer.setText(Html.fromHtml(time));
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(updateTime);

        }}


        Button dismiss_button = dialogView.findViewById(R.id.dismiss_button);

        dismiss_button.setOnClickListener(v -> DismissDialog.dismissDialog(dialog));
    }
}

