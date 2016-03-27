/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.core;

import com.autumn.core.log.TextConsole;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.mortbay.jetty.Server;

/**
 * <p>Title: 启动JETTY</p>
 *
 * <p>Description: JETTY启动界面</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class WebAppFrame extends JFrame implements ActionListener, Runnable {

    private static final long serialVersionUID = 20120711000000L;
    private JPanel contentPane;
    private BorderLayout borderLayout1 = new BorderLayout();
    private JLabel statusBar = new JLabel();
    private TextConsole textConsole1 = new TextConsole();
    private TitledBorder titledBorder1 = new TitledBorder("");
    public Server server = null;
    private PopupMenu popup = new PopupMenu();
    private MenuItem[] mItem = {new MenuItem("Exit")};
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Image iconimage;
    private Image grayimage;

    public WebAppFrame() {
        try {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
        }
    }

    /**
     * Component initialization.
     *
     * @throws java.lang.Exception
     */
    private void jbInit() throws Exception {
        inittray();
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(new Dimension(777, 570));
        setTitle("框架服务器");
        this.addWindowListener(new WebAppFrame_this_windowAdapter(this));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setText(" ");
        textConsole1.setBorder(titledBorder1);
        textConsole1.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
        textConsole1.setMaxLine(200);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        contentPane.add(textConsole1, java.awt.BorderLayout.CENTER);
    }

    /**
     * 添加托盘图标，菜单
     */
    private void inittray() {
        mItem[0].addActionListener(this);
        popup.add(mItem[0]);
        URL url = WebAppFrame.class.getResource("/resources/autumn.jpg");
        iconimage = Toolkit.getDefaultToolkit().getImage(url);
       // BufferedImage bi = ImageUtils.createImage(iconimage.getSource());
      //  GrayFilter gf = new GrayFilter();
        grayimage =iconimage;// gf.filter(bi, null);
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(grayimage, "框架服务器", popup);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
            }
            trayIcon.addActionListener(this);
            trayIcon.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getSource() == trayIcon && e.getButton() == MouseEvent.BUTTON1) {
                        setVisible(true);
                        setState(0);
                    }
                }
            });
        }
        this.setIconImage(grayimage);
        this.addWindowListener(new WindowAdapter() {
        });
    }

    /**
     * 点退出菜单事件
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mItem[0]) {
            if (server != null) {
                trayIcon.displayMessage("注意", "框架服务器正在关闭...", TrayIcon.MessageType.INFO);
                try {
                    JettyServer.stop(server);
                } catch (Exception ex) {
                }
            }
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            server = JettyServer.start(WebAppStart.port, WebAppStart.webapp);
            JettyListener jl = new JettyListener(server);
            JVMController.registry(jl);
            trayIcon.setImage(iconimage);
            this.setIconImage(iconimage);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 启动
     *
     * @param e
     */
    public void this_windowOpened(WindowEvent e) {
        if (WebAppStart.port == 0) {
            statusBar.setText("没有设置服务端口（请添加运行参数port=8080），使用默认端口8080");
        } else {
            statusBar.setText("服务端口:" + WebAppStart.port);
        }
    }

    public void this_windowIconified(WindowEvent e) {
        this.setVisible(false);
    }

    /**
     * 关闭窗口
     *
     * @param e
     */
    public void this_windowClosing(WindowEvent e) {
        if (SystemTray.isSupported()) {
            this.setVisible(false);
        } else {
            if (JOptionPane.showConfirmDialog(null, "确定退出程序？", "确认框", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                if (server != null) {
                    try {
                        JettyServer.stop(server);
                    } catch (Exception ex) {
                    }
                }
                JVMController.stop();
                System.exit(0);
            }
        }
    }
}

class WebAppFrame_this_windowAdapter extends WindowAdapter {

    private WebAppFrame adaptee;

    WebAppFrame_this_windowAdapter(WebAppFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        adaptee.this_windowOpened(e);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        adaptee.this_windowIconified(e);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}
