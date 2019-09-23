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
    private int voteCount;
    private long startTime;
    private String threadName;
    private String status;
    private String proxyIP;
    private String proxyPort;

    public RuneLocusThread(boolean headless, String threadName) {
        this.threadName = threadName;
        setupThread(headless);
    }

    public synchronized void run() {
        try {
            while (isRunning()) {
                if (!handleLocus.hasVotePage()) {
                    if (handleLocus.successfulVote()) {
                        System.err.println("We successfully voted: " + getThreadName());
                        Utils.takeScreenShot(getWebDriver(), "Successful Vote - " + failed + " - " + getThreadName());
                        resetThread();
                        voteCount++;
                        System.err.println("Vote Count: " + voteCount);
                        Condition.sleep(2500);
                    } else if (handleLocus.alreadyVoted()) {
                        System.err.println("We already voted on this IP: " + getThreadName());
                        resetThread();
                        Condition.sleep(2500);
                        failed++;
                    } else if (handleLocus.failedVote()) {
                        System.err.println("FAILED VOTE: " + failed + " - " + getThreadName());
                        failed++;
                        setStatus("Visiting Voting Page...");
                        handleLocus.visitVotingPage();
                    } else {
                        setStatus("Visiting Voting Page...");
                        handleLocus.visitVotingPage();
                    }
                } else {
                    if (handleLocus.getTimeLeft() <= 0) {
                        setStatus("Passed robot check, let's vote.");
                        handleLocus.vote(failed);
                    } else {
                        setStatus("Waiting to pass anti-robot - stage: " + handleLocus.getTimeLeft());
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
        setStatus("Set proxyIP: " + proxyIP);
        setStatus("Set proxyPort: " + proxyPort);
        if (!Core.getProxyHandler().checkProxy(proxyIP, Integer.parseInt(proxyPort))) {
            Core.getProxyHandler().resetProxy(proxyIP, proxyPort);
            setStatus("Proxy invalid, let's try another");
            grabValidProxy();
        }
    }

    public void resetThread() {
        setStatus("We've finished voting, let's change proxies: " + getThreadName());
        Core.getProxyHandler().resetProxy(proxyIP, proxyPort);
        setStatus("pausing current thread: " + getThreadName());
        setRunning(false);
        setStatus("Exiting out of browser.");
        getWebDriver().quit();
        setStatus("nulling out variables.");
        driverConfig = null;
        handleLocus = null;
        Runtime.getRuntime().gc();
        System.gc();
        setupThread(Constants.headless);
        setStatus("Starting up thread again.");
        setRunning(true);
        run();
        Condition.sleep(1250);
    }

    private void setupThread(boolean headless) {
        setStatus("Setting up the thread.");
        if (Constants.useProxies) {
            grabValidProxy();
            this.driverConfig = new DriverConfig(headless, proxyIP, proxyPort);
        } else {
            this.driverConfig = new DriverConfig(headless);
        }
        this.handleLocus = new HandleLocus(this);
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

    public int getVoteCount() {
        return voteCount;
    }

    public long getStartTime() {
        return startTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        System.out.println(status);
        this.status = status;
    }
}
