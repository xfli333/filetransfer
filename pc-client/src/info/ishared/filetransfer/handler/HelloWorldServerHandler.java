package info.ishared.filetransfer.handler;

import info.ishared.filetransfer.model.MyMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Observable;
import java.util.Observer;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

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

    private Observer observer;
    private Observable observable = new Observable();

    public HelloWorldServerHandler(Observer observer) {
        this.observer = observer;
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {


        super.channelConnected(ctx, e);

        observer.update(observable,new MyMessage("CONNECT",e.getChannel().getRemoteAddress().toString().substring(1)));
//
//        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
//        response.setHeader("msg","SEND_FILE");
//        response.setHeader("fileName","/Users/admin/temp/222/tt.jpg");
//        response.setChunked(false);
//        e.getChannel().write(response);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        observer.update(observable,new MyMessage("DISCONNECTED",e.getChannel().getRemoteAddress().toString().substring(1)));
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws IOException {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) e.getMessage();
            if(HttpMethod.GET.equals(request.getMethod()) && "SEND_MSG".equals(request.getUri())){
                handleStringMessage(request,e);
            }else{
                String type=request.getHeader("type");
                if("sendFile".equals(type)){
                    handleUploadFileInfoEvent(request);
                }else if("requestFile".equals(type)){
                    System.out.println("rrrrrrrqqqqqqq");
                    handleRequestFileEvent(request,e.getChannel());
                }
            }
        } else if(e.getMessage() instanceof HttpChunk){
            handleUploadFileEvent(e);
        }
    }


    private void handleRequestFileEvent(HttpRequest request,Channel channel) throws IOException {

//        final String path ="/Users/admin/temp/222/"+ request.getUri();
        final String path =request.getUri();
        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
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

        response.setHeader("fileName", file.getName());

        setContentLength(response, fileLength);

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

        if (!isKeepAlive(request)) {
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleUploadFileEvent(MessageEvent e) throws IOException {
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

    private void handleUploadFileInfoEvent(HttpRequest request){
        downloadFile = new File("/Users/admin/temp/111/" + request.getUri());
        try {
            fOutputStream = new FileOutputStream(downloadFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void handleStringMessage(HttpRequest request,MessageEvent e){
        System.out.println( request.getHeader("msg"));
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setHeader("msg","OK");
        e.getChannel().write(response);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        System.out.println("Unexpected exception from downstream."+ e.getCause());
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
