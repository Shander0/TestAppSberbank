package shander.testappsberbank.processes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.simpleframework.xml.core.Persister;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import shander.testappsberbank.App;
import shander.testappsberbank.R;
import shander.testappsberbank.db.DBHelper;
import shander.testappsberbank.entities.CurrenciesList;
import shander.testappsberbank.entities.Currency;
import shander.testappsberbank.interfaces.IContract;
import shander.testappsberbank.utils.StorageHelper;

public class DownloadAndParseCurrencies extends AbstractProcess implements IContract{

    private Context context;

    public DownloadAndParseCurrencies(Logger logger, Context context) {
        super(logger);
        this.context = context;
    }

    @Override
    public void start() {
        postProgressName(context.getString(R.string.currencies_load));

        OutputStream output = null;
        InputStream input = null;

        log(context.getString(R.string.load_start));

        try {
            URL url = new URL(Settings.BASE_URL + Settings.DAILY + Settings.SRCIPTS);
            URLConnection connection = url.openConnection();
            connection.connect();

            long lenghtOfFile = connection.getContentLength();
            long totalCount = 0;
            input = new BufferedInputStream(connection.getInputStream());

            output = new FileOutputStream(StorageHelper.getCacheDir(context).toString() + "/currencies.res");

            byte data[] = new byte[1024];

            int count;
            while ((count = input.read(data)) != -1) {
                totalCount += count;
                output.write(data, 0, count);
                postProgress(calculateProgress(totalCount, lenghtOfFile, 0, 50));
            }

            output.flush();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            log(e.getClass().getSimpleName() + " " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        SQLiteDatabase db = null;
        BufferedReader br = null;
        Persister serializer = new Persister();
        try {
            File file = new File(StorageHelper.getCacheDir(context), "currencies.res");

            file.createNewFile();

            InputStream inputStream = new FileInputStream(file);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "cp1251");

            br = new BufferedReader(inputStreamReader);

            db = App.from(context).getBaseHelper().getWritableDatabase();

            db.delete(Tables.CURRENCIES, null, null);

            String line;
            long length = file.length();
            long current = 0;
            postProgressName(context.getString(R.string.record_to_database));

            while ((line = br.readLine()) != null) {
                current += line.length();
                postProgress(calculateProgress(current, length, 50, 100));

                CurrenciesList list = serializer.read(CurrenciesList.class, br);

                for (Currency currency : list.getCurrencies()) {

                    ContentValues cv = DBHelper.CURRENCY_CONVERTER.convert(currency);
                    db.insert(Tables.CURRENCIES, null, cv);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null){
                db.close();
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
