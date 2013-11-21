package com.tourist.Weatherix;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;

public class WeatherActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        Intent intent = getIntent();
        String city = intent.getStringExtra(MainActivity.CODE);
        TextView cityName = (TextView) findViewById(R.id.city_name);
        cityName.setText(city);
        String city_eng = intent.getStringExtra(MainActivity.CODE_ENG);
        new WeatherTask().execute(city_eng);
    }

    private static final String API_KEY = "9r8bamvx7utesvcyxb993ggm";
    private static final String API_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?format=json&num_of_days=3&key=" + API_KEY;
    private static final String BAD_LUCK = "Не удалось выполнить операцию. Проверьте подключение к Интернету и попробуйте ещё раз";

    private class WeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = API_URL + "&q=" + params[0];
                HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity, "UTF-8");

                // parsing starts here
                String result = null;
                JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
                json = object.getString("data");
                object = (JSONObject) new JSONTokener(json).nextValue();
                String cond = object.getString("current_condition");
                object = new JSONArray(cond).getJSONObject(0);
                result = "";
                String weather = object.getString("weatherDesc");
                JSONObject weatherJ = new JSONArray(weather).getJSONObject(0);
                weather = weatherJ.getString("value");
                {
                    String T_API_KEY = "trnsl.1.1.20131001T130428Z.18896fd9b4b712d0.b8984cdd58a32edec6bbbd7e752ad2ad3b262b5f";
                    String T_API_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?lang=en-ru&key=" + T_API_KEY + "&text=";
                    HttpResponse httpResponseT = new DefaultHttpClient().execute(new HttpGet(T_API_URL + URLEncoder.encode(weather)));
                    HttpEntity httpEntityT = httpResponseT.getEntity();
                    String jsonT = EntityUtils.toString(httpEntityT, "UTF-8");
                    JSONObject objectT = (JSONObject) new JSONTokener(jsonT).nextValue();
                    String resultT = objectT.getString("text");
                    weather = resultT.substring(2, resultT.length() - 2);
                    if (weather.equals("Сплошная")) {
                        weather = "Пасмурно";
                    }
                }
                result += weather + "\n";
                result += "Температура: " + object.getString("temp_C") + "°C\n";
                result += "Влажность: " + object.getString("humidity") + "%\n";
                result += "Облачность: " + object.getString("cloudcover") + "%\n";
                String sPressure = object.getString("pressure");
                int pressure = 76000 * Integer.parseInt(sPressure) / 101325;
                result += "Давление: " + pressure + " мм. рт. ст.\n";
                String windEng = object.getString("winddir16Point");
                String wind = "";
                for (int i = 0; i < windEng.length(); i++) {
                    switch (windEng.charAt(i)) {
                        case 'N': wind += "С"; break;
                        case 'S': wind += "Ю"; break;
                        case 'E': wind += "В"; break;
                        case 'W': wind += "З"; break;
                    }
                }
                String sWindSpeed = object.getString("windspeedKmph");
                int windSpeed = 10 * Integer.parseInt(sWindSpeed) / 36;
                result += "Ветер: " + wind + ", " + windSpeed + " м/с\n";
                // go on to forecasts
                object = (JSONObject) new JSONTokener(json).nextValue();
                JSONArray array = new JSONArray(object.getString("weather"));
                for (int id = 0; id < 3; id++) {
                    result += "|";
                    object = array.getJSONObject(id);
                    String date = object.getString("date");
                    result += "Дата: " + date + "\n";
                    weather = object.getString("weatherDesc");
                    weatherJ = new JSONArray(weather).getJSONObject(0);
                    weather = weatherJ.getString("value");
                    {
                        String T_API_KEY = "trnsl.1.1.20131001T130428Z.18896fd9b4b712d0.b8984cdd58a32edec6bbbd7e752ad2ad3b262b5f";
                        String T_API_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?lang=en-ru&key=" + T_API_KEY + "&text=";
                        HttpResponse httpResponseT = new DefaultHttpClient().execute(new HttpGet(T_API_URL + URLEncoder.encode(weather)));
                        HttpEntity httpEntityT = httpResponseT.getEntity();
                        String jsonT = EntityUtils.toString(httpEntityT, "UTF-8");
                        JSONObject objectT = (JSONObject) new JSONTokener(jsonT).nextValue();
                        String resultT = objectT.getString("text");
                        weather = resultT.substring(2, resultT.length() - 2);
                        if (weather.equals("Сплошная")) {
                            weather = "Пасмурно";
                        }
                    }
                    result += weather + "\n";
                    result += "Температура: от " + object.getString("tempMinC") + "°C до " + object.getString("tempMaxC") + "°C\n";
                    windEng = object.getString("winddir16Point");
                    wind = "";
                    for (int i = 0; i < windEng.length(); i++) {
                        switch (windEng.charAt(i)) {
                            case 'N': wind += "С"; break;
                            case 'S': wind += "Ю"; break;
                            case 'E': wind += "В"; break;
                            case 'W': wind += "З"; break;
                        }
                    }
                    sWindSpeed = object.getString("windspeedKmph");
                    windSpeed = 10 * Integer.parseInt(sWindSpeed) / 36;
                    result += "Ветер: " + wind + ", " + windSpeed + " м/с\n";
                }
                // parsing ends here

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            TextView download = (TextView) findViewById(R.id.download);
            if (result == null) {
                download.setText(BAD_LUCK);
            } else {
                TextView now = (TextView) findViewById(R.id.now);
                now.setText("Сейчас");
                TextView now2 = (TextView) findViewById(R.id.now2);
                now2.setText("Сегодня");
                TextView now3 = (TextView) findViewById(R.id.now3);
                now3.setText("Завтра");
                TextView now4 = (TextView) findViewById(R.id.now4);
                now4.setText("Послезавтра");
                String[] weather = result.split("\\|");
                download.setText(weather[0]);
                TextView download2 = (TextView) findViewById(R.id.download2);
                download2.setText(weather[1]);
                TextView download3 = (TextView) findViewById(R.id.download3);
                download3.setText(weather[2]);
                TextView download4 = (TextView) findViewById(R.id.download4);
                download4.setText(weather[3]);
            }
        }
    }
}