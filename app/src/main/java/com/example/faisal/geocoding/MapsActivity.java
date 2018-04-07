package com.example.faisal.geocoding;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String url = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private String key = "&key=AIzaSyAiMx424_zKOb2BpHnuQFdyJA9_tVO_6Xo";
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
                }
            });
            queue.add(stringRequest);
        } catch (Exception e){

        }
    }

    private double[] parseJSON(JSONObject resp) throws JSONException{
        double latitude = resp.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        double longitude = resp.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

        double[] coord = {latitude, longitude};
        return coord;
    }

    public void onSearch(View view){
        final EditText ET_FIELD = findViewById(R.id.ET_FIELD);
        double[] coordinates;

        String address = ET_FIELD.getText().toString().replace(' ', '+');
        if(!address.equals("")) {
            String searchURL = url + address + key;
            callVolley(searchURL);
            if(JSON_RESP != null && !address.equals("")){
                try {
                    JSONObject in = new JSONObject(JSON_RESP);
                    coordinates = parseJSON(in);

                    ET_FIELD.setText("");
                    LatLng userCoord = new LatLng(coordinates[0], coordinates[1]);
                    mMap.addMarker(new MarkerOptions().position(userCoord).title("Here it is!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoord));
                } catch (JSONException je){
                    ET_FIELD.setText(je.getMessage());
                }
            }
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        try {
            mMap.setMyLocationEnabled(true);
        }
        catch(SecurityException e){

        }



    }
}
