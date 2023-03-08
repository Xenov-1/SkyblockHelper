package com.ovi.skyblock;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

import com.ovi.skyblock.activites.ButtonStatusManager;
import com.ovi.skyblock.dialogs.SetAPIKeyDialog;
import com.ovi.skyblock.dialogs.SetUsernameDialog;
import com.google.android.material.button.MaterialButton;


public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        MaterialButton GoBack = findViewById(R.id.go_back);
        MaterialButton SetAPI = findViewById(R.id.api_key_set);
        MaterialButton SetUsername = findViewById(R.id.username_set);


        GoBack.setOnClickListener(view -> onBackPressed());

        SetAPI.setOnClickListener(view -> SetAPIKeyDialog.showDialog(this));

        SetUsername.setOnClickListener(view -> SetUsernameDialog.showDialog(this));

        AppCompatToggleButton auction_toggle = findViewById(R.id.auction_toggle);
        auction_toggle.setChecked(ButtonStatusManager.getButtonStatus("auction_toggle", this));
        auction_toggle.setOnClickListener(view -> ButtonStatusManager.saveButtonStatus("auction_toggle", auction_toggle.isChecked(), this));

        AppCompatToggleButton fetchur_toggle = findViewById(R.id.fetchur_toggle);
        fetchur_toggle.setChecked(ButtonStatusManager.getButtonStatus("fetchur_toggle", this));
        fetchur_toggle.setOnClickListener(view -> ButtonStatusManager.saveButtonStatus("fetchur_toggle", fetchur_toggle.isChecked(), this));


        AppCompatToggleButton SBUpdates_toggle = findViewById(R.id.SBUpdates_toggle);
        SBUpdates_toggle.setChecked(ButtonStatusManager.getButtonStatus("SBUpdates_toggle", this));
        SBUpdates_toggle.setOnClickListener(view -> ButtonStatusManager.saveButtonStatus("SBUpdates_toggle", SBUpdates_toggle.isChecked(), this));

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        finish();
    }


}
