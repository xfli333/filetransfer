package info.ishared.filetransfer.util;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelStateEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-24
 * Time: PM5:00
 */
public class StringUtils {
    public static String getRemoteAddressByChannelStateEvent(Channel channel){
        return channel.getRemoteAddress().toString().substring(1);
    }
}
