package info.ishared.android.filetransfer.handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import info.ishared.android.filetransfer.AppConfig;
import info.ishared.android.filetransfer.MainActivity;
import info.ishared.android.filetransfer.util.LogUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.File;
import java.io.FileOutputStream;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-7
 * Time: PM3:02
 */
public class FileClientHandler extends SimpleChannelUpstreamHandler {
    private volatile boolean readingChunks;
    private File downloadFile;
    private FileOutputStream fOutputStream = null;
    private Handler handler;

    public FileClientHandler() {
    }


    public FileClientHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        if (e.getMessage() instanceof HttpResponse) {
            DefaultHttpResponse httpResponse = (DefaultHttpResponse) e.getMessage();
            String msg=httpResponse.getHeader("msg");
            if("OK".equals(msg)){
                Message message = new Message();
                message.what = MainActivity.ACCEPT_SEND;
                handler.sendMessage(message);
            }else if("SEND_FILE".equals(msg)){
                LogUtils.log("=====ddddd========");
                String fileName = httpResponse.getHeader("fileName");
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("fileName",fileName);
                message.what = MainActivity.RECEIVE_FILE;
                message.setData(bundle);
                handler.sendMessage(message);
            }else{
                LogUtils.log("=============");
                String fileName = httpResponse.getHeader("fileName");
                downloadFile = new File(AppConfig.SAVE_DIR + fileName);
                readingChunks = httpResponse.isChunked();
            }
        } else {
            HttpChunk httpChunk = (HttpChunk) e.getMessage();
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
            if (!readingChunks) {
                fOutputStream.close();
//                e.getChannel().close();
            }
        }

    }


    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        LogUtils.log(e.getChannel().toString());
        Message msg = new Message();
        msg.what = MainActivity.CONNECT_SUCCESS;
        handler.sendMessage(msg);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        Message msg = new Message();
        msg.what = MainActivity.CONNECT_DISCONNECTED;
        handler.sendMessage(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if (e.getCause().getLocalizedMessage().startsWith("connection timed out")) {
            Message msg = new Message();
            msg.what = MainActivity.CONNECT_FAILED;
            handler.sendMessage(msg);
            LogUtils.log(e.getCause().getLocalizedMessage());
        }else{
            LogUtils.log(e.getCause().getLocalizedMessage());
        }
    }
}
