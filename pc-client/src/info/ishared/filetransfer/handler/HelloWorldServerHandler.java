package info.ishared.filetransfer.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-7
 * Time: AM10:30
 */
public class HelloWorldServerHandler extends SimpleChannelUpstreamHandler {

    private volatile boolean readingChunks;
    private File downloadFile;
    private FileOutputStream fOutputStream = null;

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {

        super.channelConnected(ctx, e);

    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//        super.messageReceived(ctx, e);    //To change body of overridden methods use File | Settings | File Templates.
        System.out.println(e.getMessage());

        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) e.getMessage();
            downloadFile = new File("/Users/admin/temp/111/" + request.getUri());
            fOutputStream = new FileOutputStream(downloadFile);
        } else {
            readingChunks = true;
            HttpChunk httpChunk = (HttpChunk) e.getMessage();

            if (!httpChunk.isLast()) {
                ChannelBuffer buffer = httpChunk.getContent();
                while (buffer.readable()) {
                    byte[] dst = new byte[buffer.readableBytes()];
                    buffer.readBytes(dst);
                    fOutputStream.write(dst);
                }
            } else {
                readingChunks = false;

            }
            fOutputStream.flush();
            if (!readingChunks) {
                System.out.println("finish!");
                fOutputStream.close();
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        System.out.println("Unexpected exception from downstream."
                + e.getCause());
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
