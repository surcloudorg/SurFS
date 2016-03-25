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
