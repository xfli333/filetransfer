package info.ishared.android.filetransfer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import info.ishared.android.filetransfer.controller.MainController;
import info.ishared.android.filetransfer.util.NetworkUtils;
import info.ishared.android.filetransfer.util.ToastUtils;
import info.ishared.android.filetransfer.util.ViewUtils;

public class MainActivity extends Activity implements View.OnClickListener {

    public static final int CONNECT_SUCCESS = 0;
    public static final int CONNECT_FAILED = 1;
    public static final int CONNECT_DISCONNECTED = 2;
    public static final int ACCEPT_SEND = 3;
    public static final int RECEIVE_FILE = 4;


    /**
     * Called when the activity is first created.
     */
    private TextView mInfo;
    private EditText mServerIpEditText;
    private EditText mServerPort;

    private RadioButton mClientBtn;
    private RadioButton mServerBtn;
    private Button mConnectBtn;
    private Button mStartServerBtn;
    private Button mSendBtn;

    private ProgressBar mLoadingView;
    private Handler mHandler;
    private MainController mController;

    private String port;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        port = NetworkUtils.getLocalIpAddress(this);
        initUI();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECT_SUCCESS:
                        ViewUtils.hideView(mLoadingView);
                        ToastUtils.showMessage(MainActivity.this, "connect success");
                        break;
                    case CONNECT_FAILED:
                        ViewUtils.hideView(mLoadingView);
                        mConnectBtn.setEnabled(true);
                        ToastUtils.showMessage(MainActivity.this, "connect failed");
                        break;
                    case CONNECT_DISCONNECTED:
                        ViewUtils.hideView(mLoadingView);
                        mConnectBtn.setEnabled(true);
                        ToastUtils.showMessage(MainActivity.this, "connect closed");
                        break;
                    case ACCEPT_SEND:
                        try {
                            mController.sendFile("1.jpg");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RECEIVE_FILE:
                        Bundle bundle = msg.getData();
                        String fileName = bundle.getString("fileName");
                        mController.requestFile(fileName);
                        break;
                }
            }
        };

        mController = new MainController(this, mHandler);

        mInfo.setText(this.getString(R.string.local_ip_info) + "\t" + NetworkUtils.getLocalIpAddress(this));
        mServerPort.setText("" + AppConfig.PORT);
        mServerIpEditText.setText("172.17.22.234");
//        new FileTransferClient().run();
//        FileTransferServer.getInstance().run();
//        test();
    }

    private void initUI() {


        mInfo = (TextView) this.findViewById(R.id.info);
        mServerIpEditText = (EditText) this.findViewById(R.id.server_ip_input);
        mServerPort = (EditText) this.findViewById(R.id.server_port_input);

        mClientBtn = (RadioButton) this.findViewById(R.id.client_radio_btn);
        mClientBtn.setOnClickListener(this);
        mServerBtn = (RadioButton) this.findViewById(R.id.server_radio_btn);
        mServerBtn.setOnClickListener(this);
        mConnectBtn = (Button) this.findViewById(R.id.client_connect_btn);
        mConnectBtn.setOnClickListener(this);
        mStartServerBtn = (Button) this.findViewById(R.id.start_server_btn);
        mStartServerBtn.setOnClickListener(this);
        mSendBtn = (Button) this.findViewById(R.id.send_file_btn);
        mSendBtn.setOnClickListener(this);

        mLoadingView = (ProgressBar) this.findViewById(R.id.pb_loading);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.client_radio_btn:
                mServerIpEditText.setEnabled(true);
                mServerIpEditText.setText("");
                ViewUtils.hideView(mStartServerBtn);
                ViewUtils.showView(mConnectBtn);
                break;
            case R.id.server_radio_btn:
                mServerIpEditText.setEnabled(false);
                mServerIpEditText.setText(port + "");
                ViewUtils.hideView(mConnectBtn);
                ViewUtils.showView(mStartServerBtn);
                break;
            case R.id.client_connect_btn:
                mConnectBtn.setEnabled(false);
                ViewUtils.showView(mLoadingView);
                mController.connectServer("172.17.22.234", 3333);
                break;
            case R.id.send_file_btn:
                mController.sendMessage("x-firewall.apk");
                break;
        }
    }


    @Override
    public void onBackPressed() {
        mController.closeConnect();
        this.finish();
    }
}
