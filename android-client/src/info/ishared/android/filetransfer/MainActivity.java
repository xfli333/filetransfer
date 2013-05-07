package info.ishared.android.filetransfer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import info.ishared.android.filetransfer.util.NetworkUtils;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView mInfo=(TextView)this.findViewById(R.id.info);
        //获取wifi服务

        mInfo.setText(AppConfig.SAVE_DIR +"\r\n"+ NetworkUtils.getLocalIpAddress(this)+"\r\n"+NetworkUtils.getMacAddress(this));
//        new FileTransferClient().run();
//        FileTransferServer.getInstance().run();
//        test();
    }
}
