package com.ovi.skyblock.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ovi.skyblock.R;

public class SetAPIKeyDialog {

    public static AlertDialog dialog;
    public static AlertDialog.Builder builder;
    public static SharedPreferences sp;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static void showDialog(Context context) {
        SetAPIKeyDialog.context = context;
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.api_dialog_layout, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);


        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialog.show();


        EditText api_key = (EditText) dialogView.findViewById(R.id.api_key_input);
        Button submit_button = (Button) dialogView.findViewById(R.id.set_button);
        Button dismiss_button = (Button) dialogView.findViewById(R.id.dismiss_button);



        api_key.requestFocus();
        submit_button.setOnClickListener(v -> {
            String api = api_key.getText().toString();
            if(api.equals("")) {
                Toast.makeText(context.getApplicationContext(), "Please enter API key.", Toast.LENGTH_SHORT).show();


            } else {
                sp = context.getSharedPreferences("UserInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("API", api);
                editor.apply();


                Toast.makeText(context.getApplicationContext(), "API key added successfully.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dismiss_button.setOnClickListener(v -> dialog.dismiss());

        api_key.setFilters(new InputFilter[] {new InputFilter.LengthFilter(36)});
        api_key.addTextChangedListener(new TextWatcher() {
            private boolean mFormatting;
            private boolean clearFlag;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mFormatting) return;
                mFormatting = true;
                String pattern = "^[a-zA-Z\\d-]{36}$";
                String input = s.toString();
                int currentLength = s.length();

                if (clearFlag) {
                    clearFlag = false;
                    s.clear();
                }

                if (input.matches(pattern)) {
                    mFormatting = false;
                    return;
                }

                StringBuilder formatted = new StringBuilder();
                int charCount = 0;
                for (int i = 0; i < currentLength; i++) {
                    char c = input.charAt(i);
                    if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '-') {
                        formatted.append(c);
                        charCount++;
                    }
                    if (charCount == 8 || charCount == 13 || charCount == 18 || charCount == 23) {
                        formatted.append("-");
                    }
                }
                clearFlag = formatted.length() > s.length();
                s.replace(0, s.length(), formatted.toString());
                mFormatting = false;
            }
        });
    }

}
