package com.toolbelt.pages;

import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void navigateToHome() {
        page.click("a[href='/']");
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public void setMobileViewport() {
        page.setViewportSize(375, 667);
    }
}
