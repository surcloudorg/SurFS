/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.core.DataCommand;
import com.autumn.core.tcp.ConnectInfoEvent;
import com.autumn.core.tcp.ConnectInfoListener;
import com.autumn.core.tcp.ReceiveEvent;
import com.autumn.core.tcp.ReceiveListener;
import com.autumn.core.tcp.StopEvent;
import com.autumn.core.tcp.StopListener;
import com.autumn.core.tcp.TcpConnect;

public class TcpServerTest {

    static TcpConnect tcpconnect = new TcpConnect();

    public static void main(String[] args) throws Exception {
        tcpconnect.setHost("");
        tcpconnect.setPort(8020);
        tcpconnect.setPassiveMode(true);//服务端
        tcpconnect.setUserName("lsaner");
        tcpconnect.setPassWord("781105");
        tcpconnect.setRecdThread(2);
        MyListener listener = new MyListener();
        tcpconnect.addConnectInfoListener(listener);
        tcpconnect.addReceiveListener(listener);
        tcpconnect.addStopListener(listener);
        tcpconnect.start();//启动服务端

        MySender sender = new MySender();
        sender.start();

        System.in.read();
        sender.interrupt();

        tcpconnect.stop();
    }

    static class MySender extends Thread {

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                DataCommand cmd = new DataCommand("servercmd ");
                cmd.AddNewItem("param1", "param1value");
                cmd.AddNewItem("param2", "param2value");
                try {
                    sleep(5000);
                    tcpconnect.send(cmd);
                } catch (InterruptedException ex) {
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    static class MyListener implements ConnectInfoListener, ReceiveListener, StopListener {

        public void onConnectInfo(ConnectInfoEvent event) {
            //System.out.println(event.getStateCode() + ":" + event.getDescription());
        }

        public void onReceive(ReceiveEvent event) {
            // System.out.println("收到:" + event.getCommand().getDispCommand());
        }

        public void onStop(StopEvent event) {
           // System.out.println("收到未处理数据:" + event.getRecData().size());
            // System.out.println("未发送出去数据:" + event.getSndData().size());
        }

    }
}
