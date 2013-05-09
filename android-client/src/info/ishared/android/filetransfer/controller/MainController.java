package info.ishared.android.filetransfer.controller;

import android.os.Handler;
import android.os.Message;
import info.ishared.android.filetransfer.MainActivity;
import info.ishared.android.filetransfer.handler.FileClientHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

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

    public MainController(MainActivity mainActivity,Handler handler) {
        this.mainActivity = mainActivity;
        this.handler = handler;
    }

    public void connectServer(String ip,int port){
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory(){

            @Override
            public ChannelPipeline getPipeline() throws Exception{
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

    public void test(){
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET, "o");
        request.setHeader("ooo","kkk");
        future.getChannel().write(request);

    }

    public void closeConnect(){
        future.getChannel().close();
        bootstrap.releaseExternalResources();
    }
}
