package com.example.buslocationapp;

import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
    List<String> adapterList;
    private static int count=0;
    private static List<Bus> Buses=new ArrayList<>();
    MapsActivity activity=this;

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

            bRoutes = r.getRoutes(assets.open("RouteCodesNew.txt"),assets.open("busLinesNew.txt"));
            masterRoutes = getMasterRoutes();
            List<Map.Entry<String, String>> linesAndRoutes = RouteReader.getLinesAndRoutes();
            adapterList=new ArrayList<>();
            for(int i=0;i<masterRoutes.size();i++){
                String current=masterRoutes.get(i);
                for(Map.Entry entry: linesAndRoutes){
                    if(current.substring(0,4).trim().equals(entry.getKey())){
                        adapterList.add(entry.getValue()+" "+current.substring(4).trim());
                    }
                }
            }

            ArrayAdapter<String> masterAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, adapterList);
            masterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            masterRouteSpinner.setAdapter(masterAdapter);
            masterRouteSpinner.setOnItemSelectedListener(this);


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
        Toast.makeText(MapsActivity.this, adapterList.get(arg2), Toast.LENGTH_SHORT).show();
        routeVariants = getSelectedRouteVariants(selection);
        List<Map.Entry<String, String>> linesAndRoutes = RouteReader.getLinesAndRoutes();

        List<String> adapterList2=new ArrayList<>();
        for(int i=0;i<routeVariants.size();i++){
            String current=routeVariants.get(i);
            for(Map.Entry entry: linesAndRoutes){
                if(current.substring(0,4).trim().equals(entry.getKey())){
                    adapterList2.add(entry.getValue()+" "+current.substring(4).trim());
                }
            }
        }

        ArrayAdapter<String> variantAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, adapterList2);
        variantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeVariantSpinner.setAdapter(variantAdapter);
        String variantSelection = routeVariantSpinner.getSelectedItem().toString();
        String direction="";
        for(String route: routeVariants){
            StringTokenizer tk = new StringTokenizer(route," ");
            tk.nextToken();
            String newRoute = "";
            while(tk.hasMoreTokens()){
                newRoute+=tk.nextToken()+" ";
            }
            StringTokenizer tk2 = new StringTokenizer(variantSelection," ");
            tk2.nextToken();
            String newVariant = "";
            while(tk2.hasMoreTokens()){
                newVariant+=tk2.nextToken()+" ";
            }
            if(newRoute.trim().equals(newVariant.trim())){
                direction = route.substring(0,4).trim();
            }
        }
        StringTokenizer tk = new StringTokenizer(adapterList.get(arg2)," ");

        new SubTask().execute(tk.nextToken(),direction);

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
        //mMap.addMarker(new MarkerOptions().position(athens).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(athens, 12.0f));

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
            String line = file[0];
            String route = file[1];
            try {
                InputStream stream = s.open("brokerIPs.txt");
                new Subscriber(new ArrayList<>(),stream,activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void manageMarkers(Value vr, MapsActivity activity) {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Buses.contains(vr.getBus())) {
//            Buses.get(Buses.indexOf(vr.getBus())).getMarker().remove();
//            Buses.get(Buses.indexOf(vr.getBus())).setMarker(mMap.addMarker(new MarkerOptions().position(new LatLng(vr.getLatitude(),vr.getLongitude()))));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Buses.get(Buses.indexOf(vr.getBus())).getMarker().remove();
                    MarkerOptions markerOptions=new MarkerOptions().position(new LatLng(vr.getLatitude(),vr.getLongitude()));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon));
                    Buses.get(Buses.indexOf(vr.getBus())).setMarker(mMap.addMarker(markerOptions));
                }
            });
            //mMap.addMarker(Buses.get(Buses.indexOf(vr.getBus())).getMarker());
            //animateMarker(Buses.get(Buses.indexOf(vr.getBus())).getMarker(), new LatLng(vr.getLatitude(), vr.getLongitude()), true, activity);
        } else {
            Buses.add(vr.getBus());
//            Marker marker;
//            marker=mMap.addMarker(new MarkerOptions().position(new LatLng(vr.getLatitude(), vr.getLongitude())));
//            Buses.get(Buses.size() - 1).setMarker(marker);
            activity.runOnUiThread(new Runnable(){
                public void run(){
                    Marker marker;
                    marker=mMap.addMarker(new MarkerOptions().position(new LatLng(vr.getLatitude(), vr.getLongitude())));
                    Buses.get(Buses.size() - 1).setMarker(marker);
                    Buses.get(Buses.size() - 1).getMarker().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon));
                    System.out.println("added first mark111111111111111111111");
                }
            });

            //mMap.addMarker(Buses.get(Buses.size() - 1).getMarker());
        }
    }

        public static void animateMarker (final MarkerOptions marker, final LatLng toPosition, final boolean hideMarker, MapsActivity activity){
            System.out.println("In the animate!!!!!!!!!!!!");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("In the UIthread--------------");
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
                            System.out.println("In the post+++++++++++++++++++");
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
            });
            //final Handler handler = new Handler();

        }


}
