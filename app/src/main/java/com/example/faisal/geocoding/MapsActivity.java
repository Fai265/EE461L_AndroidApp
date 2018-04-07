package com.example.faisal.geocoding;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String JSON_RESP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void callVolley(String URL){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSON_RESP = response;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                    JSON_RESP = "";
                }
            });
            queue.add(stringRequest);
        } catch (Exception e){
            JSON_RESP = "";
        }
    }

    private double[] parseJSON(JSONObject resp) throws JSONException{
        JSONObject location = resp.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
        double latitude = location.getDouble("lat");
        double longitude = location.getDouble("lng");

        double[] coord = {latitude, longitude};
        return coord;
    }

    private String stateGreeting(String state){
        switch(state){
            case "AL" : return "Montgomery!";
            case "AK" : return "Juneau";
            case "AZ" : return "Phoenix";
            case "AR" : return "Little Rock";
            case "CA" : return "Sacramento";
            case "CO" : return "Denver";
            case "CT" : return "Hartford";
            case "DE" : return "Dover";
            case "FL" : return "Tallahassee";
            case "GA" : return "Atlanta";
            case "HI" : return "Honolulu";
            case "ID" : return "Boise";
            case "IL" : return "Springfield";
            case "IN" : return "Indianapolis";
            case "IA" : return "Des Moines";
            case "KS" : return "Topeka";
            case "KY" : return "Frankfort";
            case "LA" : return "Baton Rouuge";
            case "ME" : return "Augusta";
            case "MD" : return "Annapolis";
            case "MA" : return "Boston";
            case "MI" : return "Lansing";
            case "MN" : return "St. Paul";
            case "MS" : return "Jackson";
            case "MO" : return "Jefferson City";
            case "MT" : return "Helena";
            case "NE" : return "Lincoln";
            case "NV" : return "Carson City";
            case "NH" : return "Concord";
            case "NJ" : return "Trenton";
            case "NM" : return "Santa Fe";
            case "NY" : return "Albany";
            case "NC" : return "Raleigh";
            case "ND" : return "Bismarck";
            case "OH" : return "Columbus";
            case "OK" : return "Oklahoma City";
            case "OR" : return "Salem";
            case "PA" : return "Harrisburg";
            case "RI" : return "Providence";
            case "SC" : return "Columbia";
            case "SD" : return "Pierre";
            case "TN" : return "Nashville";
            case "TX" : return "Austin";
            case "UT" : return "Salt Lake City";
            case "VT" : return "Montpelier";
            case "VA" : return "Richmond";
            case "WA" : return "Olympia";
            case "WV" : return "Charleston";
            case "WI" : return "Madison";
            case "WY" : return "Cheyenne";
            
            default : return "NOT US";
        }
    }

    public void onSearch(View view){
        mMap.clear();
        final EditText ET_FIELD = findViewById(R.id.ET_FIELD);
        final String url = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        final String key = "&key=AIzaSyAiMx424_zKOb2BpHnuQFdyJA9_tVO_6Xo";
        double[] coordinates;

        String address = ET_FIELD.getText().toString().replace(' ', '+');
        if(!address.equals("")) {
            String searchURL = url + address + key;
            callVolley(searchURL);
            if(JSON_RESP != null && !address.equals("")){
                try {
                    JSONObject in = new JSONObject(JSON_RESP);
                    String state = "";
                    JSONArray stateParse = in.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                    for(int i = 0; i < stateParse.length(); i++){
                        if(stateParse.getJSONObject(i).getJSONArray("types").get(0).equals("administrative_area_level_1")){
                            state = stateParse.getJSONObject(i).getString("short_name");
                        }
                    }

                    coordinates = parseJSON(in);

                    String greeting = stateGreeting(state);

                    ET_FIELD.setText("");
                    LatLng userCoord = new LatLng(coordinates[0], coordinates[1]);
                    mMap.addMarker(new MarkerOptions().position(userCoord).title(greeting));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoord));
                } catch (JSONException je){
                    ET_FIELD.setText("Error!");
                }
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
