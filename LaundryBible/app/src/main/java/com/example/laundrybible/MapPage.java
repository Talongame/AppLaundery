//https://webnautes.tistory.com/1249 에서 지도 사용법 참고함

package com.example.laundrybible;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ActionMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.security.Permission;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapPage extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    private GoogleMap mMap; // 맵에 대한 것
    Intent intent; // 화면 바꿀 때 필요
    Button retBtn; // 뒤로 가기 버튼

    // 나의 현재 위치 저장 장소
    double nowLatitude;  //위도
    double nowLongitude; //경도
    //LatLng MYLOCATION = new LatLng(nowLatitude, nowLongitude);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 제거
        //getSupportActionBar().setTitle("세탁의 바이블");
        //getSupportActionBar().setSubtitle("지도보기");
        setContentView(R.layout.map_page);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        retBtn = findViewById(R.id.retBtn);
        retBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager lcManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener lcListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                alertProvider(provider);
            }
            @Override
            public void onProviderEnabled(String provider) {
                alertStatus(provider);
            }
            @Override
            public void onProviderDisabled(String provider) {
                checkProvider(provider);
            }
        };

        // 권한 승인에 대한 검사 및 처리 부분
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permission1== PackageManager.PERMISSION_GRANTED && permission2==PackageManager.PERMISSION_GRANTED){
            lcManager.requestLocationUpdates(lcManager.NETWORK_PROVIDER, 100, 0, lcListener);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

    }

    private void checkProvider(String provider){
        Toast.makeText(this, provider + "에 의한 위치 서비스가 꺼져있습니다.", Toast.LENGTH_SHORT).show();
        intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void alertProvider(String provider){
        Toast.makeText(this, provider + "서비스가 켜졌습니다.", Toast.LENGTH_SHORT).show();
    }

    private void alertStatus(String provider){
        Toast.makeText(this, "위치서비스가 " + provider + "로 변경되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void updateMap(Location location){
        nowLatitude = location.getLatitude();   //위도
        nowLongitude = location.getLongitude(); //경도

        System.out.println("위도: " + nowLatitude + " 경도: " + nowLongitude);

        LatLng MYLOCATION = new LatLng(nowLatitude, nowLongitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(MYLOCATION);
        markerOptions.title("현재위치");
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MYLOCATION, 15));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}