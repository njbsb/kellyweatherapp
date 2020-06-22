package com.fyp.kellyweatherapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.model.POJO.Daily;
import com.fyp.kellyweatherapp.model.POJO.Weather;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.fyp.kellyweatherapp.R.id.dialog_date;
import static com.fyp.kellyweatherapp.R.id.dialog_day;
//import static com.fyp.kellyweatherapp.R.id.dialog_daydate;
//import static com.fyp.kellyweatherapp.R.id.dialog_dayname;

public class WeatherDailyAdapter extends RecyclerView.Adapter<WeatherDailyAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Daily> dailyList;
    private static String imageURL = "https://openweathermap.org/img/wn/";
    private Dialog dialog;

    public WeatherDailyAdapter(Context context, ArrayList<Daily> dailyList) {
        this.context = context;
        this.dailyList = dailyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Daily day = dailyList.get(position);
        Weather weather = day.getWeather().get(0);
        holder.dailyTitle.setText(day.getDayName());
        holder.dailyDate.setText(day.getDatefromDT());
        Picasso.get()
                .load(weather.getIconURL())
                .error(R.drawable.ic_weatherwarning)
                .into(holder.dailyImage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, String.format("Card %s clicked", day.getDayName()), Toast.LENGTH_SHORT).show();
                loadDialog(day);
//                ForecastFragment dialogForecast = new ForecastFragment();
//                dialogForecast.show(, "");
            }
        });
    }

    private void loadDialog(Daily day) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_forecast);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img = dialog.findViewById(R.id.dialog_icon);
        TextView tv_day = dialog.findViewById(dialog_day);
        TextView tv_date = dialog.findViewById(dialog_date);
        TextView tv_cond = dialog.findViewById(R.id.dialog_weather);
        ImageView noti = dialog.findViewById(R.id.dialog_notification);
        ImageView planner = dialog.findViewById(R.id.dialog_planner);

        String condition = day.getWeather().get(0).getMain();
        String conddesc = day.getWeather().get(0).getDescription();
        tv_day.setText(day.getDayName());
        tv_date.setText(day.getDatefromDT());
        tv_cond.setText(String.format("%s\n(%s)", condition, conddesc));
        Picasso.get().load(day.getWeather().get(0).getIconURL()).into(img);

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    // NEW CLASS VIEWHOLDER
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView dailyImage;
        private TextView dailyTitle, dailyDate;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dailyImage = itemView.findViewById(R.id.dailyImage);
            dailyTitle = itemView.findViewById(R.id.dailyText);
            dailyDate = itemView.findViewById(R.id.dailyDate);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }


}
