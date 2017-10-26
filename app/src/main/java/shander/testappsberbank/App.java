package shander.testappsberbank;

import android.app.Application;
import android.content.Context;

import shander.testappsberbank.db.DBHelper;

public class App extends Application {

    private DBHelper helper;

    public static App from(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new DBHelper(this);

    }

    public DBHelper getBaseHelper() {
        return helper;
    }

}
