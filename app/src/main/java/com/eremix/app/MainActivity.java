package com.eremix.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //settings
    public static final String RATES_DATA_FILENAME = "ratesData.txt";
    public static final String RATES_DATA_FILENAME_TEST = "testRatesData.txt";
    private boolean isXMLReceived = false;
    private SharedPreferences preferences;
    private SharedPreferences appSettings;
    private ConvertFragmentController convertFragmentController;
    private SettingsFragmentController settingsFragmentController;
    private final Setting defaultRoundSetting = new Setting(R.id.settings_round_second, "0.01");
    private final Setting defaultLimitSetting = new Setting(R.id.setting_limit_second, "10");

    private boolean settingsButtonIsVisible = true;

    private Menu menu;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("CHOOSE_DATA", MODE_PRIVATE);
        appSettings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_settings);
        menuItem.setVisible(settingsButtonIsVisible);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            //Переход к фрагменту настроек
            controller.navigate(R.id.action_convert_fragment_nav_to_settings_layout);
            refreshSettingButtonVisibility(false);
        }
        if (id == android.R.id.home) {
            controller.navigateUp();
        }
        return super.onOptionsItemSelected(item);
    }

    //for settingController (saving and getting data)
    public SharedPreferences getAppSettings() {
        return appSettings;
    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean getAppState() {
        return isXMLReceived;
    }
    public void setAppState(boolean value) {
        isXMLReceived = value;
    }

    public void refreshSettingButtonVisibility(boolean value) {
        settingsButtonIsVisible = value;
        invalidateOptionsMenu();
    }

    public Setting getDefaultLimitSetting() {
        return defaultLimitSetting;
    }

    public Setting getDefaultRoundSetting() {
        return defaultRoundSetting;
    }

    public void saveDataToPrefs(String key, Serializable value) {
        SharedPreferences.Editor editor = preferences.edit();
        String toPut;
        if (value instanceof Country) {
            Gson gson = new Gson();
            toPut = gson.toJson(value);

        } else toPut = value.toString();
        editor.putString(key, toPut);
        editor.apply();
    }

    public void refreshFavData(String value, boolean isRemoving) {
        List<String> lastFavData = getSavedFavData();
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        if (lastFavData != null) {
            if (isRemoving) {
                lastFavData.remove(value);
            } else {
                lastFavData.add(value);
            }
        } else {
            lastFavData = new ArrayList<>();
        }

        String toPut = gson.toJson(lastFavData);
        editor.putString("FAV",toPut);
        editor.apply();
    }

    public void refreshHistoryData(Serializable value) {
        if (!(value instanceof HistoryUnit)) return;
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        LinkedList<HistoryUnit> lastHistorySaved = getSavedHistory();

        //int historyLimit = Integer.parseInt(getSetting("HU_LIMIT"));

        if (lastHistorySaved != null) {
            lastHistorySaved.push((HistoryUnit) value);
            int limitSetting = Integer.parseInt(getSetting("HU_LIMIT", getDefaultLimitSetting()).getValue());
            if (lastHistorySaved.size() > limitSetting) {
                lastHistorySaved.removeLast();
            }
        } else {
            lastHistorySaved = new LinkedList<>();
        }

        String toPut = gson.toJson(lastHistorySaved);
        editor.putString("HIST", toPut);
        editor.apply();
    }

    public List<String> getSavedFavData() {
        Gson gson = new Gson();
        String json = preferences.getString("FAV", null);
        if (json != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    public Country getCountryData(String key, String defValue) {
        Gson gson = new Gson();
        return gson.fromJson(preferences.getString(key, defValue), Country.class);
    }

    public String getSavedCurrencyDate() {
        return preferences.getString("CURR_DATE", "NODATA");
    }

    public LinkedList<HistoryUnit> getSavedHistory() {
        Gson gson = new Gson();
        String json = preferences.getString("HIST", null);
        if (json != null) {
            Type type = new TypeToken<LinkedList<HistoryUnit>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new LinkedList<>();
        }
    }

    public void deleteHistory() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("HIST");
        editor.apply();
    }

    public void cleanAllSaveData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("FROM");
        editor.remove("TO");
        editor.remove("HIST");
        editor.remove("FAV");
        editor.apply();
        SharedPreferences.Editor settingsEditor = appSettings.edit();
        settingsEditor.remove("ROUND");
        settingsEditor.remove("HU_LIMIT");
        settingsEditor.apply();
        if (convertFragmentController != null) convertFragmentController.initWidgets();
        if (settingsFragmentController != null) settingsFragmentController.initWidgets();
        Toast.makeText(this, "Cache cleared successfully!", Toast.LENGTH_SHORT).show();
    }

    public void saveSetting(String key, Setting setting) {
        SharedPreferences.Editor editor = appSettings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(setting);
        editor.putString(key, json);
        editor.apply();
    }

    public Setting getSetting(String key, Setting def) {
        Gson gson = new Gson();
        String json = appSettings.getString(key, null);
        Setting toReturn = def;

        if (json != null) toReturn = gson.fromJson(json, Setting.class);
        return toReturn;
    }

    public void setConvertFragmentController(ConvertFragmentController controller) {
        convertFragmentController = controller;
    }

    public void setSettingsFragmentController(SettingsFragmentController controller) {
        settingsFragmentController = controller;
    }



}

