package me.runelocus.handlers;

import me.runelocus.config.Constants;
import me.runelocus.config.DriverConfig;
import me.runelocus.threading.RuneLocusThread;
import me.runelocus.utils.Condition;
import me.runelocus.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class HandleLocus {
    private RuneLocusThread runeLocusThread;

    public HandleLocus(RuneLocusThread runeLocusThread) {
        this.runeLocusThread = runeLocusThread;
    }

    public boolean hasVotePage() {
        return getDriverConfig().pageHasText("You're voting for:");
    }

    public void vote(int attempt) {
        try {
            final Select select = new Select(getDriverConfig().findElement(By.name("countanswer")));
            final WebElement voteButton = getDriverConfig().findElement(By.name("vote"));
            if (select != null) {
                select.selectByVisibleText("1 item");
                Condition.sleep(250);
                Utils.takeScreenShot(getWebDriver(), "Select Answer - " + attempt + " - " + runeLocusThread.getThreadName());
                System.out.println("Screenshot taken of captcha with selected answer.");
            }
            if (voteButton != null) {
                voteButton.click();
                Condition.sleep(1000);
                Utils.takeScreenShot(getWebDriver(), "Vote Clicked - " + attempt + " - " + runeLocusThread.getThreadName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean alreadyVoted() {
        return getDriverConfig().pageHasText("have already voted for this server");
    }

    public boolean successfulVote() {
        return getDriverConfig().pageHasText("have recorded your");
    }

    public boolean failedVote() {
        return getDriverConfig().pageHasText("did not pass");
    }

    public void visitVotingPage() {
        if (!hasVotePage()) {
            getWebDriver().get(Constants.link);
        }
    }

    public long getTimeLeft() {
        final JavascriptExecutor jsExecutor = (JavascriptExecutor) getWebDriver();
        final Object val = jsExecutor.executeScript("return timeleft;");
        return (long) val;
    }

    private WebDriver getWebDriver() {
        return runeLocusThread.getWebDriver();
    }

    private DriverConfig getDriverConfig() {
        return runeLocusThread.getDriverConfig();
    }

}
