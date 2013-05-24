package info.ishared.filetransfer;

import info.ishared.filetransfer.model.ClientBean;
import info.ishared.filetransfer.model.MyMessage;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-15
 * Time: PM2:11
 */
public class ServerConsole implements Observer {
    private JTable connectListTable;
    private JLabel ipLabel;
    private JLabel ipAddress;
    private JButton runServerBtn;
    private JButton sendFileBtn;
    private JScrollPane jScrollPane;
    public JPanel mainPanel;


    public static List<ClientBean> clientBeanList = new ArrayList<ClientBean>();
    Object[] columnTitle = {"客户端IP", "链接时间"};


    public ServerConsole() {
        try {
            ipAddress.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

//
//        for (int i = 0; i < 10; i++) {
//            ClientBean clientBean = new ClientBean();
//            clientBean.setClientIP("11.22.12." + i);
//            clientBean.setConnectTime(new Date());
//            clientBeanList.add(clientBean);
//        }

        connectListTable.setModel(new AbstractTableModel() {

            public String getColumnName(int col) {
                return columnTitle[col].toString();
            }

            @Override
            public int getRowCount() {
                return clientBeanList.size();  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getColumnCount() {
                return columnTitle.length;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Object getValueAt(int i, int i2) {
                if (i2 == 0) {
                    return clientBeanList.get(i).getClientIP();
                }
                return clientBeanList.get(i).getConnectTime();
            }
        });

        runServerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("run... ");
                sendFileBtn.setEnabled(true);
                runServer();
                runServerBtn.setEnabled(false);
            }
        });

        sendFileBtn.setEnabled(false);
        sendFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("send ");

                clientBeanList.remove(1);
                clientBeanList.remove(3);
                connectListTable.updateUI();
            }
        });

    }

    private void createUIComponents() {

    }

    public void runServer(){
        FileTransferServer fileTransferServer =  FileTransferServer.getInstance();
        fileTransferServer.setObserver(this);
        fileTransferServer.run();
    }



    @Override
    public void update(Observable observable, Object o) {
        MyMessage message = (MyMessage) o;
        if ("CONNECT".equals(message.getMessageType())) {
            String remoteIp = message.getData().toString();
            ClientBean clientBean = new ClientBean();
            clientBean.setClientIP(remoteIp);
            clientBean.setConnectTime(new Date());
            clientBeanList.add(clientBean);
            connectListTable.updateUI();
        }else if("DISCONNECTED".equals(message.getMessageType())){
            String remoteIp = message.getData().toString();
            ClientBean clientBean = new ClientBean();
            clientBean.setClientIP(remoteIp);
            clientBeanList.remove(clientBean);
            connectListTable.updateUI();

        }

    }
}
