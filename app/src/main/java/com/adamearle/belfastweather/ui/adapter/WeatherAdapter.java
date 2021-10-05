package com.adamearle.belfastweather.ui.adapter;

import static com.adamearle.belfastweather.network.ServiceGenerator.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adamearle.belfastweather.R;
import com.adamearle.belfastweather.model.Weather;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private static final String TAG = "WeatherAdapter";

    private Context mContext;
    private List<Weather> mWeather;

    public WeatherAdapter(Context context, List<Weather> weather) {
        this.mContext = context;
        this.mWeather = weather;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Weather weather = null;
        if (holder.getAdapterPosition() != -1) {
            weather = mWeather.get(holder.getAdapterPosition());
        }

        if (weather != null) {
            String applicableDate = weather.getApplicable_date();
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(applicableDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                applicableDate = new SimpleDateFormat("dd/MM/yy").format(date);
            }
            holder.applicableDate.setText(applicableDate);

            holder.theTemp.setText(String.valueOf(weather.getThe_temp()));
            holder.stateName.setText(weather.getWeather_state_name());
            holder.stateAbbr.setText(weather.getWeather_state_abbr());

            Glide.with(mContext).load(
                    BASE_URL + "/static/img/weather/png/"
                            + weather.getWeather_state_abbr() + ".png"
            ).into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return mWeather.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView stateName, stateAbbr, applicableDate, theTemp;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stateName = itemView.findViewById(R.id.weather_stateName);
            stateAbbr = itemView.findViewById(R.id.weather_stateAbbr);
            applicableDate = itemView.findViewById(R.id.weather_applicableDate);
            theTemp = itemView.findViewById(R.id.weather_theTemp);
            icon = itemView.findViewById(R.id.weather_icon);
        }
    }
}