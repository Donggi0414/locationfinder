package com.example.intentexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
//import android.location.LocationRequest;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.Manifest;
import android.widget.Toast;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.tasks.Task;


public class MapTrace extends AppCompatActivity implements OnMapReadyCallback {

    private FragmentManager fragmentManager;
    private MapFragment mapFragment;

    private GoogleMap googleMap;

    private List<LatLng> polylinePoints;
    private Polyline polyline;

    private boolean isTimelineEnabled = false;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_trace);

        Places.initialize(getApplicationContext(), "AIzaSyDI7Lc41sYo2DsFa-ZPpcGrabGE7WqqmCo");
        PlacesClient placesClient = Places.createClient(this);

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        polylinePoints = new ArrayList<>();

        Button btn_backToMain = findViewById(R.id.btn_backToMain);
        btn_backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapTrace.this, SubActivity.class);
                startActivity(intent);
            }
        });

        Button btn_toggleTimeline = findViewById(R.id.btn_toggleTimeline);
        btn_toggleTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleTimelineClicked();
            }
        });

    }

    private void onToggleTimelineClicked() {
        isTimelineEnabled = !isTimelineEnabled;

        if (isTimelineEnabled) {
            showTimelineRecords();
        } else {
            clearTimeline();
        }
    }

    private void showTimelineRecords() {
        // 타임라인 기록을 가져와서 폴리라인을 그리는 작업 수행
        // 구글 플레이 서비스 API나 Location History API를 사용하여 타임라인 기록을 가져와서 polylinePoints에 추가하는 작업 수행

        // Places API를 사용하여 내 타임라인 기록을 가져옵니다.
        Places.initialize(getApplicationContext(), "AIzaSyDI7Lc41sYo2DsFa-ZPpcGrabGE7WqqmCo");
        PlacesClient placesClient = Places.createClient(this);

        Task<PlaceLikelihoodResponse> placeResult = placesClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodResponse> task) {
                if (task.isSuccessful()) {
                    PlaceLikelihoodResponse likelyPlaces = task.getResult();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        LatLng location = placeLikelihood.getPlace().getLatLng();
                        polylinePoints.add(location);
                    }
                    likelyPlaces.release();

                    updatePolyline();
                } else {
                    // 타임라인 기록을 가져오는 데 실패한 경우 예외 처리
                }
            }
        });
    }

    private void clearTimeline() {
        polylinePoints.clear();
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
    }

    private void updatePolyline() {
        if (polyline != null) {
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .color(Color.BLUE)
                .width(5f)
                .addAll(polylinePoints);

        polyline = googleMap.addPolyline(polylineOptions);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermissionWithRationale() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("위치정보")
                        .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapTrace.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 위치 권한이 허용된 경우 처리할 작업
                    googleMap.setMyLocationEnabled(true);
                } else {
                    // 위치 권한이 거부된 경우 처리할 작업
                    // 예: 사용자에게 위치 권한이 필요하다는 메시지를 표시하거나 다른 조치를 취함
                    Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

//
//    위의 코드는 버튼을 클릭하여 타임라인 기능을 토글하는 기능을 구현한 코드입니다. `onToggleTimelineClicked()` 메서드는 버튼 클릭 이벤트를 처리하고, 타임라인 기능 상태에 따라 `showTimelineRecords()` 메서드를 호출하여 타임라인 기록을 가져오거나 `clearTimeline()` 메서드를 호출하여 타임라인 기록을 제거합니다. 타임라인 기록을 가져오는 작업에는 Places API를 사용하여 현재 장소를 가져오는 예시가 포함되어 있습니다.
//    또한, 위치 권한을 확인하고 요청하는 부분도 포함되어 있어, 사용자가 위치 권한을 허용해야지만 내 타임라인 기록을 가져오고 폴리라인을 그릴 수 있습니다. `checkLocationPermissionWithRationale()` 메서드는 위치 권한을 확인하고, 권한이 없는 경우에는 권한 요청 다이얼로그를 표시합니다. 사용자가 위치 권한을 허용하거나 거부한 후에는 `onRequestPermissionsResult()` 메서드가 호출되어 처리합니다.
//    위의 코드를 참고하여 타임라인 기능을 토글하는 버튼을 구현하실 수 있습니다. 필요에 따라 추가적인 기능을 구현하고 수정하여 사용해보세요.