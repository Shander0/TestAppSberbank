package shander.testappsberbank.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import shander.testappsberbank.App;
import shander.testappsberbank.R;
import shander.testappsberbank.db.DBHelper;
import shander.testappsberbank.processes.AbstractProcess;
import shander.testappsberbank.processes.DownloadAndParseCurrencies;
import shander.testappsberbank.ui.MainActivity;

public class SyncService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private Log log;
    private AbstractProcess.Logger logger;
    private StateService state;
    private H handler;
    private NotificationManager notificationManager;

    public class LocalBinder extends Binder {
        public SyncService getService () {
            return SyncService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log = new Log();
        handler = new H();
        state = StateService.NULL_PROCESS;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        logger = new AbstractProcess.Logger() {

            @Override
            public void log(CharSequence msg) {
                handler.obtainMessage(H.LOG, msg).sendToTarget();
            }

            @Override
            public void postProgress(int progress) {
                handler.obtainMessage(H.PROGRESS, progress, 0).sendToTarget();
            }

            @Override
            public void postProgressName(String name) {
                handler.obtainMessage(H.PROCESS_NAME, name).sendToTarget();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.setService(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.start_synchronization))
                .setSmallIcon(R.drawable.ic_refresh)
                .build();

        startForeground(1, notification);

        log.reset();

        DBHelper dbh = App.from(SyncService.this).getBaseHelper();
        dbh.refreshDB();

        state = StateService.PRE_PROGRESS;

        new Thread(new Runnable() {
            @Override
            public void run() {
                state = StateService.PROGRESS;

                DownloadAndParseCurrencies parseCurrencies = new DownloadAndParseCurrencies(logger, SyncService.this);
                parseCurrencies.start();

                Message message = handler.obtainMessage(H.FINISH);
                message.sendToTarget();
            }
        }).start();
        return START_STICKY;
    }

    public Log getLog() {
        return log;
    }

    private SynchronizationCallback callback;

    public void setCallback(SynchronizationCallback callback) {
        this.callback = callback;
    }

    public interface SynchronizationCallback extends AbstractProcess.Logger {
        void onSynchronizationFinish();
        void onSynchronizationError();
    }

    public StateService getState() {
        return state;
    }

    public enum StateService {
        PRE_PROGRESS, PROGRESS, PROGRESS_FINISH, NULL_PROCESS
    }

    public void updateNotification(int progress) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_refresh)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.completed_sync) + progress + " %" + "\n"
                        + log.getName());

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();

        if (progress == 100) {
            notification.flags = Notification.FLAG_AUTO_CANCEL;
        }

        notificationManager.notify(1, notification);
    }

    private static class H extends Handler {

        public static final int FINISH = 1;
        public static final int LOG = 2;
        public static final int PROGRESS =3;
        public static final int PROCESS_NAME = 4;

        private SyncService service;

        private int lastProgress = 0;

        public void setService(SyncService service) {
            this.service = service;
        }

        @Override
        public void handleMessage(Message msg) {
            if (service != null) {
                switch (msg.what) {
                    case FINISH:
                        service.state = StateService.PROGRESS_FINISH;
                        service.stopForeground(true);
                        service.stopSelf();
                        if (service.callback != null) {
                            service.callback.onSynchronizationFinish();
                        }
                        break;
                    case LOG:
                        if (service.callback != null) {
                            CharSequence str = (CharSequence)msg.obj;
                            service.log.stringBuilder.append(str);
                            if (service.callback != null) {
                                service.callback.log(str);
                            }
                        }
                        break;
                    case PROGRESS:
                        if (msg.arg1 != lastProgress) {
                            lastProgress = msg.arg1;
                            service.updateNotification(msg.arg1);
                            service.log.progress = msg.arg1;
                            if (service.callback != null) {
                                service.callback.postProgress(msg.arg1);
                            }
                        }
                        break;
                    case PROCESS_NAME:
                        String name = (String)msg.obj;
                        service.log.postProgressName(name);
                        if (service.callback != null) {
                            service.callback.postProgressName(name);
                        }
                        break;
                }
            }
        }

    }

    public static class Log implements AbstractProcess.Logger {

        private StringBuilder stringBuilder;
        private String name;
        private int progress;

        public Log() {
            reset();
        }

        public void reset() {
            name = null;
            progress = 0;
            stringBuilder = new StringBuilder();
        }

        @Override
        public void log(CharSequence msg) {
            stringBuilder.append(msg).append("\n");
        }

        @Override
        public void postProgress(int progress) {
            this.progress = progress;
        }

        @Override
        public void postProgressName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }

        public StringBuilder getStringBuilder() {
            return stringBuilder;
        }

        public String getName() {
            return name;
        }

        public int getProgress() {
            return progress;
        }
    }
}
