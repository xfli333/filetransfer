package info.ishared.filetransfer.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-15
 * Time: PM2:26
 */
public class ClientBean {
    private String clientIP;
    private Date connectTie;

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public Date getConnectTie() {
        return connectTie;
    }

    public void setConnectTie(Date connectTie) {
        this.connectTie = connectTie;
    }
}
