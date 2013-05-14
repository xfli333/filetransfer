package info.ishared.android.filetransfer.controller;

import android.os.Handler;
import android.os.Message;
import info.ishared.android.filetransfer.AppConfig;
import info.ishared.android.filetransfer.MainActivity;
import info.ishared.android.filetransfer.handler.FileClientHandler;
import info.ishared.android.filetransfer.util.LogUtils;
import info.ishared.android.filetransfer.util.MessageUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-9
 * Time: PM2:01
 */
public class MainController {
    private MainActivity mainActivity;
    private Handler handler;
    ClientBootstrap bootstrap;
    ChannelFuture future;
    Channel channel;

    public MainController(MainActivity mainActivity, Handler handler) {
        this.mainActivity = mainActivity;
        this.handler = handler;
    }

    public void connectServer(String ip, int port) {
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast("decoder", new HttpResponseDecoder());

                /*
                 * 不能添加这个，对传输文件 进行了大小的限制。。。。。
                 */
//                pipeline.addLast("aggregator", new HttpChunkAggregator(6048576));
                pipeline.addLast("encoder", new HttpRequestEncoder());
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                pipeline.addLast("handler", new FileClientHandler(handler));

                return pipeline;
            }
        });
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);

        future = bootstrap.connect(new InetSocketAddress(ip, port));
        channel = future.getChannel();

    }

    public void requestFile(String fileName){
        MessageUtils.requestFile(fileName,channel);
    }

    public void sendFile(String filename) throws Exception {
        File file = new File(AppConfig.SD_PATH+"bluetooth/IMG_20121001_135254.jpg");
        MessageUtils.sendFile(file,channel);
    }

    public void sendMessage(String message){
        MessageUtils.sendMessage(message,channel);
    }

    public void closeConnect() {
        if (bootstrap != null) {
            future.getChannel().close();
            bootstrap.releaseExternalResources();
        }
    }
}
