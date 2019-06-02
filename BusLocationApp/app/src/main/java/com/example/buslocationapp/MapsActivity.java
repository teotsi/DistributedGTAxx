package com.example.buslocationapp;

import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import Service.Bus;
import Service.Subscriber;
import Service.Value;

import static java.lang.Thread.sleep;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private static GoogleMap mMap;
    private Spinner masterRouteSpinner, routeVariantSpinner;
    RouteReader r = new RouteReader();
    List<Routes> bRoutes;
    List<String> masterRoutes;
    List<String> routeVariants;
    private static int count=0;
    private static List<Bus> Buses=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        AssetManager assets = getAssets();

        try{

            masterRouteSpinner = (Spinner)findViewById(R.id.MasterRouteSpinner);
            routeVariantSpinner = (Spinner)findViewById(R.id.RouteVariantSpinner);

            bRoutes = r.getRoutes(assets.open("RouteCodesNew.txt"));
            masterRoutes = getMasterRoutes();

            masterRouteSpinner.setOnItemSelectedListener(this);


            ArrayAdapter<String> masterAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, masterRoutes);
            masterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            masterRouteSpinner.setAdapter(masterAdapter);


            routeVariantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MapsActivity.this, routeVariants.get(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3){
        String selection = masterRoutes.get(arg2);
        Toast.makeText(MapsActivity.this, masterRoutes.get(arg2), Toast.LENGTH_SHORT).show();
        routeVariants = getSelectedRouteVariants(selection);
        ArrayAdapter<String> variantAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, routeVariants);
        variantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeVariantSpinner.setAdapter(variantAdapter);
        // TODO: 2/6/2019 Call subscriber here and give him the line and the direction in the constructor, not only a list of brokers.
    }

    public void manageMarkerss(){

    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0){

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
        LatLng athens = new LatLng(37.983810, 23.727539);
        //LatLng nextToit = new LatLng(37.973810, 23.727539);
        //mMap.addMarker(new MarkerOptions().position(athens).title("Marker in Athens"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(athens, 12.0f));
        Subscriber.calledFromActivity();

    }

    public static void calledFromSubscriber(){
        System.out.println("ok it worssssssssssssssssssssssssssssssssssssss");
    }

    public List<String> getMasterRoutes(){
        List<String> mRoutes = new ArrayList<>();
        for(Routes r : bRoutes){
            mRoutes.add(r.getMasterRoute());
        }
        return mRoutes;
    }

    public List<String> getSelectedRouteVariants(String selection){
        List<String> list = new ArrayList<>();
        for(Routes r : bRoutes){
            if(r.getMasterRoute().equals(selection)){
                list = r.getRouteVariants();
                break;
            }
        }return list;
    }

    public static synchronized boolean pull(ObjectInputStream in) {
        try {
            Value vr = (Value) in.readObject();
            System.out.println(vr);
            if (vr.getLongitude()==10.0) {
                System.out.println("found null");
                return false;
            }
            visualiseData(vr);
        } catch (EOFException e){
            try {
                System.out.println("sleep");
                sleep(1000);
                count++;
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            if(e.getMessage().contains("Connection reset")){
                System.out.println("Connection reset. Service.Broker may be down.");
                return false;
            }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void visualiseData(Value vr) {
        if (Buses.contains(vr.getBus())){
            Buses.get(Buses.indexOf(vr.getBus())).getMarker().position(new LatLng(vr.getLatitude(),vr.getLongitude()));
            animateMarker(Buses.get(Buses.indexOf(vr.getBus())).getMarker(),new LatLng(vr.getLatitude(),vr.getLongitude()),true);
        }else{
            Buses.add(vr.getBus());
            Buses.get(Buses.size()-1).setMarker(new MarkerOptions());
            Buses.get(Buses.size()-1).getMarker().position(new LatLng(vr.getLatitude(),vr.getLongitude()));
            mMap.addMarker(Buses.get(Buses.size()-1).getMarker());
        }

    }

    public static void animateMarker(final MarkerOptions marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.position(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.visible(false);
                    } else {
                        marker.visible(true);
                    }
                }
            }
        });
    }


}
