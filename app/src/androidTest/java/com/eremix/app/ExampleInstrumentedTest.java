package com.eremix.app;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    //некоторые тесты походу придется делать при запуске приложение (для ссылки на activity)
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    //Проверка парсинга XML файла и записанных в файл данных
    @Test
    public void checkParsing() {
        ExchangeRatesModel exchangeRatesModel = new ExchangeRatesModel(null, null);
        XMLParser parser = new XMLParser(exchangeRatesModel);
        String content = exchangeRatesModel.getRequest("13/05/2023");
        parser.parseXMLandSaveData(content, context, MainActivity.RATES_DATA_FILENAME_TEST);

        ChooseCountryModel model = new ChooseCountryModel(context, null);
        List<String> flagLinks = model.getFlagLinksList();
        List<Country> actual = model.getDefaultCountriesList(MainActivity.RATES_DATA_FILENAME_TEST).subList(0,4);
        List<Country> expected = List.of(
                new Country("RUB", "Российский рубль", 1.0, flagLinks.get(0)),
                new Country("AUD", "Австралийский  доллар", 51.6495, flagLinks.get(1)),
                new Country("AZN", "Азербайджанский  манат", 45.4142, flagLinks.get(2)),
                new Country("GBP", "Фунт  стерлингов  Соединенного  королевства", 96.6827, flagLinks.get(3)));
        assertEquals(expected, actual);
    }

    @Test
    public void getConvertCheck() {
        ActivityScenario<MainActivity> activityScenario = activityScenarioRule.getScenario();
        activityScenario.onActivity(activity -> {

            assertNotNull(activity);

        });
    }
}