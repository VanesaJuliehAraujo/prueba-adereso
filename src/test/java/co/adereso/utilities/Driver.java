package co.adereso.utilities;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;


public class Driver {

    private static Playwright playwright;
    private static Browser browser;
    public static Page page;

    public static void initializeDriver() {
        if (playwright == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
                            .setSlowMo(500)
            );
            page = browser.newPage();
        }
    }

    public static void closeDriver() {
        if (page != null) {
            page.close();
            page = null;
        }

        if (browser != null) {
            browser.close();
            browser = null;
        }

        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}