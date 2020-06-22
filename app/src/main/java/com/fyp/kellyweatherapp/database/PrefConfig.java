package com.fyp.kellyweatherapp.database;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.fyp.kellyweatherapp.activity.MainActivity;
import com.fyp.kellyweatherapp.activity.SignupActivity;
import com.fyp.kellyweatherapp.model.POJO.CurrentWeatherData;
import com.fyp.kellyweatherapp.model.POJO.WeatherData;
import com.fyp.kellyweatherapp.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrefConfig {

    public static final String MY_PREF_NAME = "com.fyp.kellyweatherapp";
    private static final String WEATHER_DATA = "weatherdata";
    private static final String CURRENT_WEATHER_DATA = "currentweatherdata";
    private static final String USER = "user";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String TAG = "PrefConfig";
    private static User user;

    public static void saveUser(Context context, User user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUserID());
        databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "saveUser successful");
            }
        }).addOnFailureListener(e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "saveUser: unsuccessful " + e.getMessage());
            }
        );
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER, json);
        editor.apply();
    }

    public static void saveLatitude(Context context, String lat) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAT, lat);
        editor.apply();
//        Toast.makeText(context, "Latitude saved", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "saveLatitude: saved");
    }

    public static void saveLongitude(Context context, String lon) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LON, lon);
        editor.apply();
//        Toast.makeText(context, "Longitude saved", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "saveLongitude: saved");
    }

    public static void saveWeatherData(Context context, WeatherData weatherData) {
        Gson gson = new Gson();
        String json = gson.toJson(weatherData);
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(WEATHER_DATA, json);
        editor.apply();
//        Toast.makeText(context, "WeatherData saved", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "saveWeatherData: successful");
    }

    public static void saveCurrentWeatherData(Context context, CurrentWeatherData currentWeatherData) {
        Gson gson = new Gson();
        String json = gson.toJson(currentWeatherData);
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CURRENT_WEATHER_DATA, json);
        editor.apply();
//        Toast.makeText(context, "CurrentWeatherData saved", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "saveCurrentWeatherData: successful");
    }

    public static User loadUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(USER, "");
        user = gson.fromJson(json, User.class);
        assert json != null;
        if (json.equals("")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User/" + FirebaseAuth.getInstance().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    PrefConfig.saveUser(context, user);
                    Log.d(TAG, "loadUser: successful");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "loadUser: unsuccessful" + error.getMessage());
                }
            });
        } else {
            user = gson.fromJson(json, User.class);
        }
        return user;
    }

    public static String loadLatitude(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(LAT, "0");
    }

    public static String loadLongitude(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(LON, "0");
    }

    public static WeatherData loadWeatherData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(WEATHER_DATA, "");
        assert json != null;
        if(json.equals("")) {
            Toast.makeText(context, "Weather data is empty", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "loadCurrentWeatherData: WeatherData is empty.");
        }
        WeatherData weatherData = gson.fromJson(json, WeatherData.class);
        return weatherData;
    }

    public static CurrentWeatherData loadCurrentWeatherData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(CURRENT_WEATHER_DATA, "");
        assert json != null;
        if(json.equals("")) {
            Toast.makeText(context, "Current Weather data is empty", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "loadCurrentWeatherData: CurrentWeatherData is empty.");
        }
        CurrentWeatherData currentWeatherData = gson.fromJson(json, CurrentWeatherData.class);
        return currentWeatherData;
    }

    public static void registerPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unRegisterPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void removeDataFromPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
//        Toast.makeText(context, "App data is removed", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "App data is cleared.");
    }

}
