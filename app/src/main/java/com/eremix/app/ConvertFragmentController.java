package com.eremix.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class ConvertFragmentController extends Fragment {

    private ExchangeRatesModel model;
    private Context context;
    private MainActivity activity;
    private View view;

    private View fromChosenCountryView;
    private View toChosenCountryView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.convert_fragment, container, false);
        context = getContext();

        activity = (MainActivity) context;
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            activity.setConvertFragmentController(this);
            activity.refreshSettingButtonVisibility(true);
            activity.getSupportActionBar().setTitle("Currency converter");
        }

        model = new ExchangeRatesModel(this, activity);

        fromChosenCountryView = view.findViewById(R.id.from_chosen_country);
        toChosenCountryView = view.findViewById(R.id.to_chosen_country);

        //Эта функция визуализирует сохранённые данные и сохраняет новые при выборе страны из списка!
        //также в ней содержится проверка на получение данных с интернета
        initWidgets();


        Button convert_button = view.findViewById(R.id.convert_button);
        EditText fromValueField = view.findViewById(R.id.convert_to_value);
        EditText resultPlace = view.findViewById(R.id.convert_result_value);
        convert_button.setOnClickListener(e -> getConvert(fromValueField, view));

        ImageButton swapButton = view.findViewById(R.id.swap_button);
        ImageButton cleanHistoryButton = view.findViewById(R.id.h_clean_button);
        ImageButton cleanAmountButton = view.findViewById(R.id.cleanButton);
        swapButton.setOnClickListener(e -> swap());
        cleanHistoryButton.setOnClickListener(e -> cleanHistory());
        cleanAmountButton.setOnClickListener(e -> cleanAmount(fromValueField, resultPlace));

        return view;
    }

    public void setupLastCurrencyDate(String date) {
        TextView textView = view.findViewById(R.id.rateDate);

        //Если данных изначальных нет (такое может произойти только если при первом запуске не будет инета)
        if (activity.getSavedCurrencyDate().equals("NODATA")) {
            textView.setText("Check network connection!");
        } else {
            textView.setText("Last currency update - " + date);
        }

    }

    public void swap() {
        MainActivity mainActivity = (MainActivity) context;
        Country currFromCountry = mainActivity.getCountryData("FROM", null);
        Country currToCountry = mainActivity.getCountryData("TO", null);

        mainActivity.saveDataToPrefs("FROM", currToCountry);
        mainActivity.saveDataToPrefs("TO", currFromCountry);

        setupChooseCountryViews("FROM", fromChosenCountryView);
        setupChooseCountryViews("TO", toChosenCountryView);
    }

    public void cleanHistory() {
        activity.deleteHistory();
        setupHistoryView();
    }

    public void cleanAmount(EditText fromValueField, EditText convertedValueField) {
        fromValueField.setText("");
        convertedValueField.setText("");
    }

    public void getConvert(EditText fromValueField, View view) {
        Country fromCountry = activity.getCountryData("FROM", null);
        Country toCountry = activity.getCountryData("TO", null);

        if (fromCountry == null || toCountry == null) {
            Toast.makeText(context, "Check selected currencies!", Toast.LENGTH_SHORT).show();
            return;
        } else if (fromValueField.getText().toString().isEmpty()) {
            Toast.makeText(context, "Amount is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        double fromValue = Double.parseDouble(fromValueField.getText().toString());
        double result = model.convert(fromValue, fromCountry.getValue(), toCountry.getValue());

        EditText resultPlace = view.findViewById(R.id.convert_result_value);
        resultPlace.setText(String.valueOf(result));

        //При нажатии кнопки происходит создание экземпляра истории и обновление сохранённого списка

        HistoryUnit newUnit = new HistoryUnit(fromCountry, toCountry, fromValue, result);
        activity.refreshHistoryData(newUnit);
        setupHistoryView();
    }

    public void setupHistoryView() {
        ListView historyLV = view.findViewById(R.id.history_listview);

        LinkedList<HistoryUnit> units = activity.getSavedHistory();

        if (units != null) {
            HistoryAdapter adapter = new HistoryAdapter(context, R.layout.historyunit_layout, units);
            historyLV.setEmptyView(view.findViewById(R.id.empty));
            historyLV.setAdapter(adapter);
        }
    }

    public void initWidgets() {
        Bundle args = getArguments();
        if (args != null) {
            String countryType = args.getString("COUNTRY_TYPE");
            if (countryType != null && !countryType.isEmpty()) {
                Country country = (Country) args.getSerializable(Country.class.getSimpleName());
                activity.saveDataToPrefs(countryType, country);
            }
            args.clear();
        }

        checkForGetXML(); //Проверка на необходимость получить данные с центробанка (XML)
        setupChooseCountryViews("FROM", fromChosenCountryView); //Визуализация from виджета
        setupChooseCountryViews("TO", toChosenCountryView); //Визуализация to виджета
        setupHistoryView(); //Визуализация истории
        setupLastCurrencyDate(activity.getSavedCurrencyDate()); //Визуализация текста о последнем обновлении
        setChoosingNavListeners();
    }



    public void setupChooseCountryViews(String key, View view) {
        Country country = ((MainActivity) context).getCountryData(key, null);
        TextView charCodeText = view.findViewById(R.id.chosen_charcode);
        ImageView flagView = view.findViewById(R.id.chosen_country_flag);

        if (country != null) {
            charCodeText.setText(country.getCharCode());
            String imgLink = country.getImageResourceLink();
            if (imgLink != null) Picasso.get().load(imgLink).into(flagView);
        } else {
            charCodeText.setText("TAP");
            flagView.setImageResource(R.drawable.missing);
        }

    }

    public void setChoosingNavListeners() {
        fromChosenCountryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("COUNTRY_TYPE", "FROM");
                Navigation.findNavController(view).navigate(R.id.action_convert_fragment_nav_to_choosing_country_nav, arguments);
                activity.refreshSettingButtonVisibility(false);
            }
        });
        toChosenCountryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("COUNTRY_TYPE", "TO");
                Navigation.findNavController(view).navigate(R.id.action_convert_fragment_nav_to_choosing_country_nav, arguments);
                activity.refreshSettingButtonVisibility(false);
            }
        });
    }

    public void checkForGetXML() {
        if (!activity.getAppState() && activity.isNetworkConnected()) {
            getXMLContent(model.getCurrentDate());
            activity.setAppState(true);
        } else if (!activity.getAppState() && !activity.isNetworkConnected()) {
            Toast.makeText(context, "Check your network connection! The app will work on saved currency data!", Toast.LENGTH_LONG).show();
            activity.setAppState(true);
        }
    }
    public void getXMLContent(String date) {
        model.asyncGetXMLContent(date);
    }

    public void onGetXMLContent(String content) {
        XMLParser parser = new XMLParser(model);
        parser.parseXMLandSaveData(content, context, "ratesData.txt");
    }
}
