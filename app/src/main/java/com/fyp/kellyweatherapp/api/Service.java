package com.fyp.kellyweatherapp.api;

import com.fyp.kellyweatherapp.model.pojo.CurrentWeatherData;
import com.fyp.kellyweatherapp.model.pojo.WeatherData;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {

    @GET("data/2.5/onecall")
    Call<JsonObject> getWeatherData(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("units") String units,
            @Query("exclude") String exclude,
            @Query("appid") String appID
    );
    @GET("data/2.5/onecall")
    Call<WeatherData> getWeeklyWeather(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("units") String units,
            @Query("exclude") String exclude,
            @Query("appid") String appID
    );

    @GET("data/2.5/weather")
    Call<CurrentWeatherData> getCurrentWeather(
            @Query("lat") String lat,
            @Query("lon") String lon,
            @Query("units") String units,
            @Query("appid") String appID
    );
}
