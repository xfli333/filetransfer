package info.ishared.filetransfer;

import info.ishared.filetransfer.handler.FileClientHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-7
 * Time: PM3:00
 */
public class FileTransferClient {

    public void run(){
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory()
        {

            @Override
            public ChannelPipeline getPipeline() throws Exception
            {
                ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast("decoder", new HttpResponseDecoder());

                /*
                 * 不能添加这个，对传输文件 进行了大小的限制。。。。。
                 */
//                pipeline.addLast("aggregator", new HttpChunkAggregator(6048576));
                pipeline.addLast("encoder", new HttpRequestEncoder());
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                pipeline.addLast("handler", new FileClientHandler());

                return pipeline;
            }

        });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(
                "192.168.0.102", 3333));

        /*
         * 这里为了保证connect连接，所以才进行了sleep
         * 当然也可以通过future的connect属性判断
         */
        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET, "mock_location.apk");
        future.getChannel().write(request);

        // Wait until the connection is closed or the connection attempt fails.
        future.getChannel().getCloseFuture().awaitUninterruptibly();

        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();
    }
}
