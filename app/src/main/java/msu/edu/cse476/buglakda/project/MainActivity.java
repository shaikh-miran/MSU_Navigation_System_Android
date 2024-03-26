package msu.edu.cse476.buglakda.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

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
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapView);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Force the screen to stay on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // TODO ADD SOMETHING HERE
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
        mMap = googleMap;

        //LatLng sydney = new LatLng(42.731138, -84.487508);
        LatLng currLocation = new LatLng(latitude, longitude);
        LatLng spartanStatue = new LatLng(42.731138, -84.487508);
        mMap.addMarker(new MarkerOptions().position(spartanStatue).title("Spartan Statue"));
        // Move the camera to the location and set the zoom level.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spartanStatue, 15));
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
}