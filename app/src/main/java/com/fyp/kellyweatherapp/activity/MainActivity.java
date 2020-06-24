package com.fyp.kellyweatherapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.adapter.ViewPagerAdapter;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.fragment.HomeFragment;
import com.fyp.kellyweatherapp.fragment.PlannerFragment;
import com.fyp.kellyweatherapp.fragment.ProfileFragment;
import com.fyp.kellyweatherapp.model.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PERMISSION_REQ_CODE = 1;
    private boolean firstTime = false;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String latitude, longitude;
    private ProgressBar progressBar;
    private User user;
    public TextView name;
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        PrefConfig.registerPref(this, this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        PrefConfig.unRegisterPref(this, this);
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, "Youre not logged in", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        else {
            shortToast("Signed in as: " + firebaseAuth.getUid());
            user = PrefConfig.loadUser(this);
            getLocation();
            loadActivityUI();
            loadFragment();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void loadActivityUI() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        headView = navigationView.getHeaderView(0);
        name = headView.findViewById(R.id.nav_useremail);
        if(PrefConfig.loadUser(this) != null) {
            Gson gson = new Gson();
            String json = gson.toJson(PrefConfig.loadUser(this));
            Log.d("USERPREF", json);
            name.setText(PrefConfig.loadUser(getApplicationContext()).getName());
        }
        else {
            user = PrefConfig.loadUser(this);
            // get the data properly from the db
        }
        // for the drawer
        // to be safe, try to load user from db in case if previous retrieve method in loginactivity got cancelled
        // load user, check if value is null. if null, retrieve again from firebase db
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
//        getLocation();
    }

    private void loadFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container_fragment, new HomeFragment()).commit();
    }

    public void getLocation() {
        if(PrefConfig.loadLatitude(this).equals("0") || PrefConfig.loadLongitude(getApplicationContext()).equals("0")) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestRuntimePermissionLocation();
                return;
            }
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String latitude = String.valueOf(location.getLatitude());
                    String longitude = String.valueOf(location.getLongitude());
                    PrefConfig.saveLatitude(getApplicationContext(), latitude);
                    PrefConfig.saveLongitude(getApplicationContext(), longitude);
                }
            });
        }
        // no else block bcs latitude & longitude will not be used in MainActivity UI
    }

    public void requestRuntimePermissionLocation() {
        ActivityCompat.requestPermissions(this, new String[] {
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
                INTERNET
        }, PERMISSION_REQ_CODE );
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.itemHome:
                shortToast("HOME");
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new HomeFragment()).commit();
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case R.id.itemProfile:
                shortToast("PROFILE");
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new ProfileFragment()).commit();
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case R.id.itemPlanner:
                shortToast("PLANNER");
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new PlannerFragment()).commit();
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case R.id.itemExit:
                FirebaseAuth.getInstance().signOut();
                PrefConfig.removeDataFromPref(getApplicationContext());
                break;
        }
        return true;
    }

    public void shortToast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PrefConfig.MY_PREF_NAME)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new HomeFragment()).commit();
        }
        if(key.equals(PrefConfig.USER)) {
            name.setText(PrefConfig.loadUser(getApplicationContext()).getName());
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadUserDataFB extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Object doInBackground(Object[] objects) {
//            Looper.prepare();
//            User user = User.getInstance();
//            if(user.getNotesList() == null) {
//                shortToast("list is null");
//            }
//            else if(user.getNotesList().isEmpty()) {
//                shortToast("list empty");
//            }
//            else {
//                shortToast(user.getNotesList().toString());
//            }
//            runOnUiThread(MainActivity.this::getLocation);
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

}