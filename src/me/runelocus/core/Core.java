package me.runelocus.core;

import me.runelocus.config.Constants;
import me.runelocus.handlers.ProxyHandler;
import me.runelocus.threading.RuneLocusThread;

public class Core {

    private static ProxyHandler proxyHandler = new ProxyHandler(System.getProperty("user.home") + "\\Desktop\\proxies.txt");
    private static int voted = 0;

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 50; i++) {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
                Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
            }
            for (int i = 0; i < Constants.threadCount; i++) {
                final RuneLocusThread thread = new RuneLocusThread(Constants.headless, "Thread-" + i);
                thread.start();
                thread.setRunning(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getVoted() {
        return voted;
    }

    public static void setVoted(int voted) {
        Core.voted = voted;
    }

    public static ProxyHandler getProxyHandler() {
        return proxyHandler;
    }
}
