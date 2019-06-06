package com.pinka.retrofit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinka.retrofit.rest.OpenWeatherRepo;
import com.pinka.retrofit.rest.entites.WeatherRequestRestModel;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView textTemp;              // Температура (в Кельвинах)
    private TextInputEditText editCity;
    private SharedPreferences sharedPref;

    WeatherRequestRestModel model = new WeatherRequestRestModel();

    private String cityNamePrefsKey = "cityName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGui();
        initPreferences();
        initEvents();
        requestRetrofit(loadCityName());
    }

    private void initPreferences(){
        sharedPref = getPreferences(MODE_PRIVATE);
    }

    // Проинициализировать пользовательские элементы
    private void initGui(){
        textTemp = findViewById(R.id.tempTextView);
        editCity = findViewById(R.id.editCity);
    }

    // Здесь создадим обработку клика кнопки
    private void initEvents(){
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();    // Сохранить настройки
                String text = Objects.requireNonNull(editCity.getText()).toString();
                requestRetrofit(text);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        savePreferences();
    }

    // Сохранить настройки
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(cityNamePrefsKey, Objects.requireNonNull(editCity.getText()).toString());
        editor.apply();
    }

    private String loadCityName() {
        return sharedPref.getString(cityNamePrefsKey, "Moscow");
    }

    private void requestRetrofit(String city) {
        OpenWeatherRepo.getSingleton().getAPI().loadWeather(city + ",ru",
                "762ee61f52313fbd10a4eb54ae4d4de2", "metric")
                .enqueue(new Callback<WeatherRequestRestModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call,
                                           @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            model = response.body();
                            setTemperature();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequestRestModel> call, Throwable t) {
                        textTemp.setText(R.string.error);
                    }
                });

    }

    private void setTemperature() {
        String text = getString(R.string.temperature) + ": " + String.valueOf(model.main.temp);
        textTemp.setText(text);
    }


}
