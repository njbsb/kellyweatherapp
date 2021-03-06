package com.fyp.kellyweatherapp.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.adapter.PlannerAdapter;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.model.Notes;
import com.fyp.kellyweatherapp.model.pojo.Daily;

import com.fyp.kellyweatherapp.model.pojo.WeatherData;
import com.fyp.kellyweatherapp.model.User;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlannerFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "PlannerFragment";

    private SpringDotsIndicator springDotsIndicator;
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
        springDotsIndicator = view.findViewById(R.id.spring_dots_indicator);

        if(PrefConfig.loadUser(Objects.requireNonNull(getContext())) != null && PrefConfig.loadWeatherData(Objects.requireNonNull(getContext())) != null) {
            user = PrefConfig.loadUser(Objects.requireNonNull(getContext()));
            weatherData = PrefConfig.loadWeatherData(Objects.requireNonNull(getContext()));
            dailies = PrefConfig.loadWeatherData(getContext()).getDaily();
        }

        if(user.getNotesList() == null) {
            notesList = new ArrayList<>();
            for(int i = 0; i<8; i++) {
                Notes notes = new Notes();
                notes.setUserID(user.getUserID());
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
        viewPager.setPadding(110, 0, 110, 0);
        springDotsIndicator.setViewPager(viewPager);
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

    private void saveNotes() {

    }


    @Override
    public void onClick(View v) {

    }
}
