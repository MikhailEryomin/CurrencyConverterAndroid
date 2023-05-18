package com.eremix.app;

import java.io.Serializable;

public class HistoryUnit implements Serializable {

    private final Country fromCountry;
    private final Country toCountry;
    private double fromValue = 0;
    private double toValue = 0;

    public HistoryUnit(Country fromCountry, Country toCountry, double fromValue, double toValue) {
        this.fromCountry = fromCountry;
        this.toCountry = toCountry;
        this.fromValue = fromValue;
        this.toValue = toValue;
    }

    public Country getFromCountry() {
        return fromCountry;
    }

    public Country getToCountry() {
        return toCountry;
    }

    public double getFromValue() {
        return fromValue;
    }

    public double getToValue() {
        return toValue;
    }
}
