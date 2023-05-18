package com.eremix.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.List;





public class CountryAdapter extends ArrayAdapter<Country> {

    private final List<Country> countryList;

    private final int layout;
    private final LayoutInflater inflater;

    private final ChooseCountryModel model;

    public CountryAdapter(@NonNull Context context, int resource, @NonNull List<Country> countryList, ChooseCountryModel model) {
        super(context, resource, countryList);
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.countryList = countryList;
        this.model = model;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Отображение элемента
        View view = inflater.inflate(this.layout, parent, false);

        ImageView countryFlagIV = view.findViewById(R.id.countryInfo_flag);
        TextView countryNameTextView = view.findViewById(R.id.countryInfo_name);
        TextView countryCharCodeTextView = view.findViewById(R.id.countryInfo_charCode);
        ImageButton favouriteButton = view.findViewById(R.id.favourite_button);

        Country country = countryList.get(position);

        String countryFlagLink = country.getImageResourceLink();
        if (countryFlagLink != null) Picasso.get().load(countryFlagLink).into(countryFlagIV);
        favouriteButton.setOnClickListener(e -> switchFavouriteStatus(country, favouriteButton));

        //ОТОБРАЖЕНИЕ СЕРДЕЧЕК (ОБНОВЛЕНИЕ)
        if (country.isFavourite()) {
            favouriteButton.setImageResource(R.drawable.heart_on);
        } else {
            favouriteButton.setImageResource(R.drawable.heart_off);
        }

        countryNameTextView.setText(country.getName());
        countryCharCodeTextView.setText(country.getCharCode());

        return view;
    }

    private void switchFavouriteStatus(Country country, ImageButton btn) {
        country.setFavourite(!country.isFavourite());
        if (country.isFavourite()) {
            btn.setImageResource(R.drawable.heart_on);
            model.addCountryToFavourites(country);
        } else {
            btn.setImageResource(R.drawable.heart_off);
            model.removeCountryFromFavourites(country);
        }
    }
}

