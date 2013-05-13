package info.ishared.filetransfer.util;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-13
 * Time: AM11:13
 */
public class MessageUtils {

    public static void sendMessage(String msg,Channel channel){
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setHeader("msg",msg);
        channel.write(response);
    }

    public static void sendFile(final String path,Channel channel) throws Exception {

        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            return;
        }
        if (!file.isFile()) {
            return;
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnfe) {
            return;
        }
        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

        /*
         * 由于是异步传输，所以不得已加入了一些属性，用来进行文件识别
         */
        response.addHeader("fileName", file.getName());

        setContentLength(response, fileLength);



        // Write the initial line and the header.
        channel.write(response);

        // Write the content.
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
                    System.out.printf("%s: %d / %d (+%d)%n", path, current,total, amount);
                }
            });
        }

//        // Decide whether to close the connection or not.
//        if (!isKeepAlive(request)) {
//            // Close the connection when the whole content is written out.
//            writeFuture.addListener(ChannelFutureListener.CLOSE);
//        }
    }

    private static String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + ".")
                || uri.contains("." + File.separator) || uri.startsWith(".")
                || uri.endsWith(".")) {
            return null;
        }

        // Convert to absolute path.
//        return System.getProperty("user.dir") + File.separator + uri;
        return "/Users/admin/temp/222/" + uri;
    }
}
