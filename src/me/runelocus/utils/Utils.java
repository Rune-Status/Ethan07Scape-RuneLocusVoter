package me.runelocus.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static void takeScreenShot(WebDriver webDriver, String name) {
        try {
            webDriver.manage().window().maximize();
            File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            Path source = Paths.get(screenshot.getAbsolutePath());
            Path newDir = Paths.get("C:\\Users\\Ethan\\Desktop\\Drivers\\");
            Files.move(source, newDir.resolve(name + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
