package com.example.testproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_reset = findViewById(R.id.btn_reset);
        Button btn_lastMonth = findViewById(R.id.btn_lastMonth);
        Button btn_nextMonth = findViewById(R.id.btn_nextMonth);

        // When clicking the button, it gets the releases for the last month and add the views
        btn_lastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String stringURL = "https://api.rawg.io/api/games?dates=" + getLastMonth() + "," + getThisMonth() + "&page_size=50";
                System.out.println("URL: " + stringURL);
                getResponse(stringURL);
            }
        });

        // When click the button, it gets the releases for the next month and add the views
        btn_nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String stringURL = "https://api.rawg.io/api/games?dates=" + getThisMonth() + "," + getNextMonth() + "&page_size=50";
                System.out.println("URL: " + stringURL);
                getResponse(stringURL);
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeViews((LinearLayout) findViewById(R.id.layout_main));
            }
        });
    }

    void getResponse(String url){

        // First remove all views if there are any
        removeViews((LinearLayout) findViewById(R.id.layout_main));

        // Volley Requestqueue with context as param
        // Fetches a request queue object
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Construct the actual request
        // Json Array or Json Object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Rest Response", response.toString());
                        try {
                            /*
                            // This block is for reading purpose
                            // The jsonArray contains the JSONArray called "results"
                            jsonArray = response.getJSONArray("results");
                            JSONObject object = jsonArray.getJSONObject(1);
                            JSONArray testArray = object.getJSONArray("platforms");
                            JSONObject testObj = testArray.getJSONObject(0);
                            System.out.println("Object: " + object);
                            System.out.println("TestArray: " + testArray);
                            System.out.println("TestObj: " + testObj.getJSONObject("platform").get("name"));
                            // System.out.println("TestArray2: " + testArray2);
                            */

                            JSONArray results = response.getJSONArray("results");

                            /**
                             * Use this as an example -> Returns "PC"
                             * From response object -> results -> withing results (index 1) -> platforms -> array(index(0)) -> platform (obj) -> name
                             */
                            System.out.println("Gamename: " + results.getJSONObject(1).get("name"));

                            System.out.println("Platform : " + results.getJSONObject(1).getJSONArray("platforms").getJSONObject(0).getJSONObject("platform").get("name"));

                            ArrayList<String> names = new ArrayList<String>();
                            ArrayList<ArrayList<String>> platforms = new ArrayList<>();
                            ArrayList<String> releaseDates = new ArrayList<String>();

                            for(int i = 0; i < results.length(); i++){
                                // Add names
                                names.add(results.getJSONObject(i).get("name").toString());
                                releaseDates.add(results.getJSONObject(i).get("released").toString());

                                // Add platforms
                                JSONArray platformArr = results.getJSONObject(i).getJSONArray("platforms");
                                ArrayList<String> platform = new ArrayList<String>();
                                for (int j = 0; j < platformArr.length(); j++){
                                    // Add platform to platformsArr
                                    platform.add(platformArr.getJSONObject(j).getJSONObject("platform").get("name").toString());
                                }
                                // Add the platformArr to platforms
                                platforms.add(platform);
                            }

                            System.out.println("Names: " + names);
                            System.out.println("Platforms: " + platforms);
                            System.out.println("Released: " + releaseDates);

                            for (int k = 0; k < names.size(); k++){
                                addView(names.get(k), platforms.get(k), releaseDates.get(k));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest Error", error.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getThisMonth(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String thisMonth = LocalDate.now().format(formatter);

        return thisMonth;
    }

    private String getLastMonth(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONDAY, -1);
        String lastMonth = format.format(cal.getTime());
        return lastMonth;
    }

    private String getNextMonth(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONDAY, 1);
        String lastMonth = format.format(cal.getTime());
        return lastMonth;
    }


    private void addView(String gameName, ArrayList<String> gameRelease, String gamePlatform){
        //create a TextView with Layout parameters according to your needs
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //if your parent Layout is relativeLayout, just change the word LinearLayout with RelativeLayout
        TextView tv = new TextView(this);
        tv.setLayoutParams(lparams);
        tv.setText(gameName + "\n" + gamePlatform + "\n" + gameRelease + "\n");
        System.out.println("TESING");
        //get the parent layout for your new TextView and add the new TextView to it
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_main);
        linearLayout.addView(tv);
    }

    private void removeViews(LinearLayout layout){
        layout.removeAllViews();
    }

}