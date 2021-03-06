package com.fyp.kellyweatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.model.Notes;
import com.fyp.kellyweatherapp.model.pojo.Daily;
import com.fyp.kellyweatherapp.model.pojo.Weather;
import com.fyp.kellyweatherapp.model.pojo.WeatherData;
import com.fyp.kellyweatherapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
        User user = PrefConfig.loadUser(context);
        Daily day = weatherData.getDaily().get(position);
        Weather weather = day.getWeather().get(0);
        Button plannerSaveBtn = view.findViewById(R.id.planner_savebtn);
        ImageView plannericon = view.findViewById(R.id.planner_icon);
        TextView plannerday = view.findViewById(R.id.planner_day);
        EditText plannerNote = view.findViewById(R.id.planner_edittext);

        if(user.getNotesList() != null) {
//            plannerNote.setText(PrefConfig.loadUser(context).getNotesList().get(position).getNotes());
            plannerNote.setText(user.getNotesList().get(position).getNotes());
        }
        else {
            plannerNote.setText(R.string.no_notes);
            List<Notes> notesList = new ArrayList<>();
            for(int i = 0; i<weatherData.getDaily().size(); i++) {
                Notes note = new Notes();
                note.setNoteDate(weatherData.getDaily().get(i).getDateasDate());
                note.setUserID(user.getUserID());
                notesList.add(note);
            }
            user.setNotesList(notesList);
            PrefConfig.saveUser(context, user);
        }

        plannerday.setText(String.format("%s | %s | %s", day.getDayName(), day.getDateddMM(), weather.getMain()));
        Picasso.get().load(weather.getIconURL()).error(R.drawable.ic_weatherwarning).into(plannericon);
        plannerNote.setHint("ha nak tulis apa");

        plannerSaveBtn.setOnClickListener(v -> {
            String note = plannerNote.getText().toString();
            if(!note.equals("")) {
                saveNotes(context, weatherData.getDaily(), note, position);
            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void saveNotes(Context context, List<Daily> dailies, String noteString, int position) {
        User user = PrefConfig.loadUser(context);
        List<Notes> notesList = user.getNotesList();
        notesList.get(position).setNotes(noteString);
        notesList.get(position).setNoteDate(dailies.get(position).getDateasDate());
        user.setNotesList(notesList);
        PrefConfig.saveUser(context, user);
        Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show();
    }
}
