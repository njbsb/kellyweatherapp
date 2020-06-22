package com.fyp.kellyweatherapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.adapter.PlannerAdapter;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.model.Notes;
import com.fyp.kellyweatherapp.model.POJO.Daily;
import com.fyp.kellyweatherapp.model.POJO.Weather;
import com.fyp.kellyweatherapp.model.POJO.WeatherData;
import com.fyp.kellyweatherapp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlannerFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "PlannerFragment";

    private Button buttonSave;
    private ImageView forecastIcon;
    private TextView forecastDay, forecastDate;
    private ViewPager viewPager;
    private PlannerAdapter plannerAdapter;
    private WeatherData weatherData;
    private List<Daily> dailies;
    private List<Notes> notesList;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planner, container, false);
        buttonSave = view.findViewById(R.id.btn_savePlanner);

        if(PrefConfig.loadUser(Objects.requireNonNull(getContext())) != null && PrefConfig.loadWeatherData(Objects.requireNonNull(getContext())) != null) {
            user = PrefConfig.loadUser(Objects.requireNonNull(getContext()));
            weatherData = PrefConfig.loadWeatherData(Objects.requireNonNull(getContext()));
            dailies = PrefConfig.loadWeatherData(getContext()).getDaily();
        }

        if(user.getNotesList() == null) {
            // request for location
            // get latitude and longitude
            // get weather data again from api
            // OR create basic list of size 8
            notesList = new ArrayList<>();
            for(int i = 0; i<8; i++) {
                Notes notes = new Notes();
                notes.setUserID(user.getUserID());
//                notes.setNoteDate(dailies.get(i).getDateasDate());
//                notes.setUserID(user.getUserID());
//                notes.setNoteID(String.format("%s_%s", user.getUserID(), dailies.get(i).getDt()));
                // later can remove noteID since we'll be checking 2 conds: userID and date
                notesList.add(notes);
            }
            user.setNotesList(notesList);
            PrefConfig.saveUser(getContext(), user);
        }
        else {
            notesList = user.getNotesList();
        }
        setUpViewPager(view);
        return view;
    }

    private void setUpViewPager(View view) {
        plannerAdapter = new PlannerAdapter(notesList, weatherData, getContext());
        viewPager = view.findViewById(R.id.planner_viewpager);
        viewPager.setAdapter(plannerAdapter);
        viewPager.setPadding(130, 0, 130, 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void shortToast(String m) {
        Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_savePlanner:
                shortToast("clicked saved");
                break;
        }
    }
}
