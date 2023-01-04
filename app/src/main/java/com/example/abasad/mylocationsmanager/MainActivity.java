package com.example.abasad.mylocationsmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // # private members
    private MapView mapView;
    private GoogleMap gmap;
    private PlaceList list;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient googleApiClient;
    // Used to store the current location from LocationManager
    private LatLng loc;
    private FusedLocationProviderClient fusedLocationClient;


    // # life cycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);




        // Checks if ACCESS_FINE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        // Initialize the Google API Client
        googleApiClient = new GoogleApiClient.Builder
                (this, this, this)
                .addApi(LocationServices.API).build();
        // Initialize the fusedLocationClient to get current loc
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        mapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        onRead();


        mapView.onResume();
        if(gmap != null)
        {
            gmap.clear();
            AddMarkersToMap();
        }



    }

    @Override
    protected void onStart() {
        if (googleApiClient != null) {
            googleApiClient.connect();}
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {

        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // ## Map callback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        gmap.setIndoorEnabled(true);
        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        AddMarkersToMap();


    }


    // ## Location service callbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        GetLastLocation();
    }

    private void GetLastLocation()
    {
        // get the current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location lastLocation) {
                        // Got last known location. In some rare situations this can be null.
                        if (lastLocation != null) {
                            // Logic to handle location object
                            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
                            loc = new LatLng(lat, lon);

                            // Add a BLUE marker to current location and zoom
                            gmap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Your current location"));
                            gmap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                            // animate camera allows zoom
                            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));

                            // override markers
                            AddMarkersToMap();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }



    // ## Buttons callback
    public  void AddActitvity (View view)
    {
        GetLastLocation();
        Intent intent = new Intent(this, AddPlaceActivity.class);
        intent.putExtra("lat", loc.latitude);
        intent.putExtra("long", loc.longitude);
        startActivity(intent);
    }

    public void AboutClicked (View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    // # Utilities private methods
    private  void AddMarkersToMap()
    {

        for (Place p: list.list  ) {
            AddPlaceMarker(p);

        }
    }

    private void AddPlaceMarker(Place p)
    {

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng newLoc = new LatLng(p.latitude, p.longitude);
        markerOptions.position(newLoc);
        markerOptions.title(p.comment);
        if(p.haveBeen)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        else
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        gmap.addMarker(markerOptions);


    }

    // To read from XML File
    private void onRead() {
        // read the file from the data/data/packagename
        if (fileExists(this, getString(R.string.File_Name))) {
        try {

                // reading from data/data/packagename
                FileInputStream fin = openFileInput(getString(R.string.File_Name));
                InputStreamReader isr = new InputStreamReader(fin);
                char[] inputBuffer = new char[100];
                String str = "";
                int charRead;
                while ((charRead = isr.read(inputBuffer)) > 0) {
                    String readString = String.copyValueOf(inputBuffer, 0, charRead);
                    str += readString;
                }
                isr.close();

                XStream xstream = new XStream();

                xstream.alias("Place", Place.class);
                xstream.alias("Places", PlaceList.class);
                xstream.addImplicitCollection(PlaceList.class, "list");

                list = (PlaceList) xstream.fromXML(str);

                if (list.list == null)
                    list.list = new ArrayList<>();

            }
        catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        }

        else
            list = new PlaceList();


    }

    private boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

}
