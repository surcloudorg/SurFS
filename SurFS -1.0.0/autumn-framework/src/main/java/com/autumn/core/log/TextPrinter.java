package com.autumn.core.log;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * <p>Title: 将对输出流重定向</p>
 *
 * <p>Description: 主要用途将System.err重定向到一个Logger，将System.out重定向到TextConsole</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public class TextPrinter extends PrintStream {

    private TextConsole text = null;
    private Logger logger = null;

    //重定向到Jtext控件
    protected TextPrinter(OutputStream out, TextConsole text) {
        super(out);
        this.text = text;
    }

    //重定向到Logger
    public TextPrinter(OutputStream out, Logger log) {
        super(out);
        this.logger = log;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        String message = new String(buf, off, len);
        if (logger != null) {
            logger.error(message);
        }
        if (text != null) {
            text.append(message);
        }
    }
}
