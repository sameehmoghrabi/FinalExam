package com.example.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.weatherapp.api.weather.WeatherApiManager;
import com.example.weatherapp.models.CurrentWeatherInfo;
import com.example.weatherapp.models.WeatherDescription;
import com.example.weatherapp.screens.MainActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherAppWidget extends AppWidgetProvider {

    private static WeatherApiManager weatherApiManager = new WeatherApiManager();

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);

        weatherApiManager
                .getWeatherByCityName("Beirut")
                .enqueue(new Callback<CurrentWeatherInfo>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherInfo> call, Response<CurrentWeatherInfo> response) {
                        if (response.isSuccessful()) {
                            CurrentWeatherInfo body = response.body();
                            body.getTempInfo().getTemp();
                            String currentTemp = String.valueOf(body.getTempInfo().getTemp());
                            currentTemp = String.format("%1$s \u2103", currentTemp);
                            WeatherDescription currentWeatherDescription = body.getWeatherDescriptions().get(0);
                            String iconUrl = "http://openweathermap.org/img/w/" + currentWeatherDescription.getIcon() + ".png";
                            Picasso.with(context).load(iconUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    views.setImageViewBitmap(R.id.imageView, bitmap);
                                    appWidgetManager.updateAppWidget(appWidgetId, views);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                            views.setTextViewText(R.id.textView, currentTemp);
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherInfo> call, Throwable t) {
                    }
                });


        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object

        views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
        views.setOnClickPendingIntent(R.id.textView, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

