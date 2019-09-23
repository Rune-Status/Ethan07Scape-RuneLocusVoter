package me.runelocus.ui;

import me.runelocus.threading.RuneLocusThread;

public class ThreadFrame {

    public RuneLocusThread instance;
    public int voted;
    public int failed;
    public long startTime;
    public String threadName;
    public String status;

    public ThreadFrame(RuneLocusThread instance, int voted, int failed, long startTime, String threadName, String status) {
        this.instance = instance;
        this.voted = voted;
        this.failed = failed;
        this.startTime = startTime;
        this.threadName = threadName;
        this.status = status;
    }

    public RuneLocusThread getInstance() {
        return instance;
    }

    public int getVoted() {
        return voted;
    }

    public int getFailed() {
        return failed;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getStatus() {
        return status;
    }
}
