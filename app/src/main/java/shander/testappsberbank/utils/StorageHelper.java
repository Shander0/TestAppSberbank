package shander.testappsberbank.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import java.io.File;

public class StorageHelper {

    private static final String PROJECT_DIR = "TestAppSberbank";
    private static final String TMP = "tmp";

    public static File getCacheDir(Context context) {

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), PROJECT_DIR);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
        else {
            ContextWrapper cw = new ContextWrapper(context);
            File file = cw.getDir(PROJECT_DIR, Context.MODE_PRIVATE);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
    }

    public static File getOrderTmpDir(Context context) {
        File file = new File(getCacheDir(context), TMP);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }
}
