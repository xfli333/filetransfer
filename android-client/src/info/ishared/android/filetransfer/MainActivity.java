package info.ishared.android.filetransfer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import info.ishared.android.filetransfer.util.NetworkUtils;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    private TextView mInfo;
    private EditText mServerIpEditText;
    private EditText mServerPort;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mInfo=(TextView)this.findViewById(R.id.info);
        mServerIpEditText=(EditText)this.findViewById(R.id.server_ip_input);
        mServerPort=(EditText)this.findViewById(R.id.server_port_input);
        //获取wifi服务

        mInfo.setText(this.getString(R.string.local_ip_info)+"\t"+ NetworkUtils.getLocalIpAddress(this));
        mServerPort.setText(""+AppConfig.PORT);
//        new FileTransferClient().run();
//        FileTransferServer.getInstance().run();
//        test();
    }
}
