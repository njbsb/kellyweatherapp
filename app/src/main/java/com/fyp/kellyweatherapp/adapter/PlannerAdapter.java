package com.fyp.kellyweatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.model.Notes;
import com.fyp.kellyweatherapp.model.POJO.Daily;
import com.fyp.kellyweatherapp.model.POJO.Weather;
import com.fyp.kellyweatherapp.model.POJO.WeatherData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlannerAdapter extends PagerAdapter {

    private List<Notes> notesList;
    private WeatherData weatherData;
    private LayoutInflater inflater;
    private Context context;

    public PlannerAdapter(List<Notes> notesList, WeatherData weatherData, Context context) {
        this.notesList = notesList;
        this.weatherData = weatherData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.planner_item, container, false);

        Daily day = weatherData.getDaily().get(position);
        Weather weather = day.getWeather().get(0);

        ImageView plannericon = view.findViewById(R.id.planner_icon);
        TextView plannerday = view.findViewById(R.id.planner_day);
        EditText plannerNote = view.findViewById(R.id.planner_edittext);

        plannerday.setText(String.format("%s | %s | %s", day.getDayName(), day.getDateddMM(), weather.getMain()));
        Picasso.get().load(weather.getIconURL()).error(R.drawable.ic_weatherwarning).into(plannericon);
        plannerNote.setHint("ha nak tulis apa");
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
//        super.destroyItem(container, position, object);
//        throw new UnsupportedOperationException("Required method destroyItem was not overridden");
    }
}
