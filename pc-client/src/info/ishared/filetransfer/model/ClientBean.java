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
    private Date connectTime;

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientBean that = (ClientBean) o;

        if (clientIP != null ? !clientIP.equals(that.clientIP) : that.clientIP != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return clientIP != null ? clientIP.hashCode() : 0;
    }
}
