package com.fyp.kellyweatherapp.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.kellyweatherapp.activity.MainActivity;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.adapter.WeatherDailyAdapter;
import com.fyp.kellyweatherapp.api.Client;
import com.fyp.kellyweatherapp.api.Service;
import com.fyp.kellyweatherapp.model.POJO.CurrentWeatherData;
import com.fyp.kellyweatherapp.model.POJO.Daily;
import com.fyp.kellyweatherapp.model.POJO.WeatherData;
import com.fyp.kellyweatherapp.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    // UI components
    private RecyclerView recyclerView;
    private ProgressBar progressbarHome;
    private ImageButton refresh;
    private ImageView icon;
    private TextView tv_city, tv_coordinate, tv_condition, tv_temperature, tv_wind, tv_pressure, tv_humidity, tv_uvindex;
    // Object
    private View view;
    private ArrayList<Daily> dailies;
    // POJO
    private WeatherData weatherData;
    private CurrentWeatherData currentWeatherData;

    private static String TAG = "HomeFragment";
    public static String baseURL = "https://api.openweathermap.org/";
    public static String exclude = "minutely,hourly";
    public static String unit = "metric";
    public static String APIkey = "2302b82a0d9bcf350233fdea946f8355";
    public String latitude, longitude;


    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        progressbarHome = view.findViewById(R.id.progressbar_home);
        icon = view.findViewById(R.id.currentIcon);
        refresh = view.findViewById(R.id.btn_refresh);
        tv_city = view.findViewById(R.id.txt_city);
        tv_coordinate = view.findViewById(R.id.txt_coordinate);
        tv_condition = view.findViewById(R.id.txt_cond);
        tv_temperature = view.findViewById(R.id.txt_temp);
        tv_wind = view.findViewById(R.id.txt_windtext);
        tv_pressure = view.findViewById(R.id.txt_pressure);
        tv_humidity = view.findViewById(R.id.txt_humidity);
        tv_uvindex = view.findViewById(R.id.txt_uvitext);

        recyclerView = view.findViewById(R.id.home_recycler);

//        loadUIData();
        refresh.setOnClickListener(this);
        return view;
    }

    private void loadUIData() {
        latitude = PrefConfig.loadLatitude(Objects.requireNonNull(getContext()));
        longitude = PrefConfig.loadLongitude(Objects.requireNonNull(getContext()));
        if(latitude.equals("0") || longitude.equals("0")) {
//            getLocationData();
            ((MainActivity) Objects.requireNonNull(getActivity())).getLocation();
            latitude = PrefConfig.loadLatitude(Objects.requireNonNull(getContext()));
            longitude = PrefConfig.loadLongitude(Objects.requireNonNull(getContext()));
        }
        else {
            if(PrefConfig.loadWeatherData(getContext()) == null
                    || PrefConfig.loadCurrentWeatherData(getContext()) == null) {
                shortToast("Fetching data from API");
                Log.d(TAG, "onCreateView: PrefConfig WD and CWD is null");
                getAPIdata();
            }
            else {
                shortToast("Showing data from local storage");
                Log.d(TAG, "onCreateView: PrefConfig WD and CWD are not null");
                Date date_today = Calendar.getInstance().getTime();
                Date date_inPref = PrefConfig.loadWeatherData(getContext()).getDaily().get(0).getDateasDate();
                boolean dateUptoDate = compareDates(date_today, date_inPref);
                if(dateUptoDate) {
                    getAPIdata();
                }
                loadHomeDataWD(PrefConfig.loadWeatherData(getContext()));
                loadHomeDataCWD(PrefConfig.loadCurrentWeatherData(getContext()));
            }
            progressbarHome.setVisibility(View.INVISIBLE);
        }
    }

    private void disableUI() {
        icon.setVisibility(View.INVISIBLE);
        refresh.setVisibility(View.INVISIBLE);
        tv_city.setVisibility(View.INVISIBLE);
        tv_coordinate.setVisibility(View.INVISIBLE);
        tv_condition.setVisibility(View.INVISIBLE);
        tv_temperature.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }
    private void enableUI() {
        icon.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
        tv_city.setVisibility(View.VISIBLE);
        tv_coordinate.setVisibility(View.VISIBLE);
        tv_condition.setVisibility(View.VISIBLE);
        tv_temperature.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(PrefConfig.loadLatitude(Objects.requireNonNull(getContext())).equals("0") || PrefConfig.loadLongitude(getContext()).equals("0")) {
//            getLocationData();
            ((MainActivity) Objects.requireNonNull(getActivity())).getLocation();
        }
        loadUIData();
    }
    private static Date setTimeToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private boolean compareDates(Date dateToday, Date dateInDB) {
        Date todayDate = setTimeToMidnight(dateToday);
        Date dbDate = setTimeToMidnight(dateInDB);
//        shortToast("today: " + todayDate.toString() + " indb: " + dbDate.toString());
        return todayDate.after(dbDate);
    }

    private void getLocationData() {
        if(PrefConfig.loadLatitude(Objects.requireNonNull(getContext())).equals("0") || PrefConfig.loadLongitude(getContext()).equals("0")) {

//            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{ACCESS_FINE_LOCATION}, 1);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ((MainActivity) Objects.requireNonNull(getActivity())).getLocation();
//                return;
            }
            else {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        PrefConfig.saveLatitude(Objects.requireNonNull(getContext()), latitude);
                        PrefConfig.saveLongitude(getContext(), longitude);
                        loadUIData();
                    }
                });
            }
        }
        // no else block bcs latitude & longitude will not be used in MainActivity UI
    }

    private void getAPIdata() {
        Service serviceAPI = Client.getClient(baseURL);
//        ((MainActivity) Objects.requireNonNull(getActivity())).getLocation();
        latitude = PrefConfig.loadLatitude(Objects.requireNonNull(getContext()));
        longitude = PrefConfig.loadLongitude(getContext());
        Call<WeatherData> weeklyCall = serviceAPI.getWeeklyWeather(latitude, longitude, exclude, unit, APIkey);
        Call<CurrentWeatherData> currentCall = serviceAPI.getCurrentWeather(latitude, longitude, unit, APIkey);
        weeklyCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if(response.isSuccessful()) {
                    try {
                        weatherData = response.body();
                        PrefConfig.saveWeatherData(Objects.requireNonNull(getContext()), weatherData);
                        loadHomeDataWD(weatherData);
                    }
                    catch (Exception e) {
                        shortToast(e.getMessage());
                    }
                } }
            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                shortToast(t.getMessage());
            }
        });
        currentCall.enqueue(new Callback<CurrentWeatherData>() {
            @Override
            public void onResponse(Call<CurrentWeatherData> call, Response<CurrentWeatherData> response) {
                if(response.isSuccessful()) {
                    try {
                        currentWeatherData = response.body();
                        PrefConfig.saveCurrentWeatherData(Objects.requireNonNull(getContext()), currentWeatherData);
                        loadHomeDataCWD(currentWeatherData);
                    }
                    catch (Exception e) {
                        shortToast(e.getMessage());
                    } }
            }
            @Override
            public void onFailure(Call<CurrentWeatherData> call, Throwable t) {
                shortToast(t.getMessage());
            }
        });
    }

    private String getUVdescription(double uvi) {
        String desc = "";
        if(uvi<3)
            desc = "Low";
        else if(uvi>=3 && uvi<=5)
            desc = "Moderate";
        else if(uvi>=6 && uvi<=7)
            desc = "High";
        else if(uvi>=8 && uvi<=10)
            desc = "Very High";
        else
            desc = "Very High";
        return desc;
    }

    private void loadHomeDataWD(WeatherData weatherData) {
        dailies = (ArrayList<Daily>) weatherData.getDaily();

        String windspeed = String.valueOf(weatherData.getCurrent().getWindSpeed());
        String humidity = String.valueOf(weatherData.getCurrent().getHumidity());
        String pressure = String.valueOf(weatherData.getCurrent().getPressure());
        double uvi = weatherData.getCurrent().getUvi();
        String uvindex = String.valueOf(uvi);
        String uvindex_desc = getUVdescription(uvi);

        tv_wind.setText(String.format("%s m/s", windspeed));
        tv_humidity.setText(String.format("%s %%", humidity));
        tv_pressure.setText(String.format("%s hPa", pressure));
        tv_uvindex.setText(String.format("%s (%s)", uvindex, uvindex_desc));

        initializeRecycler(dailies);
    }

    private void loadHomeDataCWD(CurrentWeatherData currentWeatherData) {
        String city = currentWeatherData.getName();
        String country = currentWeatherData.getSys().getCountry();
        String coord = currentWeatherData.getCoord().getLat() + ", " +  currentWeatherData.getCoord().getLon();
        String condition = currentWeatherData.getWeather().get(0).getMain();
        String temperature = String.valueOf(currentWeatherData.getMain().getTemp());
        String iconURL = currentWeatherData.getWeather().get(0).getIconURL();

//        String windspeed = currentWeatherData.getWind().getSpeed().toString();
//        String humidity = currentWeatherData.getMain().getHumidity().toString();
//        String pressure = currentWeatherData.getMain().getPressure().toString();


        tv_city.setText(String.format("%s, %s", city, country));
        tv_coordinate.setText(coord);
        tv_condition.setText(condition);
        tv_temperature.setText(String.format("%s°", temperature));
        Picasso.get().load(iconURL).error(R.drawable.ic_weatherwarning).into(icon);
    }


    private void initializeRecycler(ArrayList<Daily> dailies) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        WeatherDailyAdapter dailyAdapter = new WeatherDailyAdapter(getActivity(), dailies);
        recyclerView.setAdapter(dailyAdapter);
    }
    public void shortToast(String m) {
        Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:
                disableUI();
                progressbarHome.setVisibility(View.VISIBLE);
//                getAPIdata();
                loadUIData();
                enableUI();
                progressbarHome.setVisibility(View.INVISIBLE);
                shortToast("Weather data refreshed");
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        getAPIdata();
        if(key.equals(PrefConfig.LAT) || key.equals(PrefConfig.LON)) {
            getAPIdata();

        }
        if(key.equals(PrefConfig.CURRENT_WEATHER_DATA) || key.equals(PrefConfig.WEATHER_DATA)) {
            loadUIData();
        }
    }
}
