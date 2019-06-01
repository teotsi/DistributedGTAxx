package locatebusapp.Activities;

import android.content.res.AssetManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.locatebusapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner masterRouteSpinner, routeVariantSpinner;
    private RouteReader r = new RouteReader();
    private List<Routes> bRoutes;

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
            bRoutes = r.getRoutes(assets.open("RouteCodesNew.txt"));

            masterRouteSpinner = (Spinner)findViewById(R.id.MasterRouteSpinner);
            routeVariantSpinner = (Spinner)findViewById(R.id.RouteVariantSpinner);


            ArrayAdapter<Routes> masterAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item);
            masterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            masterRouteSpinner.setAdapter(masterAdapter);
        }catch (IOException e) {
            e.printStackTrace();
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
        LatLng athens = new LatLng(37.983810, 23.727539);
        mMap.addMarker(new MarkerOptions().position(athens).title("Marker in Athens"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));
    }
}
