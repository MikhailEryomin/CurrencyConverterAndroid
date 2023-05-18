package com.eremix.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class ExchangeRatesModel {

    private final ConvertFragmentController controller;
    private MainActivity activity;

    public ExchangeRatesModel(ConvertFragmentController controller, MainActivity activity) {
        this.controller = controller;
        this.activity = activity;
    }

    //Эта функция запускается при работе главного графического потока
    //Выделяется отдельный поток, обрабатывающий запрос
    public void asyncGetXMLContent(String date) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Получение данных по GET-запросу
                String finalContent = getRequest(getCurrentDate());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Sending data to controller (presenter)
                        controller.onGetXMLContent(finalContent);
                    }
                });
            }
        });
    }

    //Универсальный метод получения контента
    //Удобен для тестов
    public String getRequest(String date) {
        String content = null;

        try {
            URL url = new URL("https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "windows-1251"));
            content = reader.readLine();

            reader.close();
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public String getCurrentDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("ru", "RU"));
        Date currentDate = new Date();
        return df.format(currentDate).replaceAll("\\.", "/");
    }

    //Calls only from XMLParser obj
    public void setLastCurrencyDate(String date) {
        if (activity == null) return;
        System.out.println("EEE " + date);
        controller.setupLastCurrencyDate(date);

        //Сохранение даты
        activity.saveDataToPrefs("CURR_DATE", date);
    }

    public double convert(double value, double fromCountryValue, double toCountryValue) {
        if (fromCountryValue == 0 || toCountryValue == 0) return 0;

        double relative = fromCountryValue / toCountryValue;
        double accuracyValue = 0.01;
        //Получение настройки округления!
        if (activity != null) {
            Setting def = activity.getDefaultRoundSetting();
            Setting accuracySetting = activity.getSetting("ROUND", def);
            accuracyValue = Double.parseDouble(accuracySetting.getValue());
        }

        double accuracy = 1 / accuracyValue;
        return Math.round(relative * value * accuracy) / accuracy;
    }



}
