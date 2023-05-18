package com.eremix.app;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.List;

public class ChooseCountryController extends Fragment {
    private Context context;

    private String countryRequestType;

    private ChooseCountryModel countryModel;

    private Bundle args;

    private MainActivity mainActivity;

    private ListView chooseCountryListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Интерфейс
        View view = inflater.inflate(R.layout.choosing_country, container, false);

        context = getContext();
        mainActivity = (MainActivity) context;
        if (mainActivity != null && mainActivity.getSupportActionBar() != null) {
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mainActivity.getSupportActionBar().setTitle("Choose a currency");
        }
        //Получение данных о модели (логическое ядро)
        countryModel = new ChooseCountryModel(context, this);

        //Важные виджеты
        chooseCountryListView = view.findViewById(R.id.countries_list);
        EditText searchField = view.findViewById(R.id.search_field);

        //Default setup countries list
        List<Country> countriesDefaultList = countryModel.getDefaultCountriesList(MainActivity.RATES_DATA_FILENAME);
        setupCountryList(chooseCountryListView, countriesDefaultList);
        setupSearchSystem(searchField);

        //Получение типа реквеста с предыдущего окна (FROM или TO)
        args = getArguments();
        if (args == null) Toast.makeText(context, "Bundle is not defined", Toast.LENGTH_SHORT).show();
        else countryRequestType = args.getString("COUNTRY_TYPE");

        mainActivity.getSavedFavData().forEach(System.out::println);
        return view;
    }

    //Обеспечение работы виджетам, отвечающих за поиск
    public void setupSearchSystem(EditText searchField) {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Если поле пустое, то показать дефолтный список
                if (searchField.getText().toString().equals(""))
                    setupCountryList(chooseCountryListView, countryModel.getDefaultCountriesList(MainActivity.RATES_DATA_FILENAME));

                //Получить кастомный список через ключ
                List<Country> foundCountries = countryModel.getSearchCountriesList(s.toString());

                //Создать адаптер с полученными данными
                setupCountryList(chooseCountryListView, foundCountries);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    //Calls only from model (prefs modifying listener)
    public void onRefreshFavouritesSaves() {
        System.out.println("onRefreshFavouritesSaves");
        setupCountryList(chooseCountryListView, countryModel.getDefaultCountriesList(MainActivity.RATES_DATA_FILENAME));
    }
    //Визуализация списка
    public void setupCountryList(ListView listView, List<Country> countries) {

        CountryAdapter countryAdapter = new CountryAdapter(context, R.layout.countryinfo_layout, countries, countryModel);
        listView.setAdapter(countryAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Country selected = countries.get(position);
                args.putSerializable(Country.class.getSimpleName(), selected);
                Navigation.findNavController(view).navigate(R.id.action_choosing_country_nav_to_convert_fragment_nav, args);
            }
        });
    }


}
