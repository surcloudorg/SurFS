package com.autumn.core.log;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <p>Title: 类似控制台输出的JPanel</p>
 *
 * <p>Description: 可设置界面最多显示行数，类似控制台信息翻滚的UI组件</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class TextConsole extends JPanel {

    private static final long serialVersionUID = 20120701100000L;
    private BorderLayout borderLayout = new BorderLayout();
    private final JTextArea jTextArea = new JTextArea();
    private JScrollPane jScrollPane = new JScrollPane();
    private int maxLine = 500;
    private TextPrinter mps = null;
    private List<Integer> linelength = new ArrayList<Integer>();

    public TextConsole() {
        this.setLayout(borderLayout);
        jTextArea.setToolTipText("");
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        this.setToolTipText("");
        this.add(jScrollPane, BorderLayout.CENTER);
        jScrollPane.getViewport().add(jTextArea, null);
        mps = new TextPrinter(System.out, this);
        System.setOut(mps);
    }

    /**
     * 文本框最大行数
     *
     * @param maxLn
     */
    public void setMaxLine(int maxLn) {
        this.maxLine = maxLn > 2000 ? 2000 : maxLn;
        this.maxLine = maxLn < 100 ? 100 : maxLn;
    }

    /**
     * 文本框最大行数
     *
     * @return int
     */
    public int getMaxLine() {
        return maxLine;
    }

    /**
     * 删除超长行
     */
    private void removeHead() {
        int num = maxLine / 3;
        int pos = 0;
        for (int ii = 0; ii < num; ii++) {
            pos = pos + linelength.remove(0);
        }
        jTextArea.replaceRange("", 0, pos);
    }

    /**
     * 输出
     *
     * @param strMsg
     */
    private void println(String strMsg) {
        synchronized (jTextArea) {
            if (linelength.size() >= maxLine) {
                removeHead();
            }
            int len = strMsg.length();
            jTextArea.append(strMsg);
            if (!strMsg.endsWith("\r\n")) {
                jTextArea.append("\r\n");
                len = len + 2;
            }
            linelength.add(len);
            jTextArea.setCaretPosition(jTextArea.getText().length());
        }
    }

    /**
     * 显示信息
     *
     * @param strMsg
     */
    public void append(String strMsg) {
        StringTokenizer st = new StringTokenizer(strMsg, "\r\n");
        while (st.hasMoreTokens()) {
            String ss = st.nextToken();
            println(ss);
        }
    }

    /**
     * 关闭
     */
    public void close() {
        mps.close();
    }
}
