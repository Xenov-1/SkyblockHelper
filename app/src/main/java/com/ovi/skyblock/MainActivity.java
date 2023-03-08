package com.ovi.skyblock;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ovi.skyblock.activites.ButtonStatusTask;
import com.ovi.skyblock.activites.DataSet;
import com.ovi.skyblock.activites.MayorDataSet;
import com.ovi.skyblock.dialogs.LoadingDialog;
import com.ovi.skyblock.dialogs.MayorPerksDialog;
import com.ovi.skyblock.utilities.SkyblockEvent;
import com.ovi.skyblock.utilities.TimeConverter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import com.ovi.skyblock.dialogs.DismissDialog;

import com.ovi.skyblock.utilities.SkyblockTime;
import com.ovi.skyblock.utilities.TimeFormatChanger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    private String CurrentMayorName;
    private String CurrentMayorPerks;
    private String NextMayorPerks;
    private String NextMayorName;
    private String API;
    private String UUID;

    SkyblockEvent[] nextEvents = SkyblockEvent.SINGLE_NEXT_EVENTS;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sp = getSharedPreferences("UserInfo",MODE_PRIVATE);
        API = sp.getString("API", null);
        UUID = sp.getString("UUID", null);
        MayorDataSet.setData(this);
        DataSet.setData(this);

        ButtonStatusTask.StatusTask(this);


        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            new GetDataTask().execute();
            new AuctionStatus().execute();
            new DownloadWebsiteTask().execute();

        } else {
            Toast.makeText(this, "Network connection is disabled.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Showing old data.", Toast.LENGTH_SHORT).show();
        }

        final TextView sb_time = findViewById(R.id.sb_time);
        final TextView event_timer = findViewById(R.id.event_timer);
        final TextView event_timer1 = findViewById(R.id.event_timer1);
        final TextView event_timer2 = findViewById(R.id.event_timer2);
        final TextView event_timer3 = findViewById(R.id.event_timer3);

        final Handler handler = new Handler();
        final Runnable updateTime = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                SkyblockTime currentTime = TimeConverter.getIGTime();
                String timeString = currentTime.toString();
                sb_time.setText("Skyblock Time: " + timeString);
                SkyblockEvent dark_auction = new SkyblockEvent("DARK AUCTION", 0, new SkyblockTime(0,0,0,0), 0);
                SkyblockEvent jacob_contest = new SkyblockEvent("JACOB CONTEST", 0, new SkyblockTime(0,0,0,0), 1);

                String JacobContestTimer = "<font color='#19bf8d'>" +  TimeFormatChanger.convertTimestamp(jacob_contest.getSecondsToNextOccurrence());
                event_timer.setText(Html.fromHtml(JacobContestTimer));

                String DarkAuctionTimer = "<font color='#db6b6f'>" + TimeFormatChanger.convertTimestamp(dark_auction.getSecondsToNextOccurrence());
                event_timer1.setText(Html.fromHtml(DarkAuctionTimer));

                SkyblockEvent SPOOKY_FESTIVAL = nextEvents[5];
                String SPOOKY_FESTIVAL_TIMER = "<font color='#db9044'>" + TimeFormatChanger.convertTimestamp(SPOOKY_FESTIVAL.getSecondsToNextOccurrence());
                event_timer2.setText(Html.fromHtml(SPOOKY_FESTIVAL_TIMER));

                SkyblockEvent NEW_YEAR = nextEvents[8];
                String NEW_YEAR_TIMER = "<font color='#7e5aad'>" + TimeFormatChanger.convertTimestamp(NEW_YEAR.getSecondsToNextOccurrence());
                event_timer3.setText(Html.fromHtml(NEW_YEAR_TIMER));

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTime);



        CardView current_mayor = findViewById(R.id.current_mayor);
        CardView next_mayor = findViewById(R.id.next_mayor);



        current_mayor.setOnClickListener(view -> {
            if (CurrentMayorPerks == null) {
                Toast.makeText(this, "Getting data from Hypixel.", Toast.LENGTH_SHORT).show();
            } else{
                MayorPerksDialog.showDialog(this, CurrentMayorPerks, CurrentMayorName, "Current");
            }
        });

        next_mayor.setOnClickListener(view -> {
            if (NextMayorPerks == null) {
                if (NextMayorName.equals("Unknown")) {
                    SkyblockEvent ElectionStarts = nextEvents[3];
                    String ElectionStartTimer = TimeFormatChanger.convertTimestamp(ElectionStarts.getSecondsToNextOccurrence());
                    Toast.makeText(this, "Elections starts in :  " + ElectionStartTimer, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Getting data from Hypixel.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                MayorPerksDialog.showDialog(this, NextMayorPerks, NextMayorName, "Next");
            }
        });

        FloatingActionButton SettingsActivity = findViewById(R.id.settings);

        SettingsActivity.setOnClickListener(view -> {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            finish();
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetDataTask extends AsyncTask<Void, Void, String> {
        String CurrentMayor;
        String currentMayorPerks = "";
        String NextMayor;
        String nextMayorPerks = "";



        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL mayor_url = new URL("https://api.hypixel.net/resources/skyblock/election");

                HttpURLConnection con = (HttpURLConnection) mayor_url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();

                JsonElement jsonTree = JsonParser.parseString(content.toString());
                JsonObject jsonObject = jsonTree.getAsJsonObject();

                CurrentMayor = jsonObject.get("mayor").getAsJsonObject().get("name").getAsString();
                JsonArray perks_data = jsonObject.get("mayor").getAsJsonObject().get("perks").getAsJsonArray();

                for (JsonElement perk : perks_data) {
                    String name = perk.getAsJsonObject().get("name").getAsString();
                    String description = perk.getAsJsonObject().get("description").getAsString();
                    //noinspection StringConcatenationInLoop
                    currentMayorPerks += "<font color='red'>Name: </font>" + "<font color='#19bf8d'>" + name + "</font>" + "<br>" + "<font color='#199ebf'>Description: </font>" + "<font color='yellow'>" + description + "</font>" + "<br>" + "<br>";
                    currentMayorPerks = currentMayorPerks.replaceAll("§[\\da-fk-or]", "");

                }
                try{
                    JsonArray candidates = jsonObject.get("current").getAsJsonObject().get("candidates").getAsJsonArray();
                    int highest_vote = 0;

                    for (JsonElement candidate : candidates) {
                        int total_votes = candidate.getAsJsonObject().get("votes").getAsInt();
                        if (total_votes > highest_vote) {
                            highest_vote = total_votes;
                            NextMayor = candidate.getAsJsonObject().get("name").getAsString();
                        }
                    }
                    for (JsonElement candidate : candidates) {
                        if(candidate.getAsJsonObject().get("name").getAsString().equals(NextMayor)){
                            JsonArray next_perks_data = candidate.getAsJsonObject().get("perks").getAsJsonArray();
                            for (JsonElement perk : next_perks_data) {
                                String name = perk.getAsJsonObject().get("name").getAsString();
                                String description = perk.getAsJsonObject().get("description").getAsString();
                                //noinspection StringConcatenationInLoop
                                nextMayorPerks += "<font color='red'>Name: </font>" + "<font color='#19bf8d'>" + name + "</font>" + "<br>" + "<font color='#199ebf'>Description: </font>" + "<font color='yellow'>" + description + "</font>" + "<br>" + "<br>";
                                nextMayorPerks = nextMayorPerks.replaceAll("§[\\da-fk-or]", "");

                            }
                        }
                    }

                    sp = getSharedPreferences("MayorData",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("CurrentMayorName", CurrentMayor);
                    editor.putString("CurrentMayorPerks", currentMayorPerks);
                    editor.putString("NextMayorName", NextMayor);
                    editor.putString("NextMayorPerks", nextMayorPerks);
                    editor.apply();

                } catch (Exception e){
                    NextMayor = "Unknown";
                    e.printStackTrace();
                }
                return CurrentMayor;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String mayorName) {
            super.onPostExecute(mayorName);

            TextView current_mayor_name = findViewById(R.id.current_mayor_name);
            current_mayor_name.setText(CurrentMayor);
            ImageView current_mayor_skin = findViewById(R.id.current_mayor_skin);

            TextView next_mayor_name = findViewById(R.id.next_mayor_name);
            next_mayor_name.setText(NextMayor);
            ImageView next_mayor_skin = findViewById(R.id.next_mayor_skin);

            @SuppressLint("DiscouragedApi") int current_mayor = getResources().getIdentifier(CurrentMayor.toLowerCase(), "drawable", getPackageName());
            current_mayor_skin.setImageResource(current_mayor);

            @SuppressLint("DiscouragedApi") int next_mayor = getResources().getIdentifier(NextMayor.toLowerCase(), "drawable", getPackageName());
            next_mayor_skin.setImageResource(next_mayor);

            CurrentMayorPerks = currentMayorPerks;
            NextMayorPerks = nextMayorPerks;
            CurrentMayorName = CurrentMayor;
            NextMayorName = NextMayor;
            DismissDialog.dismissDialog(LoadingDialog.dialog);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadWebsiteTask extends AsyncTask<String, Void, String> {
        String news;
        String Fetchur;

        @Override
        protected String doInBackground(String... urls) {
            try {
                // Download the website content
                Document dataWIKI = Jsoup.connect("https://hypixel-skyblock.fandom.com/wiki/Hypixel_SkyBlock_Wiki").get();
                Document newsWIKI = Jsoup.connect("https://wiki.hypixel.net/Main_Page").get();



                // Get the elements with the class "my-class"
                Elements dataElements = dataWIKI.getElementsByClass("widgetbox-content");
                Element dataElement = dataElements.get(1);


                Element newsElement = newsWIKI.getElementById("news");

                Fetchur = dataElement.toString();
                assert newsElement != null;
                news = newsElement.toString();

                String cleanedFetchur = Fetchur.replaceAll("<img[^>]*>", "");
                String cleanedNews = news.replaceAll("<img[^>]*>", "");

                sp = getSharedPreferences("Data",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("FetchurToday",cleanedFetchur );
                editor.putString("SkyblockUpdates",cleanedNews );
                editor.apply();


                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {


            TextView news_box = findViewById(R.id.Ndata);
            TextView fetchur_box = findViewById(R.id.FetchurToday);


            String cleanedNews = news.replaceAll("<img[^>]*>", "");
            news_box.setText(Html.fromHtml(cleanedNews));

            String cleanedFetchur = Fetchur.replaceAll("<img[^>]*>", "");
            fetchur_box.setText(Html.fromHtml(cleanedFetchur));



        }
    }


    @SuppressLint("StaticFieldLeak")
    private class AuctionStatus extends AsyncTask<String, Void, String> {
        String Auctions = "";

        @Override
        protected String doInBackground(String... urls) {
            try {
                String apiUrl = "https://api.hypixel.net/skyblock/auction?key=" + API + "&player=" + UUID;

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String response = stringBuilder.toString();

                JSONObject json = new JSONObject(response);
                JSONArray auctions = json.getJSONArray("auctions");

                for (int i = 0; i < auctions.length(); i++) {
                    JSONObject auction = auctions.getJSONObject(i);
                    boolean claimed = auction.getBoolean("claimed");

                    if (!claimed) {
                        boolean bin = auction.getBoolean("bin");
                        if (bin) {

                            String itemName = auction.getString("item_name");
                            String tier = auction.getString("tier");

                            if (tier.equals("COMMON")) {
                                itemName = "<font color='#FFFFFF'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("UNCOMMON")) {
                                itemName = "<font color='#4ee44e'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("RARE")) {
                                itemName = "<font color='#5555ff'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("EPIC")) {
                                itemName = "<font color='#aa00aa'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("LEGENDARY")) {
                                itemName = "<font color='#ffaa00'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("MYTHIC")) {
                                itemName = "<font color='#ff55ff'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("DIVINE")) {
                                itemName = "<font color='#55ffff'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("SPECIAL")) {
                                itemName = "<font color='#ff5353'>" + itemName + "</font><br>";
                            }
                            if (tier.equals("VERY SPECIAL")) {
                                itemName = "<font color='#ff5555'>" + itemName + "</font><br>";
                            }

                            int startingBid = auction.getInt("starting_bid");
                            boolean hasBids = auction.getJSONArray("bids").length() > 0;
                            String formattedStartingBid = formatCoins(startingBid);
                            String status = hasBids ? " <font color='green'>(Sold)</font>" : " <font color='red'>(Running)</font>";
                            int highestBidAmount = hasBids ? auction.getJSONArray("bids").getJSONObject(0).getInt("amount") : 0;
                            String formattedHighestBidAmount = formatCoins(highestBidAmount);
                            String auctionString = String.format("- %s%s%s\n", itemName, status, highestBidAmount > 0 ? "<font color='#0cf6bb'> for </font><font color='#1f6eed'>" + formattedHighestBidAmount + "</font>" : " <font color='#34a4eb'> " + formattedStartingBid + "</font>");
                            Auctions += auctionString + "<br>" + "<br>";


                            sp = getSharedPreferences("Data",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("ActiveAuction",Auctions );
                            editor.apply();

                        }
                    }
                }

                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        @SuppressLint("DefaultLocale")
        public String formatCoins(int coins) {
            if (coins >= 1000000000) {
                return String.format("%.1fB", coins / 1000000000.0);
            } else if (coins >= 1000000) {
                return String.format("%.1fM", coins / 1000000.0);
            } else if (coins >= 1000) {
                return String.format("%.1fK", coins / 1000.0);
            } else {
                return String.valueOf(coins);
            }
        }

        @Override
        protected void onPostExecute(String result) {


            TextView auctions_box = findViewById(R.id.ActiveAuctions);

            auctions_box.setText(Html.fromHtml(Auctions));



        }
    }


}