package info.ishared.android.filetransfer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import info.ishared.android.filetransfer.handler.HelloWorldClientHandler;
import info.ishared.android.filetransfer.util.NetworkUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView mInfo=(TextView)this.findViewById(R.id.info);
        mInfo.setText(AppConfig.save_dir+"\r\n"+ NetworkUtils.getIpAddress()+"\r\n"+NetworkUtils.getMacAddress(this));
//        new FileTransferClient().run();
//        test();
    }


}
