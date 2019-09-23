package me.runelocus.core;

import me.runelocus.config.Constants;
import me.runelocus.handlers.ProxyHandler;
import me.runelocus.threading.RuneLocusThread;
import me.runelocus.ui.MainUI;

import java.util.ArrayList;
import java.util.List;


public class Core {

    private static ProxyHandler proxyHandler = new ProxyHandler(System.getProperty("user.home") + "\\Desktop\\proxies.txt");

    public static void main(String[] args) {
        final MainUI ui = new MainUI();
        try {
            System.out.println("Closing all driver processes.");
            for (int i = 0; i < 50; i++) {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
            }
            System.out.println("Attempted to start "+  Constants.threadCount +" voting threads.");
            for (int i = 0; i < Constants.threadCount; i++) {
                final RuneLocusThread thread = new RuneLocusThread(Constants.headless, "Thread-"+i);
                ui.getThreadList().add(thread);
                thread.start();
                thread.setRunning(true);
            }
            System.out.println("Voting threads has been started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ProxyHandler getProxyHandler() {
        return proxyHandler;
    }
}
