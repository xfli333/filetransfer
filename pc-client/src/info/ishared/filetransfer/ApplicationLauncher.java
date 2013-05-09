package info.ishared.filetransfer;

import info.ishared.filetransfer.handler.HelloWorldClientHandler;
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

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-7
 * Time: AM10:26
 */
public class ApplicationLauncher {
    public static void main(String[] args) {
        FileTransferServer.getInstance().run();
//        new FileTransferClient().run();
    }


}
