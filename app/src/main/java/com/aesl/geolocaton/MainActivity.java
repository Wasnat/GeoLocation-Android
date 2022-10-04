package com.aesl.geolocaton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aesl.geolocaton.model.LocationModel;
import com.aesl.geolocaton.retrofit.LocationAPI;
import com.aesl.geolocaton.retrofit.RetrofitService;
import com.android.volley.RequestQueue;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    private LocationManager  locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.get_location);
        textView = (TextView) findViewById(R.id.my_location);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationModel locationModel = new LocationModel();

        RequestQueue requestQueue;

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                RetrofitService retrofitService = new RetrofitService();
                LocationAPI locationAPI = retrofitService.getRetrofit().create(LocationAPI.class);

                Toast.makeText(MainActivity.this, "\n Longitude:"+ location.getLongitude()+ " Latitude:  "+ location.getLatitude(), Toast.LENGTH_LONG).show();

                locationModel.setLongitude(location.getLongitude());
                locationModel.setLongitude(location.getLongitude());

                locationAPI.save(locationModel).enqueue(new Callback<LocationModel>() {
                    @Override
                    public void onResponse(Call<LocationModel> call, Response<LocationModel> response) {
                        Toast.makeText(MainActivity.this, "Location saved", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Call<LocationModel> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Location not saved", Toast.LENGTH_LONG).show();
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, "Error Happened!!!", t);
                    }
                });

                textView.append("\n Longitude:"+ location.getLongitude()+ " Latitude:  "+ location.getLatitude()+"\n");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LocationListener.super.onStatusChanged(provider, status, extras);
            }

            @Override
            public void onLocationChanged(@NonNull List<Location> locations) {
                LocationListener.super.onLocationChanged(locations);
            }

            @Override
            public void onFlushComplete(int requestCode) {
                LocationListener.super.onFlushComplete(requestCode);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET
                    }, 120);
                }
                return;
                }
        } else {
            configureButton();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 120:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });
    }
}