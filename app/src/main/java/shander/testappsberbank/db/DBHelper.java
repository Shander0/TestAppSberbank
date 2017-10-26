package shander.testappsberbank.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL("DROP TABLE IF IT EXISTS " + Tables.CURRENCIES);

        onCreate(db);
    }

    public Cursor getCurrencyByName (String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String qString;

        qString = SingleCurrency.CHAR_CODE +
                " = " + "'" + name + "'";

        return db.query(Tables.CURRENCIES, SingleCurrency.ALL_CURRENCY_FIELDS, qString, null, null, null, null);
    }

    public static IConverter<Currency, ContentValues> CURRENCY_CONVERTER = new IConverter<Currency, ContentValues>() {
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
}
