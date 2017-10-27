package shander.testappsberbank.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import shander.testappsberbank.App;
import shander.testappsberbank.R;
import shander.testappsberbank.db.DBHelper;
import shander.testappsberbank.entities.Currency;
import shander.testappsberbank.services.SyncService;
import shander.testappsberbank.utils.ServiceManager;

public class MainActivity extends AppCompatActivity implements ServiceConnection, SyncService.SynchronizationCallback{

    private DBHelper dbHelper;
    private Map<Integer, Currency> currencyMap = new HashMap<>();
    private SyncService mService;
    public static final int PERMISSIONS_REQUEST = 111;
    private TextView tvResult;
    private Spinner spSelectFirst;
    private Spinner spSelectSecond;
    private EditText etValue;
    private RelativeLayout progressLay;
    private RelativeLayout workLay;
    private ArrayAdapter<String> adapter;
    private Currency first;
    private Currency second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST);
        }

        dbHelper = App.from(this).getBaseHelper();
        ServiceManager sm = new ServiceManager(this);
        if (!sm.isNetworkAvailable()) {
            if (dbHelper.getEntriesCount() == 0) {
                Intent intent = new Intent(MainActivity.this, NoInternetActivity.class);
                startActivity(intent);
                finish();
            } else {
                currencyMap = DBHelper.CURSOR_TO_CURRLIST_CONVERTER.convert(dbHelper.getCurrencies());
            }
        }
        tvResult = (TextView) findViewById(R.id.tv_result);
        spSelectFirst = (Spinner) findViewById(R.id.spinner_first);
        spSelectSecond = (Spinner) findViewById(R.id.spinner_second);
        etValue = (EditText) findViewById(R.id.editText);
        progressLay = (RelativeLayout) findViewById(R.id.progress_lay);
        progressLay.setVisibility(View.VISIBLE);
        workLay = (RelativeLayout) findViewById(R.id.work_lay);
        workLay.setVisibility(View.GONE);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spSelectFirst.setAdapter(adapter);
        spSelectSecond.setAdapter(adapter);
        spSelectFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SyncService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mService != null){
            mService.stopSelf();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        SyncService.LocalBinder binder = (SyncService.LocalBinder) iBinder;
        mService = binder.getService();
        mService.setCallback(this);
        SyncService.Log log = mService.getLog();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if(mService != null){
            mService.setCallback(null);
        }
    }

    @Override
    public void log(CharSequence msg) {

    }

    @Override
    public void postProgress(int progress) {

    }

    @Override
    public void postProgressName(String name) {

    }

    @Override
    public void onSynchronizationFinish() {
        currencyMap = DBHelper.CURSOR_TO_CURRLIST_CONVERTER.convert(dbHelper.getCurrencies());
        for (Currency currency : currencyMap.values()) {
            adapter.add(currency.getCharCode() + " " + currency.getName());
        }
        spSelectFirst.setSelection(1);
        spSelectSecond.setSelection(2);
        workLay.setVisibility(View.VISIBLE);
        progressLay.setVisibility(View.GONE);
        if(mService != null){
            mService.setCallback(null);
            unbindService(this);
        }
    }

    @Override
    public void onSynchronizationError() {

    }
}
