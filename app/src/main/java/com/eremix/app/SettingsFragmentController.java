package com.eremix.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Set;

public class SettingsFragmentController extends Fragment {

    private MainActivity mainActivity;
    private SharedPreferences appSettings;
    private RadioGroup roundGroup;
    private RadioGroup limitGroup;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_layout, container, false);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            appSettings = mainActivity.getAppSettings();
            mainActivity.getSupportActionBar().setTitle("Settings");
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mainActivity.setSettingsFragmentController(this);
        }
        roundGroup = view.findViewById(R.id.settings_round_radiogroup);
        limitGroup = view.findViewById(R.id.settings_limit_radiogroup);
        initWidgets();
        roundGroup.setOnCheckedChangeListener(this::changeRoundOrLimit);
        limitGroup.setOnCheckedChangeListener(this::changeRoundOrLimit);
        Button clearButton = view.findViewById(R.id.settings_clean_button);
        clearButton.setOnClickListener(e -> mainActivity.cleanAllSaveData());
        return view;
    }



    //Инициализация за счет сохраненных данных
    public void initWidgets() {
        Setting roundSetting = mainActivity.getSetting("ROUND", mainActivity.getDefaultRoundSetting());
        RadioButton btn1 = view.findViewById(roundSetting.getGroupListId());
        btn1.setChecked(true);

        Setting limitSetting = mainActivity.getSetting("HU_LIMIT", mainActivity.getDefaultLimitSetting());
        RadioButton btn2 = view.findViewById(limitSetting.getGroupListId());
        btn2.setChecked(true);
    }

    public void changeRoundOrLimit(RadioGroup group, int checkedId) {
        RadioButton btn = view.findViewById(checkedId);
        Setting setting = new Setting(checkedId, btn.getText().toString());
        if (group.getId() == R.id.settings_round_radiogroup) {
            mainActivity.saveSetting("ROUND", setting);
        } else {
            mainActivity.saveSetting("HU_LIMIT", setting);
        }
    }



}
