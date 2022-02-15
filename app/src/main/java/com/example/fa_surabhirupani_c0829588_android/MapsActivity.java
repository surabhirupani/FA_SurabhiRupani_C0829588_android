package com.example.fa_surabhirupani_c0829588_android;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.fa_surabhirupani_c0829588_android.Database.AppDatabase;
import com.example.fa_surabhirupani_c0829588_android.Database.DAO;
import com.example.fa_surabhirupani_c0829588_android.Model.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MapsActivity";

    // Fused location provider client
    private FusedLocationProviderClient mClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    Double lat=0.0, longi=0.0;
    Place placeFav;
    private DAO dao;
    private Button hybridMapBtn, terrainMapBtn, satelliteMapBtn;
    FloatingActionButton fab;
    String distance;
    String duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dao = AppDatabase.getDb(this).getDAO();
        hybridMapBtn = findViewById(R.id.idBtnHybridMap);
        terrainMapBtn = findViewById(R.id.idBtnTerrainMap);
        satelliteMapBtn = findViewById(R.id.idBtnSatelliteMap);
        fab = findViewById(R.id.fabInfo);

       if(getIntent().getSerializableExtra("place") != null) {
           placeFav = (Place) getIntent().getSerializableExtra("place");
           lat = placeFav.getPlaceLat();
           longi = placeFav.getPlaceLong();
           fab.setVisibility(View.VISIBLE);
       }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mClient = LocationServices.getFusedLocationProviderClient(this);

        hybridMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change
                // the type of map to hybrid.
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        // adding on click listener for our terrain map button.
        terrainMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change
                // the type of terrain map.
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        // adding on click listener for our satellite map button.
        satelliteMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is to change the
                // type of satellite map.
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInfoDialog();
            }
        });
    }

    private void openInfoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dlog_distance_info);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tvInfo = dialog.findViewById(R.id.tvInfo);
        tvInfo.setText("Total Distance is "+distance+"\nTotal Travel Duration is "+duration);

        dialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);
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

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();
    }

    private void startUpdateLocation() {
//
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
//                mMap.clear();
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions options = new MarkerOptions().position(userLocation)
                            .title("You are here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .snippet("Your Location");
                    mMap.addMarker(options);

                    if(lat!=0.0) {

                        LatLng placeLocation = new LatLng(lat, longi);
                        MarkerOptions options1 = new MarkerOptions().position(placeLocation)
                                .title(placeFav.getPlaceName())
                                .draggable(true);
                        mMap.addMarker(options1);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 8));
                        Location locationB = new Location("point B");

                        locationB.setLatitude(lat);
                        locationB.setLongitude(longi);

                        float distance = location.distanceTo(locationB);
                        Log.d("DISTANCE", ""+distance);
                        getDestinationInfo(placeLocation, userLocation);

                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 8));
                    }
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                }
            }
        };



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setMessage("The permission is mandatory")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                            }
                        }).create().show();
            } else
                startUpdateLocation();
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        putMarerOnMap(latLng);
    }

    private void putMarerOnMap(LatLng latLng) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Add Place");
        builder.setMessage("Do you want to add this place as your favourite?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String address = "";

                try {

                    List<Address> listAdddresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

                    if (listAdddresses != null && listAdddresses.size() > 0) {
                        if (listAdddresses.get(0).getThoroughfare() != null) {
                            if (listAdddresses.get(0).getSubThoroughfare() != null) {
                                address += listAdddresses.get(0).getSubThoroughfare() + " ";
                            }
                            address += listAdddresses.get(0).getThoroughfare();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (address.equals("")) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm yyyy-MM-dd");
                    address += sdf.format(new Date());
                }

                mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                Place place1;
                String message;
                if(lat!=0) {
                    place1 = placeFav;
                    message = "Place Updated!";
                } else {
                    place1 = new Place();
                    message = "Place Added!";
                }
                place1.setVisited(false);
                place1.setPlaceName(address);
                place1.setPlaceLong(latLng.longitude);
                place1.setPlaceLat(latLng.latitude);
                dao.insertPlace(place1);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d("LOC", "DragStart");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng favLocation = new LatLng(marker.getPosition().latitude, marker.getPosition().latitude);
        putMarerOnMap(favLocation);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d("LOC", "DragStart");
    }

    private void getDestinationInfo(LatLng latLngDestination, LatLng latLngSrc) {
        String serverKey = "AIzaSyDjcthx1X4L9llPKD--mV6TADLnYsATGA4"; // Api Key For Google Direction API \\
        final LatLng origin = latLngSrc;
        final LatLng destination = latLngDestination;
        //-------------Using AK Exorcist Google Direction Library---------------\\
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info distanceInfo = leg.getDistance();
                            Info durationInfo = leg.getDuration();
                            distance = distanceInfo.getText();
                            duration = durationInfo.getText();

                            //------------Displaying Distance and Time-----------------\\
//                            showingDistanceTime(distance, duration); // Showing distance and time to the user in the UI \\
                            String message = "Total Distance is " + distance + " and Estimated Time is " + duration;
                            Log.d("MAPLOCATIO", message);
//                            StaticMethods.customSnackBar(consumerHomeActivity.parentLayout, message,
//                                    getResources().getColor(R.color.colorPrimary),
//                                    getResources().getColor(R.color.colorWhite), 3000);

                            //--------------Drawing Path-----------------\\
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(MapsActivity.this,
                                    directionPositionList, 5, getResources().getColor(R.color.purple_500));
                            mMap.addPolyline(polylineOptions);
                            //--------------------------------------------\\

                            //-----------Zooming the map according to marker bounds-------------\\
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(origin);
                            builder.include(destination);
                            LatLngBounds bounds = builder.build();

                            int width = getResources().getDisplayMetrics().widthPixels;
                            int height = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                            mMap.animateCamera(cu);
                            //------------------------------------------------------------------\\

                        } else if (status.equals(RequestResult.NOT_FOUND)) {
                            Toast.makeText(getApplicationContext(), "No routes exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
        //-------------------------------------------------------------------------------\\

    }
}