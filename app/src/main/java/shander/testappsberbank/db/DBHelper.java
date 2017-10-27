package shander.testappsberbank.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.List;

import shander.testappsberbank.entities.CurrenciesList;
import shander.testappsberbank.entities.Currency;
import shander.testappsberbank.interfaces.IContract;
import shander.testappsberbank.utils.IConverter;

public class DBHelper extends SQLiteOpenHelper implements IContract {

    public DBHelper(Context context) {
        super(context, Database.SBER, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CURRENCIES_TABLE = "create table "
                + Tables.CURRENCIES + "(" + SingleCurrency.ID + " integer primary key autoincrement not null, "
                + SingleCurrency.VALUTE_ID + " text, " + SingleCurrency.NUM_CODE + " integer, "
                + SingleCurrency.CHAR_CODE + " text, " + SingleCurrency.NAME + " text, "
                + SingleCurrency.NOMINAL + " float, " + SingleCurrency.VALUE + " float" + ");";

        sqLiteDatabase.execSQL(CREATE_CURRENCIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i != i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF IT EXISTS " + Tables.CURRENCIES);

            onCreate(sqLiteDatabase);
        }
    }

    public void refreshDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CURRENCIES);

        onCreate(db);
    }

    public Cursor getCurrencies () {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.query(Tables.CURRENCIES, SingleCurrency.ALL_CURRENCY_FIELDS, null, null, null, null, null);
    }

    public int getEntriesCount() {
        SQLiteDatabase db = this.getWritableDatabase();

        return (int)DatabaseUtils.queryNumEntries(db, Tables.CURRENCIES);
    }

    public static IConverter<Currency, ContentValues> CURR_TO_CONTENT_CONVERTER = new IConverter<Currency, ContentValues>() {
        @Override
        public ContentValues convert(Currency src) {

            ContentValues cv = new ContentValues();

            cv.put(SingleCurrency.VALUTE_ID, src.getId());
            cv.put(SingleCurrency.NUM_CODE, src.getNumCode());
            cv.put(SingleCurrency.CHAR_CODE, src.getCharCode());
            cv.put(SingleCurrency.NAME, src.getName());
            cv.put(SingleCurrency.NOMINAL, src.getNominal());
            cv.put(SingleCurrency.VALUE, src.getValue());
            return cv;
        }
    };

    public static IConverter<Cursor, HashMap<Integer, Currency>> CURSOR_TO_CURRLIST_CONVERTER = new IConverter<Cursor, HashMap<Integer, Currency>>() {
        @Override
        public HashMap<Integer, Currency> convert(Cursor src) {
            HashMap<Integer, Currency> map = new HashMap<>();

            for (int i = 0; i < src.getCount(); i++) {
                src.moveToPosition(i);
                Currency c=  new Currency();
                c.setId(src.getString(src.getColumnIndex(SingleCurrency.VALUTE_ID)));
                c.setName(src.getString(src.getColumnIndex(SingleCurrency.NAME)));
                c.setCharCode(src.getString(src.getColumnIndex(SingleCurrency.CHAR_CODE)));
                c.setNominal(src.getFloat(src.getColumnIndex(SingleCurrency.NOMINAL)));
                c.setNumCode(src.getInt(src.getColumnIndex(SingleCurrency.NUM_CODE)));
                c.setValue(src.getFloat(src.getColumnIndex(SingleCurrency.VALUE)));

                map.put(c.getNumCode(), c);
            }
            return map;
        }
    };
}
