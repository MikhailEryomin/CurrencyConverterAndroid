package com.eremix.app;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseCountryModel {

    private final Context context;
    private final ChooseCountryController controller;
    private MainActivity activity;

    public ChooseCountryModel(Context context, ChooseCountryController controller) {
        this.context = context;
        this.controller = controller;
        //Я не могу сделать так, ибо может выброситься classCastException при тестировании
        //this.activity = (MainActivity) context;
        try {
            activity = (MainActivity) context;
        } catch (ClassCastException e) {
            activity = null;
        }
    }


    //Получение списка флагов
    public List<String> getFlagLinksList() {
        List<String> flagLinks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                //Файл с флагами не меняется! При изменение порядка стран в XML порядок флагов нарушится!
                new InputStreamReader(context.getAssets().open("flags.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                flagLinks.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return flagLinks;
    }


    //Получение полного списка стран
    public List<Country> getDefaultCountriesList(String fileName) {

        List<Country> countries = new ArrayList<>();
        List<String> flagLinks = getFlagLinksList();

        Country ru = new Country("RUB", "Российский рубль", 1, flagLinks.get(0));

        checkForFavourite(ru);

        countries.add(ru);
        int k = 1;

        try (FileInputStream stream = context.openFileInput(fileName)) {
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(isr);
            String line = null;

            while ((line = reader.readLine()) != null) {
                //Если пробел находится между двумя заглавными или между строчной и цифрой, то по нему сплитим
                String[] data = line.split("(?<=[A-ZА-Я])\\s(?=[A-ZА-Я])|(?<=[A-zА-я()])\\s(?=\\d)");

                String imgURL = null;
                //Пока нет конкретных картинок
                if (k < flagLinks.size()) {
                    imgURL = flagLinks.get((k++));
                }

                //Выгружаем построчно данные о странах
                String rateValue = data[2].replace(",", ".");
                Country country = new Country(data[0], data[1], Double.parseDouble(rateValue), imgURL);
                checkForFavourite(country);
                //Теперь нужно сохранить все страны в список и передать адаптеру
                countries.add(country);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //сортировка по избранным странам
        Collections.sort(countries, Country.countryComparator);
        countries.forEach(e -> System.out.print(e.isFavourite() + " "));
        return countries;
    }

    public void checkForFavourite(Country country) {
        List<String> favouritesCharCodes = new ArrayList<>();

        if (activity != null) {
            favouritesCharCodes = activity.getSavedFavData();
        }

        favouritesCharCodes.forEach(chCode -> {
            String charCode = country.getCharCode();
            if (charCode.equals(chCode)) country.setFavourite(true);
        });
    }

    public void removeCountryFromFavourites(Country country) {
        String charCode = country.getCharCode();
        if (activity != null) {
            activity.refreshFavData(charCode, true);
        }
        controller.onRefreshFavouritesSaves();
    }

    public void addCountryToFavourites(Country country) {
        String charCode = country.getCharCode();
        if (activity != null) {
            activity.refreshFavData(charCode, false);
        }
        controller.onRefreshFavouritesSaves();
    }


    //Получение кастомного списка по ключу searchKey
    public List<Country> getSearchCountriesList(String searchKey) {
        List<Country> found = new ArrayList<>();

        found = getDefaultCountriesList(MainActivity.RATES_DATA_FILENAME).stream()
                .filter(country -> country.getCharCode().toLowerCase()
                        .contains(searchKey.toLowerCase()) || country.getName().toLowerCase().contains(searchKey.toLowerCase()))
                .collect(Collectors.toList());

        return found;
    }

}
