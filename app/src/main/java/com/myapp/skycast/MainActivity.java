package com.myapp.skycast;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText cityInput;
    private Button getWeatherButton;
    private TextView weatherResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cityInput = findViewById(R.id.cityInput);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        weatherResult = findViewById(R.id.weatherResult);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();

                if (!city.isEmpty()) {
                    getWeatherData(city);
                }else {
                    weatherResult.setText("Please Enter a City Name.");
                }

            }
        });
    }

    private void getWeatherData(String city) {
        // to define the api key
        String apiKey = "8aeff85d0ebb4086797116df721a7b78"; // openWeather api key
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this); //request queue that handles api calls

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseWeatherData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        weatherResult.setText("City Not Found. Try again.");// if api request fails
                    }
                });
            queue.add(request); // add the request to the queue (sends API request)
    }

    private void parseWeatherData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response); // convert response string to a json string

            // extracts the temperature object from the "main" JSON object
            JSONObject main = jsonObject.getJSONObject("main");
            double temperature = main.getDouble("temp");

            // extracts the weather object from the "weather" array
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String mainCategory = weather.getString("main");
            String description = weather.getString("description");

            // Display the extracted weather information in the TextView
            weatherResult.setText("Temperature: " + temperature + "°C\n" + "Description: " + description);

            //sets the icon depends on the main and description
            setWeatherIcon( mainCategory,description );
        } catch (JSONException e) {
            e.printStackTrace();
            weatherResult.setText("Error Getting Weather Data");//prints the error if JSON Parsing Fails
        }


    }

    private void setWeatherIcon(String main, String description){
        ImageView showicon = findViewById(R.id.weathericon);
        description = description.toLowerCase();

        switch (main.toLowerCase()){
            case "rain" :
                showicon.setImageResource(R.drawable.icon_rain);
                break;
            case "thunderstorm" :
                showicon.setImageResource(R.drawable.icon_thunderstorm);
                break;
            case "drizzle" :
                showicon.setImageResource(R.drawable.icon_showerrain);
                break;
            case "snow" :
                showicon.setImageResource(R.drawable.icon_snow);
                break;
            case "atmosphere" :
                showicon.setImageResource(R.drawable.icon_mist);
                break;
            case "clear" :
                showicon.setImageResource(R.drawable.icon_clearsky);
                break;
            case "clouds" :
                if (description.contains("few")){
                    showicon.setImageResource(R.drawable.icon_fewclouds);
                }
                else if (description.contains("scattered")){
                    showicon.setImageResource(R.drawable.icon_scatteredclouds);
                }
                else if (description.contains("broken")){
                    showicon.setImageResource(R.drawable.icon_brokenclouds);
                }
                else {
                    showicon.setImageResource(R.drawable.icon_brokenclouds);
                }
                break;
            default:
                showicon.setImageResource(R.drawable.icon_default);
                break;
        }
    }

}