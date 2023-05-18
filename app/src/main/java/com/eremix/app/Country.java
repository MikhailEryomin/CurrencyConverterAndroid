package com.eremix.app;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class Country implements Serializable {
    private final String charCode;
    private String name;
    private final double value;
    private boolean isFavourite = false;
    private final String imageResourceLink;


    public Country(String charCode, String name, double value, String imageResourceLink) {
        this.charCode = charCode;
        this.imageResourceLink = imageResourceLink;
        this.name = name;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public String getCharCode() {
        return charCode;
    }

    public String getName() {
        return name;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getImageResourceLink() {
        return imageResourceLink;
    }

    public static Comparator<Country> countryComparator = new Comparator<Country>() {
        @Override
        public int compare(Country country1, Country country2) {
            // Проверка, является ли country1 избранным
            boolean isCountry1Favourite = country1.isFavourite();
            boolean isCountry2Favourite = country2.isFavourite();

            if (isCountry1Favourite && !isCountry2Favourite) {
                // Если только country1 является избранным, перемещаем его вверх
                return -1;
            } else if (!isCountry1Favourite && isCountry2Favourite) {
                // Если только country2 является избранным, перемещаем его вверх
                return 1;
            } else {
                // Если оба или ни один из элементов не являются избранными,
                // сохраняем текущий порядок элементов
                return 0;
            }
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Country country)) return false;
        return Double.compare(country.value, value) == 0 && charCode.equals(country.charCode) && name.equals(country.name) && isFavourite == country.isFavourite && imageResourceLink.equals(country.imageResourceLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(charCode, name, isFavourite, value, imageResourceLink);
    }
}



