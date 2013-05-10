package info.ishared.android.filetransfer.controller;

import android.os.Handler;
import android.os.Message;
import info.ishared.android.filetransfer.AppConfig;
import info.ishared.android.filetransfer.MainActivity;
import info.ishared.android.filetransfer.handler.FileClientHandler;
import info.ishared.android.filetransfer.util.LogUtils;
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

        future = bootstrap.connect(new InetSocketAddress(ip, port));

    }

    public void sendFile(final String filename) throws Exception {

//        File file = new File(AppConfig.SAVE_DIR+filename);
        File file = new File(AppConfig.SD_PATH+"bluetooth/IMG_20121001_135254.jpg");


        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.POST, filename);


        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnfe) {
            return;
        }
        long fileLength = raf.length();

        request.addHeader("fileName", filename);

        setContentLength(request, fileLength);

        Channel ch = future.getChannel();

        // Write the initial line and the header.
        ch.write(request);

        // Write the content.
        ChannelFuture writeFuture;
        if (ch.getPipeline().get(SslHandler.class) != null) {
            // Cannot use zero-copy with HTTPS.
            writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, 8192));
        } else {
            // No encryption - use zero-copy.
            final FileRegion region = new DefaultFileRegion(raf.getChannel(),
                    0, fileLength);
            writeFuture = ch.write(region);
            writeFuture.addListener(new ChannelFutureProgressListener() {
                public void operationComplete(ChannelFuture future) {
                    region.releaseExternalResources();
                }

                public void operationProgressed(ChannelFuture future,long amount, long current, long total) {
                    LogUtils.log(filename+","+ current+","+total+","+amount);
                }
            });
        }

        // Decide whether to close the connection or not.
//        if (true) {
            // Close the connection when the whole content is written out.
//            writeFuture.addListener(ChannelFutureListener.CLOSE);
//        }

//        request.setHeader("ooo", "kkk");
//        future.getChannel().write(response);

    }

    public void closeConnect() {
        if (bootstrap != null) {
            future.getChannel().close();
            bootstrap.releaseExternalResources();
        }
    }
}
