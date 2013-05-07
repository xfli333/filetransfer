package info.ishared.android.filetransfer;

import android.os.Environment;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-7
 * Time: AM10:46
 */
public class AppConfig {
    public static final String TAG = "FILE TRANSFER";
    public static final String save_dir= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/";
}
