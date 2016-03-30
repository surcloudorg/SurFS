/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


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
     *
     * @param maxLn
     */
    public void setMaxLine(int maxLn) {
        this.maxLine = maxLn > 2000 ? 2000 : maxLn;
        this.maxLine = maxLn < 100 ? 100 : maxLn;
    }

    /**
     *
     * @return int
     */
    public int getMaxLine() {
        return maxLine;
    }


    private void removeHead() {
        int num = maxLine / 3;
        int pos = 0;
        for (int ii = 0; ii < num; ii++) {
            pos = pos + linelength.remove(0);
        }
        jTextArea.replaceRange("", 0, pos);
    }

    /**
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


    public void close() {
        mps.close();
    }
}
