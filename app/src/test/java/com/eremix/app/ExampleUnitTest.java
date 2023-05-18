package com.eremix.app;


import static org.junit.Assert.*;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //Проверка на то, что данные успешно получаются запросами
    @Test
    public void checkForActualData() {
        ExchangeRatesModel exchangeRatesModel = new ExchangeRatesModel(null, null);
        String expected = "\"13.05.2023\"";

        String actual = exchangeRatesModel.getRequest("13/05/2023");
        int id = actual.indexOf(expected);
        assertNotEquals(-1, id);

        expected = "\"15.05.2023\"";
        id = actual.indexOf(expected);
        assertEquals(-1, id);

        actual = exchangeRatesModel.getRequest("No a date");
        assertNull(actual);
    }

    //Проверка конвертаций (Это чистая логика без подключения сохраненных данных...)
    @Test
    public void convertCheck() {
        ExchangeRatesModel model = new ExchangeRatesModel(null, null);
        //USD -> AMD
        double fromCountryValue = 77.2041;
        double toCountryValue = 0.1998;
        double value = 0.0;
        double expected = 0.0;
        double actual = model.convert(value, fromCountryValue, toCountryValue);
        assertEquals(expected, actual, 10e-3);

        value = 44.23;
        actual = model.convert(value, fromCountryValue, toCountryValue);
        expected = 17090.77;
        assertEquals(expected, actual, 10e-3);

        //null -> null
        fromCountryValue = 0;
        toCountryValue = 0;
        value = 99.99;
        actual = model.convert(value, fromCountryValue, toCountryValue);
        assertEquals(0, actual, 10e-3);

        //EUR -> GBP
        fromCountryValue = 84.2500;
        toCountryValue = 96.6827;
        value = 50;
        expected = 43.57;
        actual = model.convert(value, fromCountryValue, toCountryValue);
        assertEquals(expected, actual, 10e-3);

        value = 99.99;
        expected = 87.13;
        actual = model.convert(value, fromCountryValue, toCountryValue);
        assertEquals(expected, actual, 10e-3);
    }

    @Test
    public void historyLogicCheck() {

    }

    @Test
    public void chooseCountryLogicCheck() {

    }

    @Test
    public void settingsCheck() {

    }

}