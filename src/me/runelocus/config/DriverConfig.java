package me.runelocus.config;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DriverConfig {

    private WebDriver webDriver;
    private String exePath = System.getProperty("user.home") + "\\Desktop\\Drivers" + File.separator;

    public DriverConfig(boolean headless) {
        if (headless) {
            webDriver = getPhantonJS();
        } else {
            webDriver = getChromeDriver();
        }
    }

    public DriverConfig(boolean headless, String proxyIP, String proxyPort) {
        if (headless) {
            webDriver = getPhantonJS(proxyIP, proxyPort);
        } else {
            webDriver = getChromeDriver(proxyIP, proxyPort);
        }
    }

    public WebDriver getPhantonJS() {
        String[] cli_args = new String[]{"--load-images=true", "--load-images=yes", "--ssl-protocol=any", "--ignore-ssl-errors=true", "--web-security=false", "--webdriver-loglevel=NONE"};
        File file = new File(exePath + "phantomjs.exe");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cli_args);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                file.getAbsolutePath());
        caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
        return new PhantomJSDriver(caps);
    }

    public WebDriver getPhantonJS(String proxyIP, String proxyPort) {
        String[] cli_args = new String[]{"--proxy=" + proxyIP + ":" + proxyPort, "--proxy-type=http", "--ssl-protocol=any", "--ignore-ssl-errors=true", "--web-security=false", "--webdriver-loglevel=NONE"};
        File file = new File(exePath + "phantomjs.exe");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setJavascriptEnabled(true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cli_args);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                file.getAbsolutePath());
        caps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
        return new PhantomJSDriver(caps);
    }

    private WebDriver getChromeDriver(String proxyIP, String proxyPort) {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        options.addArguments("--proxy-server=http://" + proxyIP + ":" + proxyPort);
        options.addArguments("--blink-settings=imagesEnabled=false");
        prefs.put("profile.managed_default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);
        System.setProperty("webdriver.chrome.driver", exePath + "chromedriver.exe");
        return new ChromeDriver(options);
    }

    private WebDriver getChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<>();
        options.addArguments("--blink-settings=imagesEnabled=false");
        prefs.put("profile.managed_default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);
        System.setProperty("webdriver.chrome.driver", exePath + "chromedriver.exe");
        return new ChromeDriver(options);
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public boolean pageHasText(String text) {
        return getWebDriver().getPageSource().contains(text);
    }

    public boolean pageTitleContains(String text) {
        return getWebDriver().getTitle().contains(text);
    }

    public WebElement findElement(By by) {
        try {
            return getWebDriver().findElement(by);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasCloudFlare() {
        if (pageTitleContains("Attention Required!")) {
            return findElement(By.id("g-recaptcha-response")) != null;
        }
        return false;
    }

    /*public boolean handleCloudfareCaptcha() {
        try {
            if (hasCloudFlare()) {
                final JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
                final TwoCaptchaService service = new TwoCaptchaService(Constants.captchaApiKey, Constants.googleCaptchaKey, webDriver.getCurrentUrl());
                final String response = service.solveCaptcha();
                jsExecutor.executeScript("document.getElementById('g-recaptcha-response').value=\"" + response + "\";");
                Condition.sleep(1500);
                final WebElement vote = findElement(By.id("recaptcha_submit"));
                if (vote != null) {
                    vote.click();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/

}
