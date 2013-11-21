package com.tourist.Weatherix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
    public static final String[] CITIES = {"Москва", "Санкт-Петербург", "Минск", "Гомель", "Сочи"};
    public static final String[] CITIES_ENG = {"Moscow", "Saint+Petersburg", "Minsk", "Gomel", "Sochi"};
    public static final String CODE = "city";
    public static final String CODE_ENG = "city_eng";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView lvChooser = (ListView) findViewById(R.id.lvChooser);
        ArrayAdapter<String> adapterC = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, CITIES);
        lvChooser.setAdapter(adapterC);
        lvChooser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), WeatherActivity.class);
                intent.putExtra(CODE, CITIES[position]);
                intent.putExtra(CODE_ENG, CITIES_ENG[position]);
                startActivity(intent);
            }
        });
    }
}
