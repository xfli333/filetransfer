package info.ishared.android.filetransfer.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-13
 * Time: AM11:13
 */
public class MessageUtils {

    public static void sendMessage(String msg,Channel channel){
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,"SEND_MSG");
        request.setHeader("msg",msg);
        channel.write(request);
    }

    public static void sendFile(File file,Channel channel) throws Exception {
        LogUtils.log( channel.toString());
        final String filename=file.getName();
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, filename);
        request.setHeader("type","sendFile");

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnfe) {
            return;
        }
        long fileLength = raf.length();

        request.addHeader("fileName", filename);

        setContentLength(request, fileLength);

        channel.write(request);

        ChannelFuture writeFuture;
        if (channel.getPipeline().get(SslHandler.class) != null) {
            // Cannot use zero-copy with HTTPS.
            writeFuture = channel.write(new ChunkedFile(raf, 0, fileLength, 8192));
        } else {
            // No encryption - use zero-copy.
            final FileRegion region = new DefaultFileRegion(raf.getChannel(),
                    0, fileLength);
            writeFuture = channel.write(region);
            writeFuture.addListener(new ChannelFutureProgressListener() {
                public void operationComplete(ChannelFuture future) {
                    region.releaseExternalResources();
                }

                public void operationProgressed(ChannelFuture future,long amount, long current, long total) {
                    LogUtils.log(filename+","+ current+","+total+","+amount);
                }
            });
        }

        if (!isKeepAlive(request)) {
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    public static void requestFile(String fileName,Channel channel){
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET, fileName);
        request.setHeader("type","requestFile");
        channel.write(request);
    }
}
