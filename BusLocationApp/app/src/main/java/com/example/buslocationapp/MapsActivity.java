package com.example.buslocationapp;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Service.Subscriber;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private Spinner masterRouteSpinner, routeVariantSpinner;
    RouteReader r = new RouteReader();
    List<Routes> bRoutes;
    List<String> masterRoutes;
    List<String> routeVariants;

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
        mMap.addMarker(new MarkerOptions().position(athens).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));
        new SubTask().execute();
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
    private class SubTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... file) {
            AssetManager s = getAssets();
            try {
                InputStream stream = s.open("brokerIPs.txt");
                new Subscriber(new ArrayList<>(),stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
