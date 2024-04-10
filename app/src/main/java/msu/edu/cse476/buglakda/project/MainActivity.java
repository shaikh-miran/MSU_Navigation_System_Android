package msu.edu.cse476.buglakda.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final static double ENGLAT = 42.724303;

    private final static double ENGLONG = -84.480507;

    private final static double STEMLAT = 42.7265543;
    private final static double STEMLONG = -84.4876656;

    private final static double BROADLONG = 42.7265538;
    private final static double BROADLAT = -84.4951027;
    /**
     * Constants for setting of where user is going
     */
    private final static String TO = "to";
    private final static String TOLAT = "tolat";
    private final static String TOLONG = "tolong";

    /**
     * Location maanager to know current location
     */
    private LocationManager locationManager = null;

    /**
     * Current latitude, longitude, and if valid
     */
    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;

    /**
     * Location user is going to
     */
    private double toLatitude = 0;
    private double toLongitude = 0;
    private String to = "";

    /**
     * Google map on the screen
     */
    private GoogleMap mMap;

    /**
     * Listener for current location
     */
    private ActiveListener activeListener = new ActiveListener();

    /**
     * Support preferences to keep track of to location on app exit
     */
    private SharedPreferences settings = null;

    private Marker lastMarker = null;
    private Polyline lastPolyline = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences("my.app.packagename_preferences", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission((this),
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified with the map is ready to be used.
        setupLocationManagerAndMapFragment();

        // Force the screen to stay on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setupLocationManagerAndMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void setUI() {
        LatLng currLocation = new LatLng(latitude, longitude);

        Log.i("LATITUDE", String.valueOf(latitude));
        Log.i("LONGITUDE", String.valueOf(longitude));

        if (mMap != null && valid) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    setupLocationManagerAndMapFragment();
                }
            } else {
                // Permission Denied
                Toast.makeText(this, "Permission denied to access your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onStartHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //LatLng sydney = new LatLng(42.731138, -84.487508);
        LatLng currLocation = new LatLng(latitude, longitude);

        // TODO Temporary location, update coords to toLatitude and toLongitude once implemented
        LatLng spartanStatue = new LatLng(42.731138, -84.487508);

        mMap = googleMap;

        //mMap.addMarker(new MarkerOptions().position(spartanStatue).title("Spartan Statue"));

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 15));


        // Request walking directions
        //requestWalkingDirections(currLocation, spartanStatue);
    }

    /**
     * Constructs the API request URL to get directions to destination from current location
     * @param origin Current location of user in latitude and longitude
     * @param destination Destination location in latitude and longitude
     */
    private void requestWalkingDirections(LatLng origin, LatLng destination) {
        // Construct the directions API
        String apiKey = "AIzaSyDb6ZRmE8TDUBLR9-FQjP_UAHnOUf-TH8g";
        String requestUrl = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&mode=walking&key=%s",
                origin.latitude, origin.longitude,
                destination.latitude, destination.longitude,
                apiKey);

        Log.i("DESTLATITUDE", String.valueOf(destination.latitude));
        Log.i("DESTLONGITUDE", String.valueOf(destination.longitude));

        // Execute the directions API in an AsyncTask
        new FetchDirectionsTask().execute(requestUrl);
    }

    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray routes = jsonResponse.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject poly = route.getJSONObject("overview_polyline");
                    String polyline = poly.getString("points");
                    drawPolyline(polyline);
                } else {
                    // Log the case where no routes are found
                    Log.e("DirectionsError", "No routes found in the response.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Log the JSON parsing error
                Log.e("DirectionsError", "Error parsing JSON response.", e);
            }
        }


    }

    private void drawPolyline(String polylinePoints) {
        List<LatLng> decodedPath = PolyUtil.decode(polylinePoints);
        if (mMap != null) {
            // Remove the last polyline if it exists
            if (lastPolyline != null) {
                lastPolyline.remove();
            }
            lastPolyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath)); // Save the new polyline
        }
    }

    /**
     * Save a location to preferences
     * @param locName Location name
     * @param lat Latitude of location
     * @param lon Longitude of location
     */
    private void saveLocation(String locName, double lat, double lon) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TO, locName);
        editor.putString(TOLAT, String.valueOf(lat));
        editor.putString(TOLONG, String.valueOf(lon));
        editor.apply();
    }

    /**
     * Store current location
     * @param location Current location
     */
    private void onLocation(Location location) {
        if (location == null) {
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        valid = true;
        ImageView imageView = findViewById(R.id.buildingImageView);
        double diff = 0.05;
        if (longitude>ENGLONG-diff &&  longitude<ENGLONG+diff && latitude<ENGLAT+diff && latitude>ENGLAT-diff) {

            imageView.setImageResource(R.drawable.eb_nw_entrance);
        }
        if (longitude>STEMLONG-diff &&  longitude<STEMLONG+diff && latitude<STEMLAT+diff && latitude>STEMLAT-diff) {

            imageView.setImageResource(R.drawable.stem_build);
        }
        if (longitude>BROADLONG-diff &&  longitude<BROADLONG+diff && latitude<BROADLAT+diff && latitude>BROADLAT-diff) {

            imageView.setImageResource(R.drawable.broad_build);
        }
        setUI();
    }

    /**
     * Register the active listener for user's location
     */
    private void registerListeners() {
        unregisterListeners();

        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);

        // Find the best available provider
        String bestAvailable = locationManager.getBestProvider(criteria, true);

        if (bestAvailable != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
            Log.i("BESTAVAILABLE", bestAvailable);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }
    }

    /**
     * Unregister the active listeners of user's location
     */
    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }

    /**
     * Called when this application becomes foreground again
     */
    @Override
    protected void onResume() {
        super.onResume();

        setupLocationManagerAndMapFragment();

        registerListeners();
    }

    /**
     * Called when this application is no longer the foreground application
     */
    @Override
    protected void onPause() {
        unregisterListeners();

        super.onPause();
    }

    private class ActiveListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            onLocation(location);
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            registerListeners();
        }
    };
    public void setToLocation(View view) {
        TextView EditLocation = findViewById(R.id.classroomInput);
        String address = EditLocation.getText().toString().trim();
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double toLatitude = location.getLatitude();
                double toLongitude = location.getLongitude();

                // Remove the last marker and polyline if they exist
                if (lastMarker != null) {
                    lastMarker.remove();
                    lastMarker = null; // Clear the reference to the marker
                }
                if (lastPolyline != null) {
                    lastPolyline.remove();
                    lastPolyline = null; // Clear the reference to the polyline
                }

                // Set the tolat and tolong
                saveLocation(address, toLatitude, toLongitude);

                // Set pin on the map
                LatLng destination = new LatLng(toLatitude, toLongitude);
                lastMarker = mMap.addMarker(new MarkerOptions().position(destination).title(address));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
                LatLng currLocation = new LatLng(latitude, longitude);
                requestWalkingDirections(currLocation, destination);
            } else {
                Toast.makeText(this, "Unable to find location. Please enter a valid address.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error finding location. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}