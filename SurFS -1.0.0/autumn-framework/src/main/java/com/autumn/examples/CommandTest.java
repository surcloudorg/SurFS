/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.autumn.examples;

import com.autumn.util.ConsoleCommand;

public class CommandTest extends Thread {

    ConsoleCommand command = null;

    public void terminate() {
        command.stop();
    }

    @Override
    public void run() {
        command = new ConsoleCommand("nslookup sads");
        try {
            Integer res = command.call();
            System.out.println("returnCode="+res);
        } catch (Exception ex) {
        }
    }
}
