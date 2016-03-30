/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.surfs.nas.log;

import java.io.OutputStream;
import java.io.PrintStream;


public class TextPrinter extends PrintStream {

    private TextConsole text = null;
    private Logger logger = null;


    protected TextPrinter(OutputStream out, TextConsole text) {
        super(out);
        this.text = text;
    }


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
