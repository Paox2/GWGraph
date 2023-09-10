package crawler.util;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Driver {

    public ChromeDriver driverSetUp(BrowserMobProxy proxy) {
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("incognito");
        options.addArguments("--headless");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-extensions");
//        options.setCapability("goog:loggingPrefs", java.util.logging.Level.OFF);
        options.setCapability("acceptInsecureCerts", true);
        options.setCapability("proxy", seleniumProxy);


        ChromeDriver driver = new ChromeDriver(options);
        return driver;
    }
}
