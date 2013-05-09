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


    /**
     * Called when the activity is first created.
     */
    private TextView mInfo;
    private EditText mServerIpEditText;
    private EditText mServerPort;

    private RadioButton mClientBtn;
    private RadioButton mServerBtn;
    private Button mConnectBtn;

    private ProgressBar mLoadingView;
    private View mClientView;
    private Handler mHandler;
    private MainController mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initUI();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECT_SUCCESS:
                        ViewUtils.hideView(mLoadingView);
                        ToastUtils.showMessage(MainActivity.this,"connect success");
                        mController.test();
                        break;
                    case CONNECT_FAILED:
                        ViewUtils.hideView(mLoadingView);
                        mConnectBtn.setEnabled(true);
                        ToastUtils.showMessage(MainActivity.this,"connect failed");
                        break;
                    case CONNECT_DISCONNECTED:
                        ViewUtils.hideView(mLoadingView);
                        mConnectBtn.setEnabled(true);
                        ToastUtils.showMessage(MainActivity.this,"connect closed");
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

        mLoadingView = (ProgressBar) this.findViewById(R.id.pb_loading);
        mClientView = this.findViewById(R.id.client_view);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.client_radio_btn:
                ViewUtils.showView(mClientView);
                break;
            case R.id.server_radio_btn:
                ViewUtils.hideView(mClientView);
                break;
            case R.id.client_connect_btn:
                mConnectBtn.setEnabled(false);
                ViewUtils.showView(mLoadingView);
                mController.connectServer("172.17.22.234",3333);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        mController.closeConnect();
        this.finish();
    }
}
