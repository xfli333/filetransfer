package info.ishared.filetransfer;

import com.sun.deploy.panel.JreTableModel;
import info.ishared.filetransfer.model.ClientBean;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Seven
 * Date: 13-5-15
 * Time: PM2:11
 */
public class ServerConsole {
    private JTable connectListTable;
    private JLabel ipLabel;
    private JLabel ipAddress;
    private JButton runServerBtn;
    private JButton sendFileBtn;
    private JScrollPane jScrollPane;
    public JPanel mainPanel;

    Object[][] tableData =
            {
                    new Object[]{"李清照", 29, "女"},
                    new Object[]{"苏格拉底", 56, "男"},
                    new Object[]{"李白", 35, "男"},
                    new Object[]{"弄玉", 18, "女"},
                    new Object[]{"虎头", 2, "男"}
            };

    List<ClientBean> clientBeanList = new ArrayList<ClientBean>();
    //定义一维数据作为列标题
    Object[] columnTitle = {"姓名", "年龄"};


    public ServerConsole() {
        for (int i = 0; i < 10; i++) {
            ClientBean clientBean = new ClientBean();
            clientBean.setClientIP("11.22.12." + i);
            clientBean.setConnectTie(new Date());
            clientBeanList.add(clientBean);
        }
//    Comment this code to add table dynamically

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
                return 2;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Object getValueAt(int i, int i2) {
                System.out.println(i + ":" + i2);
                if (i2 == 0) {
                    return clientBeanList.get(i).getClientIP();
                }
                return clientBeanList.get(i).getConnectTie();
            }
        });

        runServerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("run... ");

                sendFileBtn.setEnabled(true);
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
}
