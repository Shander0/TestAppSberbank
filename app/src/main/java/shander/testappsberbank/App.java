package shander.testappsberbank;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import shander.testappsberbank.db.DBHelper;
import shander.testappsberbank.services.SyncService;

public class App extends Application {

    private DBHelper helper;

    public static App from(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new DBHelper(this);
        Intent startService = new Intent(this, SyncService.class);
        startService(startService);
    }

    public DBHelper getBaseHelper() {
        return helper;
    }

}
