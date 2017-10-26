package shander.testappsberbank.interfaces;

public interface IContract {

    interface Settings {
        String BASE_URL = "http://www.cbr.ru/";
        String SRCIPTS = "scripts/";
        String DAILY = "XML_daily.asp";
    }

    interface Database {
        String SBER = "sber_db";
    }

    interface Tables {
        String CURRENCIES = "currencies";
    }

    interface SingleCurrency {
        String ID = "curr_id";
        String VALUTE_ID = "valute_id";
        String NUM_CODE = "num_code";
        String CHAR_CODE = "char_code";
        String NAME = "name";
        String NOMINAL = "nominal";
        String VALUE = "value";

        String[] ALL_CURRENCY_FIELDS = {ID, VALUTE_ID, NUM_CODE, CHAR_CODE, NOMINAL, NAME, VALUE};
    }

}
