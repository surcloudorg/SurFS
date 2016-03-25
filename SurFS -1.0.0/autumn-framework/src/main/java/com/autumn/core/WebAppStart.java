package com.autumn.core;

import com.autumn.util.MainArgs;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * <p>Title: 启动JETTY</p>
 *
 * <p>Description: 启动JETTY（with windows）</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 */
public class WebAppStart {

    public static int port = 0;
    public static String webapp = "web";
    boolean packFrame = false;

    /**
     * Construct and show the application.
     */
    public WebAppStart() {
        WebAppFrame frame = new WebAppFrame();
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }
        Thread t = new Thread(frame);
        t.start();
        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }

    /**
     * Application entry point.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        try {
            JVMController.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        MainArgs param = new MainArgs(args);
        //获取服务端口
        port = param.getInt("port", 8080);
        webapp = param.getString("webapp", "web");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception exception) {
                }
                WebAppStart webAppStart = new WebAppStart();
            }
        });
    }
}
