package info.ishared.android.filetransfer.handler;

import android.util.Log;
import info.ishared.android.filetransfer.AppConfig;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-7
 * Time: AM10:31
 */
public class HelloWorldClientHandler extends SimpleChannelHandler {

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        String message = (String) e.getMessage();
        Log.d(AppConfig.TAG,message);
        e.getChannel().close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Log.d(AppConfig.TAG,"Unexpected exception from downstream."
                + e.getCause());
        e.getChannel().close();
    }
}