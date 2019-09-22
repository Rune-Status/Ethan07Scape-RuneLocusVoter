package me.runelocus.threading;

import me.runelocus.config.Constants;
import me.runelocus.config.DriverConfig;
import me.runelocus.core.Core;
import me.runelocus.handlers.HandleLocus;
import me.runelocus.utils.Condition;
import me.runelocus.utils.Utils;
import org.openqa.selenium.WebDriver;

public class RuneLocusThread extends Thread {
    private volatile boolean running = false;
    private DriverConfig driverConfig;
    private HandleLocus handleLocus;
    private int failed;
    private String threadName;
    private String proxyIP;
    private String proxyPort;

    public RuneLocusThread(boolean headless, String threadName) {
        this.threadName = threadName;
        if (Constants.useProxies) {
            grabValidProxy();
            this.driverConfig = new DriverConfig(headless, proxyIP, proxyPort);
        } else {
            this.driverConfig = new DriverConfig(headless);
        }
        this.handleLocus = new HandleLocus(this);
    }

    public synchronized void run() {
        try {
            while (isRunning()) {
                if (!handleLocus.hasVotePage()) {
                    if (handleLocus.successfulVote()) {
                        System.err.println("We successfully voted: " + getThreadName());
                        Utils.takeScreenShot(getWebDriver(), "Successful Vote - " + failed + " - " + getThreadName());
                        clearThread();
                        Core.setVoted(Core.getVoted() + 1);
                        System.err.println("Vote Count: " + Core.getVoted());
                        Condition.sleep(2500);
                    } else if (handleLocus.alreadyVoted()) {
                        System.err.println("We already voted on this IP: " + getThreadName());
                        clearThread();
                        Condition.sleep(2500);
                    } else if (handleLocus.failedVote()) {
                        System.err.println("FAILED VOTE: " + failed + " - " + getThreadName());
                        failed++;
                        System.out.println("Visiting Voting Page...");
                        handleLocus.visitVotingPage();
                    } else {
                        System.out.println("Visiting Voting Page...");
                        handleLocus.visitVotingPage();
                    }
                } else {
                    if (handleLocus.getTimeLeft() <= 0) {
                        System.out.println("Passed robot check, let's vote.");
                        handleLocus.vote(failed);
                    } else {
                        System.out.println("Waiting to pass anti-robot - stage: " + handleLocus.getTimeLeft());
                        Condition.sleep(1000);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void grabValidProxy() {
        final String proxy = Core.getProxyHandler().grabProxy();
        proxyIP = proxy.split(":")[0];
        proxyPort = proxy.split(":")[1];
        System.out.println("Set proxyIP: " + proxyIP);
        System.out.println("Set proxyPort: " + proxyPort);
        if (!Core.getProxyHandler().checkProxy(proxyIP, Integer.parseInt(proxyPort))) {
            Core.getProxyHandler().resetProxy(proxyIP, proxyPort);
            System.err.println("Proxy invalid, let's try another");
            grabValidProxy();
        }
    }

    public void clearThread() {
        System.out.println("We've finished voting, let's change proxies: " + getThreadName());
        Core.getProxyHandler().resetProxy(proxyIP, proxyPort);
        System.out.println("Stopping current thread: " + getThreadName());
        setRunning(false);
        getWebDriver().quit();
        driverConfig = null;
        System.out.println("Attempting to start a new thread: " + getThreadName());
        final RuneLocusThread thread = new RuneLocusThread(Constants.headless, getThreadName());
        thread.start();
        thread.setRunning(true);
        System.out.println("New thread has been started: " + getThreadName());
        Runtime.getRuntime().gc();
        System.gc();
        Condition.sleep(2500);
    }

    public WebDriver getWebDriver() {
        return this.driverConfig.getWebDriver();
    }

    public DriverConfig getDriverConfig() {
        return driverConfig;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getFailed() {
        return failed;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
