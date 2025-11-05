package com.toolbelt.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

public class BrowserFactory {
    public static Browser launchBrowser(Playwright playwright) {
        String browserName = System.getProperty("browser", "chromium").toLowerCase();

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();

        switch (browserName) {
            case "firefox":
                return playwright.firefox().launch(options);
            case "webkit":
                return playwright.webkit().launch(options);
            case "chromium":
            default:
                return playwright.chromium().launch(options);
        }
    }
}
