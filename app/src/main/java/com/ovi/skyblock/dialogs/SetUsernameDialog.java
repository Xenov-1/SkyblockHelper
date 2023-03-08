package com.ovi.skyblock.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.ovi.skyblock.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetUsernameDialog extends DialogFragment {

    public static AlertDialog dialog;
    public static AlertDialog.Builder builder;
    public static SharedPreferences sp;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    static String username = "";


    public static void showDialog(Context context) {
        SetUsernameDialog.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.username_dialog, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);


        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        dialog.show();


        EditText username_input = (EditText) dialogView.findViewById(R.id.username_input);
        Button submit_button = (Button) dialogView.findViewById(R.id.set_button);
        Button dismiss_button = (Button) dialogView.findViewById(R.id.dismiss_button);


        username_input.requestFocus();

        submit_button.setOnClickListener(v -> {
            username = username_input.getText().toString();
            if (username.equals("")) {
                Toast.makeText(context.getApplicationContext(), "Please enter your Minecraft username.", Toast.LENGTH_SHORT).show();


            } else {
                new UUIDConversionTask().execute(username);
                dialog.dismiss();

            }
        });


        dismiss_button.setOnClickListener(v -> dialog.dismiss());
    }


    public static class UUIDConversionTask extends AsyncTask<String, Void, String> {
        // Define any member variables or helper methods here
        String UUID = "";
        @Override
        protected String doInBackground(String... params) {
            // This method will be executed in a background thread


            // Use the Mojang API to convert the username to a UUID
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                JSONObject json = new JSONObject(line);
                UUID = json.getString("id");



            } catch (Exception e) {
                // Handle any errors here
            }

            return UUID;
        }


        @Override
        protected void onPostExecute(String UUID) {
            // This method will be executed on the main UI thread
            // Do something with the UUID here
            if (UUID != null) {
                sp = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("UUID", UUID);
                editor.apply();

                Toast.makeText(context.getApplicationContext(), "Username added successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context.getApplicationContext(), "Unable to add username.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
