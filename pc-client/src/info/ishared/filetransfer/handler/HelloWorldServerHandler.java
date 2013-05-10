package info.ishared.filetransfer.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
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

      if(e.getMessage() instanceof  HttpRequest){
          HttpRequest request = (HttpRequest) e.getMessage();
          downloadFile = new File("/Users/admin/temp/111/"+ "1.jpg");
      }else{
        DefaultHttpChunk httpChunk = (DefaultHttpChunk) e.getMessage();
        if (!httpChunk.isLast()) {
            ChannelBuffer buffer = httpChunk.getContent();
            if (fOutputStream == null) {
                fOutputStream = new FileOutputStream(downloadFile);
            }
            while (buffer.readable()) {
                byte[] dst = new byte[buffer.readableBytes()];
                buffer.readBytes(dst);
                fOutputStream.write(dst);
            }
        } else {
            readingChunks = false;
        }
        fOutputStream.flush();
      }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        System.out.println("Unexpected exception from downstream."
                + e.getCause());
        e.getChannel().close();
    }
}
